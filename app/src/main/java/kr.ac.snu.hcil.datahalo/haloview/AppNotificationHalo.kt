package kr.ac.snu.hcil.datahalo.haloview

import android.content.Context
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotificationType
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisEffect
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractAggregatedVisEffect
import kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisObject

class AppNotificationHalo(context: Context, attributeSet: AttributeSet? = null)
    : ConstraintLayout(context, attributeSet){

    companion object {
        const val TAG = "APP_NOTIFICATION_HALO"
    }

    private var visConfig: AppHaloConfig? = null
    private var appPackageName: String = "Not Initialized"

    private val pivotViewID: Int

    private val currentIndependentNotificationIDs: MutableList<Int> = mutableListOf()
    private val currentAggregatedNotificationIDs : MutableList<Int> = mutableListOf()

    private var currentIndependentVisEffectName: String? = null
    private val currentIndependentNotiVisLayoutInfos: MutableMap<Int, LayoutParams> = mutableMapOf()
    private val currentIndependentVisEffects: MutableMap<Int, AbstractIndependentVisEffect> = mutableMapOf()

    private var currentAggregatedVisEffectName: String? = null
    private var currentAggregatedVisEffect: AbstractAggregatedVisEffect? = null
    private var currentAggregatedNotisVisLayoutInfo: Pair<List<LayoutParams>, List<LayoutParams>>? = null

    init{
        clipChildren = false
        clipToPadding = false
        clipToOutline = false

        id = View.generateViewId()

        pivotViewID = View.generateViewId()

        val pivotView = ImageView(context).also{
            it.id = pivotViewID
            it.setBackgroundColor(Color.RED)
        }

        addView(pivotView)

        ConstraintSet().apply{
            clone(this@AppNotificationHalo)

            constrainHeight(pivotView.id, 10)
            constrainWidth(pivotView.id, 10)

            connect(pivotView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(pivotView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
            connect(pivotView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            connect(pivotView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)

            applyTo(this@AppNotificationHalo)
        }
    }

    /* 필요 기능
     *  1) independent, aggregated Notification ID 관리
     *  2) independent, aggregated Notification과 매핑되는 visEffect를 관리
     *  3) independent, aggregated VisEffect의 위치 관리
     *  4) independent, aggregated VisEffect에 공히 적용되는 animation 효과
     *  5) App Notification Data 들어오면 각 시각화에 전파
     */

    fun getAppPackageName(): String = appPackageName

    fun setVisConfig(appHaloConfig: AppHaloConfig){

        //TODO(만약 visConfig에서 layoutmethod, independent effect, aggregated effect의 이름이 바뀌었다면, 현재 존재하는 VisEffect도 치환해야함
        //current visConfig

        val replaceLayoutMethod = visConfig?.let{currentConfig -> currentConfig.haloLayoutMethodName != appHaloConfig.haloLayoutMethodName}?: true
        val replaceIndependentVisEffect = currentIndependentVisEffectName?.let{ it != appHaloConfig.independentVisEffectName} ?: true
        val replaceAggregatedVisEffect = currentAggregatedVisEffectName?.let{ it != appHaloConfig.aggregatedVisEffectName} ?: true

        currentIndependentVisEffectName = appHaloConfig.independentVisEffectName
        currentAggregatedVisEffectName = appHaloConfig.aggregatedVisEffectName

        visConfig = appHaloConfig
        appPackageName = appHaloConfig.packageName

        //parameter updates
        currentIndependentNotiVisLayoutInfos.forEach{
            //TODO()
        }

        if(replaceIndependentVisEffect){
            currentIndependentVisEffects.keys.forEach{ id ->
                currentIndependentVisEffects[id]?.deleteVisObjectsInLayout(this)
                currentIndependentVisEffects[id] = VisEffectManager.createNewIndependentVisEffect(appHaloConfig.independentVisEffectName, appHaloConfig)
            }
        }

        currentIndependentVisEffects.values.forEach{ effect ->
            effect.visualParameters = appHaloConfig.independentVisEffectVisParams
            effect.independentVisObjects.forEachIndexed{ index, visObj ->
                visObj.setVisParams(appHaloConfig.independentVisualParameters[index])
                visObj.setDataParams(appHaloConfig.independentDataParameters[index])
                visObj.setAnimParams(*appHaloConfig.independentAnimationParameters[index].toTypedArray())
                visObj.setVisMapping(appHaloConfig.independentVisualMappings[index])
            }
        }

        if(replaceAggregatedVisEffect){
            currentAggregatedVisEffect?.deleteVisObjectsInLayout(this)
            currentAggregatedVisEffect = VisEffectManager.createNewAggregatedVisEffect(appHaloConfig.aggregatedVisEffectName, appHaloConfig)
        }

        currentAggregatedVisEffect?.apply{
            objVisualParameters = appHaloConfig.aggregatedVisualParameters
            objDataParameters = appHaloConfig.aggregatedDataParameters
            objAnimationParameters = appHaloConfig.aggregatedAnimationParameters
            setVisMapping(appHaloConfig.aggregatedVisualMappings)
        } ?: run{
            currentAggregatedVisEffect = VisEffectManager.createNewAggregatedVisEffect(appHaloConfig.aggregatedVisEffectName, appHaloConfig).apply{
                objVisualParameters = appHaloConfig.aggregatedVisualParameters
                objDataParameters = appHaloConfig.aggregatedDataParameters
                objAnimationParameters = appHaloConfig.aggregatedAnimationParameters
                setVisMapping(appHaloConfig.aggregatedVisualMappings)
            }
        }
    }

    fun setAppHaloData(enhancedAppNotifications: EnhancedAppNotifications){

        if(enhancedAppNotifications.packageName != appPackageName)
            return

        visConfig?.let{config ->
            val result = VisDataManager.convert(enhancedAppNotifications, config)
            val independentNotis = result[NotificationType.INDEPENDENT]?: emptyList()
            val aggregatedNotis = result[NotificationType.AGGREGATED]?: emptyList()

            //0. Delete Phase
            currentIndependentNotificationIDs.filter{currentlyValidID -> currentlyValidID !in independentNotis.map{it.id}}.map{ expiredID ->
                val visEffect = currentIndependentVisEffects[expiredID]!!
                visEffect.deleteVisObjectsInLayout(this)

                currentIndependentNotificationIDs.remove(expiredID)
                currentIndependentNotiVisLayoutInfos.remove(expiredID)
                currentIndependentVisEffects.remove(expiredID)
            }

            currentAggregatedNotificationIDs.filter{currentlyValidID -> currentlyValidID !in aggregatedNotis.map{it.id}}.map{ expiredID ->
                currentAggregatedNotificationIDs.remove(expiredID)
            }

            // 1. Data Update Phase

            //1-1. update (기존에 존재하는 애들을 업데이트 하는 방식임
            val updateIndependent = independentNotis.filter{it.id in currentIndependentNotificationIDs}
            updateIndependent.map{ enhancedNotification ->
                currentIndependentVisEffects[enhancedNotification.id]?.let{ visEffect ->
                    visEffect.setEnhancedNotification(enhancedNotification)
                }
            }

            //1-2. add
            val addIndependent = independentNotis.filterNot{it.id in currentIndependentNotificationIDs}
            addIndependent.map{ enhancedNotification ->
                val visEffect = VisEffectManager.createNewIndependentVisEffect(config.independentVisEffectName, config)
                currentIndependentNotificationIDs.add(enhancedNotification.id)
                currentIndependentVisEffects[enhancedNotification.id] = visEffect
                visEffect.setEnhancedNotification(enhancedNotification)
            }

            val addAggregated = aggregatedNotis.filterNot{it.id in currentAggregatedNotificationIDs}
            addAggregated.map{ enhancedNotification ->
                currentAggregatedNotificationIDs.add(enhancedNotification.id)
            }

            currentAggregatedVisEffect?.apply{
                setEnhancedNotification(aggregatedNotis)
            } ?: run{
                currentAggregatedVisEffect = VisEffectManager.createNewAggregatedVisEffect(config.aggregatedVisEffectName, config).apply{
                    setEnhancedNotification(aggregatedNotis)
                }
            }

            val (independentVisLayoutMap, aggregatedVisLayoutPair) = AppHaloLayoutMethods
                    .getLayout(config)
                    .generateLayoutParams(this, pivotViewID, independentNotis, currentIndependentVisEffects.toMap(), aggregatedNotis, currentAggregatedVisEffect)

            // 2. Layout Update Phase
            currentIndependentVisEffects.forEach{ idToEffect ->
                val notiID = idToEffect.key
                val visEffect = idToEffect.value

                independentVisLayoutMap[notiID]?.let{ layout ->
                    currentIndependentNotiVisLayoutInfos[notiID] = layout
                    visEffect.placeVisObjectsInLayout(this, layout)
                }
            }


            currentAggregatedNotisVisLayoutInfo = aggregatedVisLayoutPair
            currentAggregatedVisEffect?.placeVisObjectsInLayout(this, aggregatedVisLayoutPair)

        }
    }
}