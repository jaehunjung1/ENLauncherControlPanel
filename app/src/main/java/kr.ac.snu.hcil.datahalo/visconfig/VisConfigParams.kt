package kr.ac.snu.hcil.datahalo.visconfig

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Property
import android.view.View
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visualEffects.NewVisShape
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

data class IndependentVisObjectVisParams(
        var selectedPos: Double = 1.0,
        var selectedPosRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var selectedPosRangeList: List<Pair<Double, Double>> = MapFunctionUtilities.bin(selectedPosRange, 5),
        var selectedShape: VisObjectShape = VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
        var selectedShapeList: List<VisObjectShape> = listOf(),
        var selectedMotion: AnimatorSet = AnimatorSet(),
        var selectedMotionList: List<AnimatorSet> = listOf(),
        var selectedColor: Int = Color.BLACK,
        var selectedColorList: List<Int> = listOf(),
        var selectedSize: Double = 1.0,
        var selectedSizeRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var selectedSizeRangeList: List<Pair<Double, Double>> = MapFunctionUtilities.bin(selectedSizeRange, 5),
        var additional: Map<String, Any> = emptyMap()
)

data class IndependentVisObjectDataParams(
        val binNums: Int = 5,
        var selectedImportanceRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var selectedImportanceRangeList: List<Pair<Double, Double>> =  MapFunctionUtilities.bin(selectedImportanceRange, binNums),
        var selectedLifeList: List<EnhancedNotificationLife> = listOf(EnhancedNotificationLife.STATE_1_JUST_TRIGGERED, EnhancedNotificationLife.STATE_2_TRIGGERED_NOT_INTERACTED, EnhancedNotificationLife.STATE_3_JUST_INTERACTED, EnhancedNotificationLife.STATE_4_INTERACTED_NOT_DECAYED, EnhancedNotificationLife.STATE_5_DECAYING),
        var keywordGroupMap: Map<String, MutableList<String>> = emptyMap(),
        var tSaturation: Long = -1L, //3hrs
        var additional: Map<String, Any> = emptyMap()
){
    val keywordGroups: List<String>
        get() = keywordGroupMap.keys.toList()
    val givenImportanceRange: Pair<Double, Double> = Pair(0.0, 1.0)
    val givenLifeList: List<EnhancedNotificationLife> = listOf(EnhancedNotificationLife.STATE_1_JUST_TRIGGERED, EnhancedNotificationLife.STATE_2_TRIGGERED_NOT_INTERACTED, EnhancedNotificationLife.STATE_3_JUST_INTERACTED, EnhancedNotificationLife.STATE_4_INTERACTED_NOT_DECAYED, EnhancedNotificationLife.STATE_5_DECAYING)
}

data class IndependentVisObjectAnimParams(
        var property: Property<View, Float>,
        var values: Array<Float>,
        var duration: Long,
        var interpolator: TimeInterpolator,
        var sustained: List<EnhancedNotificationLife> = listOf(
                EnhancedNotificationLife.STATE_1_JUST_TRIGGERED,
                EnhancedNotificationLife.STATE_2_TRIGGERED_NOT_INTERACTED,
                EnhancedNotificationLife.STATE_3_JUST_INTERACTED,
                EnhancedNotificationLife.STATE_4_INTERACTED_NOT_DECAYED,
                EnhancedNotificationLife.STATE_5_DECAYING
        ),
        var repeatCount: Int = ObjectAnimator.INFINITE,
        var repeatMode: Int = ObjectAnimator.REVERSE
){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

data class IndependentVisEffectVisParams(
        val radius: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0),
        var offsetAngle: Float = 0f
)

data class AggregatedVisEffectParams(
        var groupNumber: Int = 5,
        var contentGroupMap: Map<String, List<String>> = emptyMap()
)