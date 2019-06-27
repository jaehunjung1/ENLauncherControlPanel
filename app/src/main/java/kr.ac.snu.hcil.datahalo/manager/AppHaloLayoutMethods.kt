package kr.ac.snu.hcil.datahalo.manager

import androidx.constraintlayout.widget.ConstraintLayout
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.utils.ANHComponentUIDGenerator
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractAggregatedVisEffect
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisEffect

class AppHaloLayoutMethods {
    companion object{
        private fun exceptionLayoutNotMatched(layoutName: String) = Exception("$layoutName Does Not Exist")

        private val registeredLayoutMethods: List<AbstractANHVisLayout>
                = listOf(ClockwiseSortedLayout)

        val availiableLayouts:List<String>
            get() = registeredLayoutMethods.map{layout -> layout.layoutID}


        fun getLayout(visConfig: AppHaloConfig): AbstractANHVisLayout{
            val layout = registeredLayoutMethods.find{layout -> layout.layoutID == visConfig.haloLayoutMethodName}
            if (layout == null)
                throw exceptionLayoutNotMatched(visConfig.haloLayoutMethodName)
            else
                return layout
        }
    }
}

interface InterfaceANHVisLayout{
    val layoutID: String
    fun generateLayoutParams(target: ConstraintLayout,
                             pivotViewID: Int,
                             independent: List<EnhancedNotification>,
                             independentVisEffects: Map<Int, AbstractIndependentVisEffect>,
                             aggregated: List<EnhancedNotification>,
                             aggregatedVisEffect: AbstractAggregatedVisEffect?)
            : Pair<Map<Int, ConstraintLayout.LayoutParams>, ConstraintLayout.LayoutParams>
}

abstract class AbstractANHVisLayout(override val layoutID: String): InterfaceANHVisLayout{
    companion object{
        fun EXCEPTION_VIS_EFFECT_NOT_EXIST(notiID: Int) = Exception("VisEffect for $notiID Does Not Exist")
        const val sizeOfIVE = 10
        const val sizeOfAVE = 100
    }
}

object ClockwiseSortedLayout: AbstractANHVisLayout("ClockwiseSortedLayout"){
    override fun generateLayoutParams(
            target: ConstraintLayout,
            pivotViewID: Int,
            independent: List<EnhancedNotification>,
            independentVisEffects: Map<Int, AbstractIndependentVisEffect>,
            aggregated: List<EnhancedNotification>,
            aggregatedVisEffect: AbstractAggregatedVisEffect?): Pair<Map<Int, ConstraintLayout.LayoutParams>, ConstraintLayout.LayoutParams> {

        val k = independent.size
        val eachAngle: Double = 360.0 / k

        val independentLPs = independent.sortedWith(compareBy({ it.whiteRank}, {it.initTime})).mapIndexed{index, enhancedNotification ->
            val notiID = enhancedNotification.id
            val (wScale, hScale) = independentVisEffects[notiID]?.let{visEffect -> visEffect.independentVisObjects[0].getPosition()}
                    ?: throw EXCEPTION_VIS_EFFECT_NOT_EXIST(notiID)

            val layoutparam = ConstraintLayout.LayoutParams(sizeOfIVE,sizeOfIVE).also{
                it.circleConstraint = pivotViewID
                it.circleRadius = Math.round(0.5 * Math.min(target.layoutParams.width, target.layoutParams.height) * Math.min(wScale, hScale) / 2).toInt()
                it.circleAngle = (index * eachAngle).toFloat()
            }
            enhancedNotification.id to layoutparam
        }.toMap()

        val aggregatedLP = ConstraintLayout.LayoutParams(20, 20).also{
            it.circleConstraint = pivotViewID
            it.circleRadius = Math.round(0.1 * target.height / 2).toInt()
            it.circleAngle = 0f
        }

        return Pair(independentLPs, aggregatedLP)
    }
}
