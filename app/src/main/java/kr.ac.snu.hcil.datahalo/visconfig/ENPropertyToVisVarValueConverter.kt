package kr.ac.snu.hcil.datahalo.visconfig

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.widget.ImageView
import com.android.launcher3.R
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.notificationdata.NotiHierarchy

interface ENPropertyToVisVarValueConverter {
    val type: NotiProperty
    val possibleConversions: MutableMap<NotiVisVariable, Boolean>

    fun toPositionXY(minX: Int, maxX: Int, minY: Int, maxY: Int): Pair<Int, Int>
    fun toSize(minW: Int, maxW: Int, minH: Int, maxH: Int): Pair<Int, Int>
    fun toShape(grade: Int): Drawable
    fun toColor(grade: Int, colorCode: Int): Int
    fun toMotionDuration(minDuration: Long, maxDuration: Long, animator: ValueAnimator): ValueAnimator
}

abstract class ImportanceToVisVarValueConverter(
        override val type: NotiProperty,
        var currImportance: Double,
        val minImportance: Double = 0.0,
        val maxImportance: Double = 1.0,
        val context: Context): ENPropertyToVisVarValueConverter {

    override val possibleConversions: MutableMap<NotiVisVariable, Boolean> = mutableMapOf(
            NotiVisVariable.POSITION_X to true,
            NotiVisVariable.POSITION_Y to true,
            NotiVisVariable.SHAPE to true,
            NotiVisVariable.SIZE_X to true,
            NotiVisVariable.SIZE_Y to true,
            NotiVisVariable.COLOR to true,
            NotiVisVariable.MOTION to true
    )

    val defaultGrade = 5

    override fun toPositionXY(minX: Int, maxX: Int, minY: Int, maxY: Int): Pair<Int, Int>{
        val x = (maxX - minX) * ((currImportance - minImportance) / (maxImportance - minImportance))
        val y = (maxY - minY) * ((currImportance - minImportance) / (maxImportance - minImportance))
        return Pair(Math.round(x).toInt(), Math.round(y).toInt())
    }

    override fun toSize(minW: Int, maxW: Int, minH: Int, maxH: Int): Pair<Int, Int>{
        val x = (maxW - minW) * ((currImportance - minImportance) / (maxImportance - minImportance))
        val y = (maxH - minH) * ((currImportance - minImportance) / (maxImportance - minImportance))
        return Pair(Math.round(x).toInt(), Math.round(y).toInt())
    }

    override fun toShape(grade: Int): Drawable {

        val impin100 = Math.round(currImportance * 100.0).toInt()
        val lvListDrawable = LevelListDrawable().also{
            var currentLow = 0
            var grade = 0
            while(currentLow < 100){
                it.addLevel(
                        currentLow,
                        currentLow + 100 / grade - 1,
                        context.getDrawable(
                                context.resources.getIdentifier("shape_level_$grade", "drawable", context.packageName )
                        )
                )
                currentLow += 100 / grade
                currentLow += 1
            }
        } as ImageView

        lvListDrawable.setImageLevel(impin100)
        return lvListDrawable.drawable
    }


    override fun toColor(grade: Int, colorCode: Int): Int {

        val a = 255 * ((currImportance - minImportance) / (maxImportance - minImportance))
        val r = Color.red(colorCode)
        val g = Color.green(colorCode)
        val b = Color.blue(colorCode)

        return Color.argb( Math.round(a).toInt(), r, g, b)
    }

    override fun toMotionDuration(minDuration: Long, maxDuration: Long, animator: ValueAnimator): ValueAnimator{
        return animator.also{
            it.duration = Math.round(minDuration + (maxDuration - minDuration) * ((currImportance - minImportance) / (maxImportance - minImportance)))
        }
    }

}

abstract class ElapsedTimeToVisValueConverter(
        override val type: NotiProperty,
        var elapsedTime: Long,
        val saturation: Long = 1000 * 60 * 60 * 3L,
        val context: Context): ENPropertyToVisVarValueConverter{

    override val possibleConversions: MutableMap<NotiVisVariable, Boolean> = mutableMapOf(
            NotiVisVariable.POSITION_X to true,
            NotiVisVariable.POSITION_Y to true,
            NotiVisVariable.SHAPE to true,
            NotiVisVariable.SIZE_X to true,
            NotiVisVariable.SIZE_Y to true,
            NotiVisVariable.COLOR to true,
            NotiVisVariable.MOTION to true
    )

    override fun toPositionXY(minX: Int, maxX: Int, minY: Int, maxY: Int): Pair<Int, Int> {

        val time = if(elapsedTime > saturation) saturation else elapsedTime
        val x = 1.0 * (maxX - minX) * time / saturation
        val y = 1.0 * (maxY - minY) * time / saturation
        return Pair(Math.round(x).toInt(), Math.round(y).toInt())
    }

    override fun toSize(minW: Int, maxW: Int, minH: Int, maxH: Int): Pair<Int, Int> {
        val time = if(elapsedTime > saturation) saturation else elapsedTime
        val w = 1.0 * (maxW - minW) * time / saturation
        val h = 1.0 * (maxH - minH) * time / saturation
        return Pair(Math.round(w).toInt(), Math.round(h).toInt())
    }

    override fun toShape(grade: Int): Drawable {
        val time = if(elapsedTime > saturation) saturation else elapsedTime
        val timein100 = Math.round(100.0 * (time / saturation)).toInt()

        val lvListDrawable = LevelListDrawable().also{
            var currentLow = 0
            var grade = 0
            while(currentLow < 100){
                it.addLevel(
                        currentLow,
                        currentLow + 100 / grade - 1,
                        context.getDrawable(
                                context.resources.getIdentifier("shape_level_$grade", "drawable", context.packageName )
                        )
                )
                currentLow += 100 / grade
                currentLow += 1
            }
        } as ImageView

        lvListDrawable.setImageLevel(timein100)
        return lvListDrawable.drawable
    }

    override fun toColor(grade: Int, colorCode: Int): Int {
        val time = if(elapsedTime > saturation) saturation else elapsedTime

        val a = 255.0 * ( time / saturation)
        val r = Color.red(colorCode)
        val g = Color.green(colorCode)
        val b = Color.blue(colorCode)

        return Color.argb( Math.round(a).toInt(), r, g, b)
    }

    override fun toMotionDuration(minDuration: Long, maxDuration: Long, animator: ValueAnimator): ValueAnimator {
        val time = if(elapsedTime > saturation) saturation else elapsedTime

        return animator.also{
            it.duration = Math.round(minDuration + (maxDuration - minDuration) * (1.0 * time / saturation))
        }
    }
}

