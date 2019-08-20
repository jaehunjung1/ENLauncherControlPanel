package kr.ac.snu.hcil.datahalo.manager

import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractAggregatedVisEffect
import kr.ac.snu.hcil.datahalo.visualEffects.AbstractIndependentVisEffect
import kotlin.math.roundToInt

class AppHaloLayoutMethods {
    companion object{
        private fun exceptionLayoutNotMatched(layoutName: String) = Exception("$layoutName Does Not Exist")

        private val registeredLayoutMethods: List<AbstractANHVisLayout>
                = listOf(BookshelfLayout, ClockwiseSortedLayout)

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
            : Pair<Map<Int, ConstraintLayout.LayoutParams>, Pair<List<ConstraintLayout.LayoutParams>, List<ConstraintLayout.LayoutParams>>>
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
            aggregatedVisEffect: AbstractAggregatedVisEffect?)
            : Pair<Map<Int, ConstraintLayout.LayoutParams>, Pair<List<ConstraintLayout.LayoutParams>, List<ConstraintLayout.LayoutParams>>> {

        val k = independent.size
        val eachAngle: Double = 360.0 / k

        val independentLPs = independent.sortedWith(compareBy({ it.whiteRank }, { it.initTime })).mapIndexed{index, enhancedNotification ->
            val notiID = enhancedNotification.id
            val (wScale, hScale) = independentVisEffects[notiID]?.let{visEffect -> visEffect.independentVisObjects[0].getPosition()}
                    ?: throw EXCEPTION_VIS_EFFECT_NOT_EXIST(notiID)
            val layoutParam = ConstraintLayout.LayoutParams(sizeOfIVE,sizeOfIVE).also{
                it.circleConstraint = pivotViewID
                it.circleRadius = (0.5 * minOf(target.layoutParams.width, target.layoutParams.height) * minOf(wScale, hScale) / 2.5).roundToInt()
                it.circleAngle = (index * eachAngle).toFloat()
            }
            enhancedNotification.id to layoutParam
        }.toMap()

        var aggregatedLPs: Pair<List<ConstraintLayout.LayoutParams>, List<ConstraintLayout.LayoutParams>> = Pair(emptyList(), emptyList())

        aggregatedVisEffect?.let{ aggrVisEffect ->
            val groupVisObjs = aggrVisEffect.getGroupBoundVisObjects()
            val groupVisLPs = List(groupVisObjs.size){ index ->
                val (wScale, hScale) = groupVisObjs[index].getPosition()
                ConstraintLayout.LayoutParams(sizeOfAVE,sizeOfAVE).also{
                    it.circleConstraint = pivotViewID
                    it.circleRadius = (0.5 * minOf(target.layoutParams.width, target.layoutParams.height) * minOf(wScale, hScale) / 2).roundToInt()
                    it.circleAngle = (index * eachAngle).toFloat()
                }
            }

            val normalVisObjs = aggrVisEffect.getNormalVisObjects()
            val normalVisLPs = List(normalVisObjs.size){ index ->
                val (wScale, hScale) = normalVisObjs[index].getPosition()
                ConstraintLayout.LayoutParams(sizeOfAVE,sizeOfAVE).also{
                    it.circleConstraint = pivotViewID
                    it.circleRadius = (0.5 * minOf(target.layoutParams.width, target.layoutParams.height) * minOf(wScale, hScale) / 2).roundToInt()
                    it.circleAngle = (index * eachAngle).toFloat()
                }
            }

            aggregatedLPs = Pair(groupVisLPs, normalVisLPs)
        }
        return Pair(independentLPs, aggregatedLPs)
    }
}

object BookshelfLayout: AbstractANHVisLayout("BookshelfLayout"){
    override fun generateLayoutParams(
            target: ConstraintLayout, pivotViewID: Int,
            independent: List<EnhancedNotification>,
            independentVisEffects: Map<Int, AbstractIndependentVisEffect>,
            aggregated: List<EnhancedNotification>,
            aggregatedVisEffect: AbstractAggregatedVisEffect?)
            : Pair<Map<Int, ConstraintLayout.LayoutParams>, Pair<List<ConstraintLayout.LayoutParams>, List<ConstraintLayout.LayoutParams>>> {

        val intervalAngle = 10.0f

        val independentLPs = independent.sortedWith(compareBy({it.whiteRank}, {it.initTime})).mapIndexed{index, enhancedNotification ->
            val notiID = enhancedNotification.id
            val (wScale, hScale) = independentVisEffects[notiID]?.let{visEffect -> visEffect.independentVisObjects[0].getPosition()}
                    ?: throw EXCEPTION_VIS_EFFECT_NOT_EXIST(notiID)

            val layoutParam = ConstraintLayout.LayoutParams(sizeOfIVE, sizeOfIVE).also{
                it.circleConstraint = pivotViewID
                it.circleRadius = (0.2 * target.layoutParams.width).roundToInt()
                it.circleAngle = intervalAngle * index
            }
            enhancedNotification.id to layoutParam
        }.toMap()

        var aggregatedLPs: Pair<List<ConstraintLayout.LayoutParams>, List<ConstraintLayout.LayoutParams>> = Pair(emptyList(), emptyList())

        return Pair(independentLPs, aggregatedLPs)
    }
}
