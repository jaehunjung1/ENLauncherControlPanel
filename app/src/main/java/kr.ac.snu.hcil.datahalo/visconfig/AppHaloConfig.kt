package kr.ac.snu.hcil.datahalo.visconfig

import android.animation.AnimatorSet
import android.graphics.Color
import android.view.View
import android.view.animation.LinearInterpolator
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.notificationdata.NotiHierarchy

data class AppHaloConfig(val packageName: String){
    companion object {
        fun applyLayoutConfig(appHaloConfig: AppHaloConfig, name: String): AppHaloConfig{
            return when(name){
                "ClockwiseSortedLayout" -> {
                    appHaloConfig.apply{
                        haloLayoutMethodName = name
                    }
                }
                else -> {appHaloConfig}
            }
        }

        fun applyIndependentEffectConfig(appHaloConfig: AppHaloConfig, name: String): AppHaloConfig{
            return when(name){
                "SingleVisObjIndependentEffect" -> {
                    appHaloConfig.apply{
                        independentVisEffectName = name
                        independentVisEffectVisParams = listOf(
                                IndependentVisEffectVisParams(
                                        radius = mutableListOf(0),
                                        offsetAngle = 0f)
                        )
                        independentVisualMappings = listOf(
                                mapOf(
                                        NuNotiVisVariable.POSITION to NotiProperty.IMPORTANCE,
                                        NuNotiVisVariable.SIZE to NotiProperty.IMPORTANCE,
                                        NuNotiVisVariable.SHAPE to NotiProperty.LIFE_STAGE,
                                        NuNotiVisVariable.MOTION to NotiProperty.LIFE_STAGE,
                                        NuNotiVisVariable.COLOR to NotiProperty.CONTENT
                                )
                        )
                        independentDataParameters = listOf(
                                IndependentVisObjectDataParams()
                        )
                        independentVisualParameters = listOf(
                                IndependentVisObjectVisParams()
                        )
                        independentAnimationParameters = listOf(
                                listOf(
                                        IndependentVisObjectAnimParams(
                                                property = View.ALPHA,
                                                values = arrayOf(.5f),
                                                duration = 2 * 1000L,
                                                interpolator = LinearInterpolator())
                                )
                        )
                    }
                }
                else -> {appHaloConfig}
            }
        }

        fun applyAggregatedEffectConfig(appHaloConfig: AppHaloConfig, name: String): AppHaloConfig{
            return when(name){
                "SimpleAggregatedEffect" -> {
                    appHaloConfig.apply{
                        aggregatedVisEffectName = name

                    }
                }
                else -> {appHaloConfig}
            }
        }
    }

    //Filter & Sample Methods Configs
    var filterImportanceConfig = mapOf(
            WGBFilterVar.ACTIVE to true,
            WGBFilterVar.WHITE_COND to 0.5,
            WGBFilterVar.BLACK_COND to 0.0
    )
    var filterObservationWindowConfig = mapOf(
            WGBFilterVar.ACTIVE to true,
            WGBFilterVar.WHITE_COND to 60 * 60 * 1000L,
            WGBFilterVar.BLACK_COND to 6 * 60 * 60 * 1000L
    )
    var filterChannelConfig = mapOf(
            WGBFilterVar.ACTIVE to false,
            WGBFilterVar.WHITE_COND to setOf<NotiHierarchy>(),
            WGBFilterVar.BLACK_COND to setOf<NotiHierarchy>()
    )
    var filterKeywordConfig = mapOf(
            WGBFilterVar.ACTIVE to true,
            WGBFilterVar.WHITE_COND to setOf<String>(),
            WGBFilterVar.BLACK_COND to setOf<String>()
    )
    var maxNumOfIndependentNotifications: Int = 3

    //AppHalo Layout & Visualization Methods
    var haloLayoutMethodName:String = AppHaloLayoutMethods.availiableLayouts[0]
    var independentVisEffectName: String = VisEffectManager.availableIndependentVisEffects[0]
    var aggregatedVisEffectName: String = VisEffectManager.availableAggregatedVisEffects[0]

    // 사용자 인풋으로 받고 아닌 경우에는 기본 설정으로 가야겠죠?

    var independentVisEffectVisParams: List<IndependentVisEffectVisParams> = listOf(
            IndependentVisEffectVisParams(
                    radius = mutableListOf(0, 0, 0, 0, 0),
                    offsetAngle = 0f
            )
    )

    var independentVisualMappings: List<Map<NuNotiVisVariable, NotiProperty?>> = listOf(
            mapOf(
                    NuNotiVisVariable.POSITION to NotiProperty.IMPORTANCE,
                    NuNotiVisVariable.SIZE to NotiProperty.IMPORTANCE,
                    NuNotiVisVariable.SHAPE to NotiProperty.CONTENT,
                    NuNotiVisVariable.MOTION to NotiProperty.LIFE_STAGE,
                    NuNotiVisVariable.COLOR to NotiProperty.LIFE_STAGE
            )
    )

    var independentVisualParameters: List<IndependentVisObjectVisParams> = listOf(
            IndependentVisObjectVisParams().also{
                it.selectedShapeList = mutableListOf(

                )
                it.selectedMotionList = mutableListOf(
                        AnimatorSet(),
                        AnimatorSet(),
                        AnimatorSet(),
                        AnimatorSet(),
                        AnimatorSet()
                )
                it.selectedColorList = mutableListOf(
                        Color.RED,
                        Color.YELLOW,
                        Color.GREEN,
                        Color.BLUE,
                        Color.DKGRAY
                )

            }
    )

    var independentDataParameters: List<IndependentVisObjectDataParams> = listOf(
            IndependentVisObjectDataParams()
    )

    var independentAnimationParameters: List<List<IndependentVisObjectAnimParams>> = listOf(
            listOf(
                    IndependentVisObjectAnimParams(
                            property = View.ALPHA,
                            values = arrayOf(.5f),
                            duration = 2 * 1000L,
                            interpolator = LinearInterpolator())
            )
    )

}