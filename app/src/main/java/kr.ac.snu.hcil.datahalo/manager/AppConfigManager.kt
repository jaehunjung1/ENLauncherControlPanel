package kr.ac.snu.hcil.datahalo.manager

import android.animation.AnimatorSet
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.view.View
import android.view.animation.LinearInterpolator
import kr.ac.snu.hcil.datahalo.visconfig.*
import kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisMappingRule
import kr.ac.snu.hcil.datahalo.visualEffects.NewVisShape
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

class AppConfigManager {
    companion object {
        fun retrieveDefaultAppConfig(packageName: String): AppHaloConfig {
            return AppHaloConfig(packageName).apply{

                filterKeywordConfig = mapOf(
                        WGBFilterVar.ACTIVE to true,
                        WGBFilterVar.WHITE_COND to setOf("안단태"),
                        WGBFilterVar.BLACK_COND to setOf("한구현")
                )


                keywordGroupPatterns.apply{
                    addKeywordGroup("G1", setOf("연구실 공지", "서진욱 교수님"), 0, VisDataManager.getExampleSaturationPattern(VisDataManager.availableImportanceSaturationPatterns()[0])!!)
                    addKeywordGroup("G2", setOf("택배", "발송"), 1, VisDataManager.getExampleSaturationPattern(VisDataManager.availableImportanceSaturationPatterns()[0])!!)
                    addKeywordGroup("G3", setOf("광고"), 2, VisDataManager.getExampleSaturationPattern(VisDataManager.availableImportanceSaturationPatterns()[0])!!)
                }

                haloLayoutMethodName = AppHaloLayoutMethods.availiableLayouts[0]
                independentVisEffectName = VisEffectManager.availableIndependentVisEffects[0]
                aggregatedVisEffectName = VisEffectManager.availableAggregatedVisEffects[0]

                independentVisEffectVisParams = IndependentVisEffectVisParams(
                        radius = mutableListOf(0, 0, 0, 0, 0),
                        offsetAngle = 0f
                )

                independentVisualMappings.add(
                        mapOf(
                                NotiVisVariable.POSITION to null,
                                NotiVisVariable.SIZE to NotiProperty.IMPORTANCE,
                                NotiVisVariable.SHAPE to null,
                                NotiVisVariable.MOTION to null,
                                NotiVisVariable.COLOR to NotiProperty.LIFE_STAGE
                        )
                )

                independentVisualParameters.add(
                        IndependentVisObjectVisParams().also{
                            it.selectedShapeList = listOf(
                                    VisObjectShape(NewVisShape.RECT, ShapeDrawable(RectShape())),
                                    VisObjectShape(NewVisShape.RECT, ShapeDrawable(RectShape())),
                                    VisObjectShape(NewVisShape.RECT, ShapeDrawable(RectShape())),
                                    VisObjectShape(NewVisShape.RECT, ShapeDrawable(RectShape())),
                                    VisObjectShape(NewVisShape.RECT, ShapeDrawable(RectShape()))
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
                                        NotiVisVariable.POSITION to Pair(NotiAggregationType.COUNT, null),
                                        NotiVisVariable.SIZE to Pair(NotiAggregationType.MEAN_NUMERIC, NotiProperty.IMPORTANCE),
                                        NotiVisVariable.SHAPE to Pair(NotiAggregationType.COUNT, null),
                                        NotiVisVariable.MOTION to Pair(NotiAggregationType.COUNT, null),
                                        NotiVisVariable.COLOR to Pair(NotiAggregationType.COUNT, null)
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
                                    Color.argb(100, 255, 0, 0),
                                    Color.argb(100, 0, 255, 255),
                                    Color.argb(100, 0, 255, 0),
                                    Color.argb(100, 0, 0, 255),
                                    Color.argb(100, 50, 50, 50)
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
}