package kr.ac.snu.hcil.datahalo.haloview

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.AttributeSet
import android.widget.ImageView
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications
import kr.ac.snu.hcil.datahalo.utils.ANHComponentUIDGenerator
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotificationType
import kr.ac.snu.hcil.datahalo.manager.VisDataConverter
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisEffect
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractAggregatedVisEffect

class AppNotificationHalo(
        val appPackageName: String,
        private var visConfig: AppHaloConfig,
        context: Context, attributeSet: AttributeSet?)
    : ConstraintLayout(context, attributeSet){

    companion object {
        const val TAG = "APP_NOTIFICATION_HALO"
    }

    init{
        clipChildren = false
        //set Visualization local view들과 layout을 결정할 pivotView를 하나 center에 생성

        val pivotView = ImageView(this.context).also{
            it.id = ANHComponentUIDGenerator.GLOBAL_PIVOT
            it.setBackgroundColor(Color.RED)
            it.layoutParams = LayoutParams(5, 5)
        }

        addView(pivotView)

        ConstraintSet().also{
            it.clone(this)

            it.connect(pivotView.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
            it.connect(pivotView.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT)
            it.connect(pivotView.id, ConstraintSet.BOTTOM, this.id, ConstraintSet.BOTTOM)
            it.connect(pivotView.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT)

            it.applyTo(this)
        }
    }

    /* 필요 기능
     *  1) independent, aggregated Notification ID 관리
     *  2) independent, aggregated Notification과 매핑되는 visEffect를 관리
     *  3) independent, aggregated VisEffect의 위치 관리
     *  4) independent, aggregated VisEffect에 공히 적용되는 animation 효과
     *  5) App Notification Data 들어오면 각 시각화에 전파
     */

    private val currentIndependentNotificationIDs: MutableList<Int> = mutableListOf()
    //private val currentAggregatedNotificationIDs : MutableList<Int> = mutableListOf()
    private val currentIndependentNotiVisLayoutInfos: MutableMap<Int, LayoutParams> = mutableMapOf()
    private val currentIndependentVisEffects: MutableMap<Int, AbstractIndependentVisEffect> = mutableMapOf()
    private  var currentAggregatedVisEffect: AbstractAggregatedVisEffect? = null


    fun setVisConfig(appHaloConfig: AppHaloConfig){
        visConfig = appHaloConfig
    }

    fun setAppHaloData(enhancedAppNotifications: EnhancedAppNotifications){
        //관련 없는 app의 notiData면 끝
        if(enhancedAppNotifications.packageName != appPackageName)
            return

        //Independent한 것과 Aggregate할 것 구분
        val result = VisDataConverter.convert(enhancedAppNotifications, visConfig)
        val independentNotis = result[NotificationType.INDEPENDENT]?: emptyList()
        val aggregatedNotis = result[NotificationType.AGGREGATED]?: emptyList()

        //update, add, delete 필요함

        //0. Delete Phase
        currentIndependentNotificationIDs.filter{currentlyValidID -> currentlyValidID !in independentNotis.map{it.id}}.map{ expiredID ->
            val visEffect = currentIndependentVisEffects[expiredID]!!
            visEffect.deleteVisObjectsInLayout(this)

            currentIndependentNotificationIDs.remove(expiredID)
            currentIndependentNotiVisLayoutInfos.remove(expiredID)
            currentIndependentVisEffects.remove(expiredID)
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
            val visEffect = VisEffectManager.createNewIndependentVisEffect(visConfig.independentVisEffectName, visConfig)

            currentIndependentNotificationIDs.add(enhancedNotification.id)
            currentIndependentVisEffects[enhancedNotification.id] = visEffect
            visEffect.setEnhancedNotification(enhancedNotification)
        }

        val (independentVisLayoutMap, aggregatedVisLayout) = AppHaloLayoutMethods.getLayout(visConfig)
                .generateLayoutParams(this, independentNotis, currentIndependentVisEffects.toMap(), aggregatedNotis, currentAggregatedVisEffect)


        // 2. Layout Update Phase
        currentIndependentVisEffects.forEach{ idToEffect ->
            val notiID = idToEffect.key
            val visEffect = idToEffect.value

            independentVisLayoutMap[notiID]?.let{ layout ->
                currentIndependentNotiVisLayoutInfos[notiID] = layout
                visEffect.placeVisObjectsInLayout(this, layout)
            }
        }

        //TODO(aggregated effect)
        currentAggregatedVisEffect.let{

        }
    }

}