abstract class InteractionStateToVisValueConverter(
        override val type: NotiProperty,
        var currentState: EnhancedNotificationLife,
        val context: Context): ENPropertyToVisVarValueConverter{

    override val possibleConversions: MutableMap<NotiVisVariable, Boolean> = mutableMapOf(
            NotiVisVariable.POSITION_X to false,
            NotiVisVariable.POSITION_Y to false,
            NotiVisVariable.SHAPE to true,
            NotiVisVariable.SIZE_X to false,
            NotiVisVariable.SIZE_Y to false,
            NotiVisVariable.COLOR to true,
            NotiVisVariable.MOTION to true
    )

    override fun toPositionXY(minX: Int, maxX: Int, minY: Int, maxY: Int): Pair<Int, Int> {
        return Pair(-1, -1)
    }

    override fun toSize(minW: Int, maxW: Int, minH: Int, maxH: Int): Pair<Int, Int> {
        return Pair(-1, -1)
    }

    override fun toShape(grade: Int): Drawable {
        val map = mapOf(
                EnhancedNotificationLife.STATE_1 to context.getDrawable(R.drawable.halo_shape_level_item_0),
                EnhancedNotificationLife.STATE_2 to context.getDrawable(R.drawable.halo_shape_level_item_1),
                EnhancedNotificationLife.STATE_3 to context.getDrawable(R.drawable.halo_shape_level_item_2),
                EnhancedNotificationLife.STATE_4 to context.getDrawable(R.drawable.halo_shape_level_item_3),
                EnhancedNotificationLife.STATE_5 to context.getDrawable(R.drawable.halo_shape_level_item_4)
        )
        return map[currentState]!!
    }

    override fun toColor(grade: Int, colorCode: Int): Int {
        val map = mapOf(
                EnhancedNotificationLife.STATE_1 to context.getColor(R.color.red_700),
                EnhancedNotificationLife.STATE_2 to context.getColor(R.color.deep_orange_700),
                EnhancedNotificationLife.STATE_3 to context.getColor(R.color.yellow_700),
                EnhancedNotificationLife.STATE_4 to context.getColor(R.color.green_700),
                EnhancedNotificationLife.STATE_5 to context.getColor(R.color.light_blue_700)
        )
        return map[currentState]!!
    }

    override fun toMotionDuration(minDuration: Long, maxDuration: Long, animator: ValueAnimator): ValueAnimator {
        val map = mapOf(
                EnhancedNotificationLife.STATE_1 to 1000L,
                EnhancedNotificationLife.STATE_2 to 1000L,
                EnhancedNotificationLife.STATE_3 to 1000L,
                EnhancedNotificationLife.STATE_4 to 1000L,
                EnhancedNotificationLife.STATE_5 to 1000L
        )
        return animator.also{
            it.duration = map[currentState]!!
        }
    }
}

abstract class NotificationHierarchyToVisValueConverter(
        override val type: NotiProperty,
        var notiHierarchy: NotiHierarchy,
        val packageName: String,
        val context: Context): ENPropertyToVisVarValueConverter{

    override val possibleConversions: MutableMap<NotiVisVariable, Boolean> = mutableMapOf(
            NotiVisVariable.POSITION_X to false,
            NotiVisVariable.POSITION_Y to false,
            NotiVisVariable.SHAPE to true,
            NotiVisVariable.SIZE_X to false,
            NotiVisVariable.SIZE_Y to false,
            NotiVisVariable.COLOR to true,
            NotiVisVariable.MOTION to false
    )
    //NotificationHierarchy에 맞게 적절한 애를 뱉어주는 Utility 클래스가 존재해야 함

    override fun toSize(minW: Int, maxW: Int, minH: Int, maxH: Int): Pair<Int, Int> {
        return Pair(-1, -1)
    }

    override fun toPositionXY(minX: Int, maxX: Int, minY: Int, maxY: Int): Pair<Int, Int> {
        return Pair(-1, -1)
    }

    override fun toColor(grade: Int, colorCode: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun toMotionDuration(minDuration: Long, maxDuration: Long, animator: ValueAnimator): ValueAnimator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toShape(grade: Int): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}



