package kr.ac.snu.hcil.datahalo.ui.viewmodel

import android.animation.AnimatorSet
import androidx.lifecycle.*
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.View
import android.view.animation.LinearInterpolator
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.visconfig.*
import kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisMappingRule
import kr.ac.snu.hcil.datahalo.visualEffects.NewVisShape
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

class AppHaloConfigViewModel: ViewModel() {
    companion object{
        const val TAG = "APP_HALO_CONFIG_VIEWMODEL"
        const val SAMPLE_PACKAGE_NAME = "kr.ac.snu.hcil.datahalo.sample"
    }

    val appHaloConfigLiveData: MutableLiveData<AppHaloConfig> = MutableLiveData()

    init{
        appHaloConfigLiveData.value = AppHaloConfig(SAMPLE_PACKAGE_NAME).apply{

            haloLayoutMethodName = AppHaloLayoutMethods.availiableLayouts[0]
            independentVisEffectName = VisEffectManager.availableIndependentVisEffects[0]
            aggregatedVisEffectName = VisEffectManager.availableAggregatedVisEffects[0]

            independentVisEffectVisParams = IndependentVisEffectVisParams(
                    radius = mutableListOf(0, 0, 0, 0, 0),
                    offsetAngle = 0f
            )

            independentVisualMappings.add(
                    mapOf(
                        NuNotiVisVariable.POSITION to null,
                        NuNotiVisVariable.SIZE to null,
                        NuNotiVisVariable.SHAPE to NotiProperty.CONTENT,
                        NuNotiVisVariable.MOTION to NotiProperty.LIFE_STAGE,
                        NuNotiVisVariable.COLOR to NotiProperty.IMPORTANCE
                    )
            )

            independentVisualParameters.add(
                    IndependentVisObjectVisParams().also{
                        it.selectedShapeList = listOf(
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape()))
                        )
                        it.selectedMotionList = listOf(
                                AnimatorSet(),
                                AnimatorSet(),
                                AnimatorSet(),
                                AnimatorSet(),
                                AnimatorSet()
                        )
                        it.selectedColorList = listOf(
                                Color.RED,
                                Color.YELLOW,
                                Color.GREEN,
                                Color.BLUE,
                                Color.DKGRAY
                        )
                    }
            )

            independentDataParameters.add(
                    IndependentVisObjectDataParams().also{
                        it.keywordGroupMap = mapOf(
                                "업무" to mutableListOf(),
                                "친구" to mutableListOf(),
                                "가족" to mutableListOf(),
                                "광고" to mutableListOf(),
                                "기본" to mutableListOf()
                        )
                    }
            )

            independentAnimationParameters.add(
                    listOf(
                            IndependentVisObjectAnimParams(
                                    property = View.ALPHA,
                                    values = arrayOf(.5f),
                                    duration = 2 * 1000L,
                                    interpolator = LinearInterpolator())
                    )
            )

            aggregatedVisualMappings.add(
                    AggregatedVisMappingRule(
                            groupProperty = NotiProperty.LIFE_STAGE,
                            visMapping = mapOf(
                                    NuNotiVisVariable.POSITION to Pair(NotiAggregationType.COUNT, null),
                                    NuNotiVisVariable.SIZE to Pair(NotiAggregationType.COUNT, null),
                                    NuNotiVisVariable.SHAPE to Pair(NotiAggregationType.COUNT, null),
                                    NuNotiVisVariable.MOTION to Pair(NotiAggregationType.COUNT, null),
                                    NuNotiVisVariable.COLOR to Pair(NotiAggregationType.COUNT, null)
                            )
                    )
            )

            aggregatedVisualParameters.add(
                    AggregatedVisObjectVisParams().also{
                        it.selectedShapeList = listOf(
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
                                VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape()))
                        )
                        it.selectedMotionList = listOf(
                                AnimatorSet(),
                                AnimatorSet(),
                                AnimatorSet(),
                                AnimatorSet(),
                                AnimatorSet()
                        )
                        it.selectedColorList = listOf(
                                Color.RED,
                                Color.YELLOW,
                                Color.GREEN,
                                Color.BLUE,
                                Color.DKGRAY
                        )
                    }
            )

            aggregatedDataParameters.add(
                    AggregatedVisObjectDataParams().also{
                        it.keywordGroupMap = mapOf(
                                "업무" to mutableListOf(),
                                "친구" to mutableListOf(),
                                "가족" to mutableListOf(),
                                "광고" to mutableListOf(),
                                "기본" to mutableListOf()
                        )
                    }
            )

            aggregatedAnimationParameters.add(
                    listOf()
            )
        }
    }
}