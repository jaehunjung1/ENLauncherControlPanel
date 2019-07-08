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
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancementPattern
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visualEffects.NewVisShape
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape
import kotlin.math.roundToLong

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
        var selectedLifeList: List<EnhancedNotificationLife> = listOf(EnhancedNotificationLife.JUST_TRIGGERED, EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED, EnhancedNotificationLife.JUST_INTERACTED, EnhancedNotificationLife.INTERACTED_NOT_DECAYING, EnhancedNotificationLife.DECAYING),
        var keywordGroupMap: Map<String, MutableList<String>> = emptyMap(),
        var tSaturation: Long = -1L, //3hrs
        var additional: Map<String, Any> = emptyMap()
){
    val keywordGroups: List<String>
        get() = keywordGroupMap.keys.toList()
    val givenImportanceRange: Pair<Double, Double> = Pair(0.0, 1.0)
    val givenLifeList: List<EnhancedNotificationLife> = listOf(EnhancedNotificationLife.JUST_TRIGGERED, EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED, EnhancedNotificationLife.JUST_INTERACTED, EnhancedNotificationLife.INTERACTED_NOT_DECAYING, EnhancedNotificationLife.DECAYING)
}

data class IndependentVisObjectAnimParams(
        var property: Property<View, Float>,
        var values: Array<Float>,
        var duration: Long,
        var interpolator: TimeInterpolator,
        var sustained: List<EnhancedNotificationLife> = listOf(
                EnhancedNotificationLife.JUST_TRIGGERED,
                EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED,
                EnhancedNotificationLife.JUST_INTERACTED,
                EnhancedNotificationLife.INTERACTED_NOT_DECAYING,
                EnhancedNotificationLife.DECAYING
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

data class NotificationEnhacementParams(
        var initialImportance: Double = 0.5,
        var lifespan: Long = 1000L * 60 * 60 * 6,
        var importanceRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var firstPattern: EnhancementPattern = EnhancementPattern.EQ,
        var secondPattern: EnhancementPattern = EnhancementPattern.EQ,
        var firstSaturationTime: Long = (lifespan * 0.5).roundToLong(),
        var secondSaturationTime: Long= (lifespan * 0.5).roundToLong()
)