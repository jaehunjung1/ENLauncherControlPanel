package kr.ac.snu.hcil.datahalo.visualEffects

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.ImageView
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.notificationdata.NotiHierarchy
import kr.ac.snu.hcil.datahalo.utils.ANHComponentUIDGenerator
import kr.ac.snu.hcil.datahalo.utils.CoordinateConverter
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.*

interface InterfaceAggregatedVisEffect{

    val effectID: String
    val localPivotID: Int

    fun getMappedNotificationID(): List<Int>
    fun getDrawables(): List<Drawable>
    fun getAnimatorSet(): AnimatorSet
    fun setEnhancedNotification(enhancedNotifications: List<EnhancedNotification>)
    fun placeVisObjectsInLayout(constraintLayout: ConstraintLayout, pivotLayoutParams: ConstraintLayout.LayoutParams)
    fun deleteVisObjectsInLayout(constraintLayout: ConstraintLayout)
}
data class AggregatedVisMappingRule(val groupProperty: NotiProperty?, val visMapping: Map<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>)
abstract class AbstractAggregatedVisEffect(
        final override val effectID: String,
        var mappingRules: List<AggregatedVisMappingRule>
): InterfaceAggregatedVisEffect
{
    companion object{
        private fun exceptionInvalidAggregation(
                notiProperty: NotiProperty?,
                aggregatedOp: NotiAggregationType
        ) = Exception("$notiProperty Cannot Be Aggregated With $aggregatedOp")

        private fun exceptionInvalidGroupping(notiProperty: NotiProperty) = Exception("Invalid Groupping on $notiProperty")
    }

    final override val localPivotID: Int = ANHComponentUIDGenerator.generateLocalPivotUID()
    private val drawables: MutableList<Drawable> = mutableListOf()
    private var animatorSet: AnimatorSet = AnimatorSet()
    private var currentCenterPolar: Pair<Int, Float> = Pair(-1, 0f)
    private val mappedNotificationIDs: MutableList<Int> = mutableListOf()


    val groupBoundVisObjects: MutableList<List<AbstractAggregatedVisObject>> = mutableListOf()
    val normalVisObjects: MutableList<AbstractAggregatedVisObject> = mutableListOf()

    init{

        mappingRules.forEach{mappingRule ->
            if(mappingRule.groupProperty == null){
                normalVisObjects
            }
        }

        normalVisObjects.forEach{ abstractAggregatedVisObject ->
            abstractAggregatedVisObject.setID(ANHComponentUIDGenerator.generateVisObjectUID())
        }
        groupBoundVisObjects.forEach{ unitAggregatedVisObjects ->
            unitAggregatedVisObjects.map{it.setID(ANHComponentUIDGenerator.generateVisObjectUID())}

        }
    }

    final override fun getMappedNotificationID(): List<Int> = mappedNotificationIDs.toList()
    final override fun getDrawables(): List<Drawable> = drawables.toList()
    final override fun getAnimatorSet() = animatorSet
    final override fun setEnhancedNotification(enhancedNotifications: List<EnhancedNotification>) {

        mappedNotificationIDs.clear()
        mappedNotificationIDs.addAll(enhancedNotifications.map{it.id})
        drawables.clear()

        val animatorCollection = mutableListOf<Animator>()
        groupBoundVisObjects.forEach{ visObjects ->
            val groupByProp = visObjects[0].getNotiPropertyGroupedBy()
            val groupResult: Map<Any, List<EnhancedNotification>> = group(enhancedNotifications, groupByProp!!)

            visObjects.forEachIndexed{ index, visObject ->
                visObject.setGroupLabel(groupResult.keys.toList()[index])
                val tempResult = visObject.getVisMapping().values.map{ aggregationTypeAndTargetProperty ->
                    val aggrType = aggregationTypeAndTargetProperty.first
                    val targetProp = aggregationTypeAndTargetProperty.second
                    val aggregationResult = aggregate(groupResult.values.toList()[index], targetProp, aggrType)
                    Pair(aggregationTypeAndTargetProperty, aggregationResult)
                }.toMap()
                val (drawable, animator) = visObject.getDrawableWithAnimator(tempResult)
                drawables.add(drawable)
                animatorCollection.addAll(
                        animator.childAnimations.map{anim -> anim.also{it.setTarget(drawable)}}
                )
            }
        }

        normalVisObjects.forEach{ visObject ->
            val tempResult = visObject.getVisMapping().values.map{ aggregationTypeAndTargetProperty ->
                val aggrType = aggregationTypeAndTargetProperty.first
                val targetProp = aggregationTypeAndTargetProperty.second
                val aggregationResult = aggregate(enhancedNotifications, targetProp, aggrType)
                Pair(aggregationTypeAndTargetProperty, aggregationResult)
            }.toMap()
            val (drawable, animator) = visObject.getDrawableWithAnimator(tempResult)
            drawables.add(drawable)
            animatorCollection.addAll(
                    animator.childAnimations.map{anim -> anim.also{it.setTarget(drawable)}}
            )
        }

        animatorSet.playTogether(animatorCollection)
    }


    private fun group(
            enhancedNotifications: List<EnhancedNotification>,
            groupByProp: NotiProperty,
            visEffectParams: AggregatedVisEffectParams = AggregatedVisEffectParams()
    ): Map<Any, List<EnhancedNotification>>{
        return enhancedNotifications.groupBy {
            when(groupByProp){
                NotiProperty.LIFE_STAGE -> { it.lifeCycle }
                NotiProperty.IMPORTANCE -> {
                    val bins = MapFunctionUtilities.bin(Pair(0.0, 1.0), visEffectParams.groupNumber)
                    val ratio = it.currEnhancement
                    bins.filter{range -> range.first <= ratio && ratio < range.second}.let{filterResult->
                        if(filterResult.isEmpty()) exceptionInvalidGroupping(groupByProp) else filterResult.first()
                    }
                }
                NotiProperty.CONTENT-> {
                    val keywordGroupMap = visEffectParams.contentGroupMap
                    keywordGroupMap.mapValues{group ->
                        val groupName = group.key
                        val members = group.value
                        members.fold(0, {acc:Int, member:String -> if(it.notiContent.contains(member)) acc + 1 else acc})
                    }.maxBy{groupHitCount -> groupHitCount.value}.let{ result -> result?.key ?: "default" }
                }
                else -> {
                    exceptionInvalidGroupping(groupByProp)
                }
            }
        }
    }

    private fun aggregate(
            groupedNotification: List<EnhancedNotification>,
            targetProp: NotiProperty?,
            op: NotiAggregationType
    ): Any{
        when(op){
            NotiAggregationType.COUNT -> {
                return groupedNotification.size
            }
            NotiAggregationType.MIN_NUMERIC -> {
                return when(targetProp){
                    NotiProperty.IMPORTANCE -> {
                        groupedNotification.map{it.currEnhancement}.min() ?: -1.0
                    }
                    else -> {
                        exceptionInvalidAggregation(targetProp, op)
                    }
                }
            }
            NotiAggregationType.MEAN_NUMERIC -> {
                return when(targetProp){
                    NotiProperty.IMPORTANCE -> {
                        groupedNotification.map{it.currEnhancement}.average()
                    }
                    else -> {
                        exceptionInvalidAggregation(targetProp, op)
                    }
                }
            }
            NotiAggregationType.MAX_NUMERIC -> {
                return when(targetProp){
                    NotiProperty.IMPORTANCE -> {
                        groupedNotification.map{it.currEnhancement}.max() ?: -1.0
                    }
                    else -> {
                        exceptionInvalidAggregation(targetProp, op)
                    }
                }
            }
            NotiAggregationType.MOST_FREQUENT_NOMINAL -> {
                return when(targetProp){
                    NotiProperty.LIFE_STAGE -> {
                        group(groupedNotification, targetProp).toList().sortedBy { (_, value) -> value.size }.last().first as EnhancedNotificationLife
                    }
                    NotiProperty.CONTENT -> {
                        group(groupedNotification, targetProp).toList().sortedBy { (_, value) -> value.size }.last().first as String
                    }
                    else -> {
                        exceptionInvalidAggregation(targetProp, op)
                    }
                }
            }
        }
    }

    private fun doGroupAndAggregate(
            enhancedNotifications: List<EnhancedNotification>,
            aggregationRules: List<VisMappingManager.NotiPropertyAggregationRule>
    ){

        val groupedAggregationResult: Map<NotiProperty, Map<Any, List<Triple<NotiAggregationType, NotiProperty, Any>>>>
                = aggregationRules
                .filter{it.groupByProperty != null}
                .groupBy{it.groupByProperty!!}
                .map{ruleByGroupProperty ->
                    val groupProperty = ruleByGroupProperty.key
                    val aggruleList = ruleByGroupProperty.value
                    val groupResult: Map<Any, List<EnhancedNotification>> = group(enhancedNotifications, groupProperty)

                    val tempResult = mutableListOf<Pair<Any, Triple<NotiAggregationType, NotiProperty, Any>>>()

                    groupResult.forEach{ groupData ->
                        val groupKey = groupData.key
                        val groupVal = groupData.value

                        aggruleList.forEach{ rule ->
                            val aggOp = rule.aggType
                            val targetProp = rule.property
                            val aggregatedVal = aggregate(groupVal, targetProp, aggOp)
                            tempResult.add(Pair(groupKey, Triple(aggOp, targetProp, aggregatedVal)))
                        }
                    }

                    Pair(
                            groupProperty,
                            tempResult.groupBy{it.first}.mapValues{
                        groupedResult -> groupedResult.value.map{ it.second }
                    })
                 }.toMap()






    }

    final override fun placeVisObjectsInLayout(constraintLayout: ConstraintLayout, pivotLayoutParams: ConstraintLayout.LayoutParams) {
        val localPivotView = constraintLayout.findViewById<ImageView>(localPivotID)

        if(localPivotView == null){
            //r: global pivot -> local pivot 사이의 거리
            //theta: 꼭대기 기준으로 r과 이루는 각도
            currentCenterPolar = Pair(pivotLayoutParams.circleRadius, pivotLayoutParams.circleAngle)
            val newLocalPivotView = ImageView(constraintLayout.context).also{
                it.id = localPivotID
                it.setBackgroundColor(Color.YELLOW)
                it.layoutParams = pivotLayoutParams
            }

            constraintLayout.addView(newLocalPivotView)
        }

        if(currentCenterPolar.first != pivotLayoutParams.circleRadius || currentCenterPolar.second != pivotLayoutParams.circleAngle){
            currentCenterPolar = Pair(pivotLayoutParams.circleRadius, pivotLayoutParams.circleAngle)
            localPivotView.layoutParams = pivotLayoutParams
        }

        //local pivot 기준의 극좌표계 배치. 법선 방향이 꼭대기가 되도록 회전
        groupBoundVisObjects.forEach{visObjs ->
            visObjs.forEachIndexed{ index, visObj ->
                val (x, y) = visObj.getPosition()
                val (r, theta) = CoordinateConverter.centerBasedCartesianToPolarCoordinate(x, y)
                val layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        .also{
                            it.circleConstraint = localPivotID
                            it.circleRadius = Math.round(r).toInt()
                            it.circleAngle = theta.toFloat()
                        }
                if(constraintLayout.findViewById<ImageView>(visObj.getID()) == null){
                    val imageView = ImageView(constraintLayout.context).also{
                        it.id = visObj.getID()
                        it.setImageDrawable(drawables[index])

                        val (centerBasedX, centerBasedY) = CoordinateConverter.polarToCenterBasedCartesianCoordinate(
                                currentCenterPolar.first.toDouble(),
                                currentCenterPolar.second.toDouble()
                        )

                        val (defaultX, defaultY) = CoordinateConverter.centerBasedToDefaultCartesianCoordinate(
                                centerBasedX,
                                centerBasedY,
                                constraintLayout.width.toDouble(),
                                constraintLayout.height.toDouble()
                        )
                        it.pivotX = defaultX.toFloat()
                        it.pivotY = defaultY.toFloat()
                        it.rotation = theta.toFloat()
                    }
                    constraintLayout.addView(imageView, layoutParams)
                }
                else{
                    constraintLayout.findViewById<ImageView>(visObj.getID()).also{
                        it.setImageDrawable(drawables[index])
                        it.layoutParams = layoutParams
                    }
                }
            }

        }

        normalVisObjects.forEachIndexed{ index, visObj ->
            val (x, y) = visObj.getPosition()
            val (r, theta) = CoordinateConverter.centerBasedCartesianToPolarCoordinate(x, y)
            val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    .also{
                        it.circleConstraint = localPivotID
                        it.circleRadius = Math.round(r).toInt()
                        it.circleAngle = theta.toFloat()
                    }
            if(constraintLayout.findViewById<ImageView>(visObj.getID()) == null){
                val imageView = ImageView(constraintLayout.context).also{
                    it.id = visObj.getID()
                    it.setImageDrawable(drawables[index])

                    val (centerBasedX, centerBasedY) = CoordinateConverter.polarToCenterBasedCartesianCoordinate(
                            currentCenterPolar.first.toDouble(),
                            currentCenterPolar.second.toDouble()
                    )

                    val (defaultX, defaultY) = CoordinateConverter.centerBasedToDefaultCartesianCoordinate(
                            centerBasedX,
                            centerBasedY,
                            constraintLayout.width.toDouble(),
                            constraintLayout.height.toDouble()
                    )
                    it.pivotX = defaultX.toFloat()
                    it.pivotY = defaultY.toFloat()
                    it.rotation = theta.toFloat()
                }
                constraintLayout.addView(imageView, layoutParams)
            }
            else{
                constraintLayout.findViewById<ImageView>(visObj.getID()).also{
                    it.setImageDrawable(drawables[index])
                    it.layoutParams = layoutParams
                }
            }
        }
    }

    final override fun deleteVisObjectsInLayout(constraintLayout: ConstraintLayout) {
        groupBoundVisObjects.forEach{visObjs ->
            visObjs.forEach{
                constraintLayout.removeView(
                        constraintLayout.findViewById(it.getID())
                )
            }
        }

        normalVisObjects.forEach{
            constraintLayout.removeView(
                    constraintLayout.findViewById(it.getID())
            )
        }

        constraintLayout.removeView(
                constraintLayout.findViewById(localPivotID)
        )
    }
}