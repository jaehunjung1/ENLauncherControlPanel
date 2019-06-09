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
import kr.ac.snu.hcil.datahalo.visualEffects.NewVisShape
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

data class IndependentVisObjectVisParams(
        var selectedPos: Double = 0.5,
        var posRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var selectedShape: VisObjectShape = VisObjectShape(NewVisShape.OVAL, ShapeDrawable(OvalShape())),
        var shapeList: List<VisObjectShape> = emptyList(),
        var selectedMotion: AnimatorSet = AnimatorSet(),
        var motionList: List<AnimatorSet> = emptyList(),
        var selectedColor: Int = Color.CYAN,
        var colorList: List<Int> = emptyList(),
        var selecteSize: Double = 1.0,
        var sizeRange: Pair<Double, Double> = Pair(0.0, 1.0),
        var additional: Map<String, Any> = emptyMap()
)

data class IndependentVisObjectDataParams(
        var importanceRange:Pair<Double, Double> = Pair(0.0, 1.0),
        var lifeList: List<EnhancedNotificationLife> = listOf(EnhancedNotificationLife.STATE_1_JUST_TRIGGERED, EnhancedNotificationLife.STATE_2_TRIGGERED_NOT_INTERACTED, EnhancedNotificationLife.STATE_3_JUST_INTERACTED, EnhancedNotificationLife.STATE_4_INTERACTED_NOT_DECAYED, EnhancedNotificationLife.STATE_5_DECAYING),
        var contentGroupMap: Map<String, List<String>> = emptyMap(),
        var tSaturation: Long = -1L, //3hrs
        var additional: Map<String, Any> = emptyMap()
){
    val contentGroupList: List<String>
        get() = contentGroupMap.keys.toList()
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