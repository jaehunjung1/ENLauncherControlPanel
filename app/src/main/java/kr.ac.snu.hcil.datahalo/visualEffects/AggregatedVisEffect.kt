package kr.ac.snu.hcil.datahalo.visualEffects

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.ImageView
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.utils.CoordinateConverter
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.*
import kotlin.math.roundToInt

interface InterfaceAggregatedVisEffect{

    val effectID: String
    val localPivotID: Int

    fun getMappedNotificationID(): List<Int>
    fun getDrawables(): List<Drawable>
    fun getAnimatorSet(): AnimatorSet

    fun setVisMapping(aggrMappingRules: List<AggregatedVisMappingRule>)
    fun setEnhancedNotification(enhancedNotifications: List<EnhancedNotification>)

    fun getGroupBoundVisObjects(): List<AbstractAggregatedVisObject>
    fun getNormalVisObjects(): List<AbstractAggregatedVisObject>
    fun getSizeOfGroupBoundVisObjects(): Int
    fun getSizeOfNormalVisObjects(): Int

    fun placeVisObjectsInLayout(constraintLayout: ConstraintLayout, aggregatedPivotLayoutParams: Pair<List<ConstraintLayout.LayoutParams>, List<ConstraintLayout.LayoutParams>>)
    fun deleteVisObjectsInLayout(constraintLayout: ConstraintLayout)
}
data class AggregatedVisMappingRule(val groupProperty: NotiProperty?, val visMapping: Map<NotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>)

abstract class AbstractAggregatedVisEffect(
        final override val effectID: String,
        var mappingRules: List<AggregatedVisMappingRule>,
        var effectParameters: AggregatedVisEffectVisParams,
        var objVisualParameters: List<AggregatedVisObjectVisParams>,
        var objDataParameters: List<AggregatedVisObjectDataParams>,
        var objAnimationParameters: List<List<AggregatedVisObjectAnimParams>>): InterfaceAggregatedVisEffect
{
    companion object{
        private fun exceptionInvalidAggregation(
                notiProperty: NotiProperty?,
                aggregatedOp: NotiAggregationType
        ) = Exception("$notiProperty Cannot Be Aggregated With $aggregatedOp")

        private fun exceptionInvalidGrouping(notiProperty: NotiProperty) = Exception("Invalid Grouping on $notiProperty")
    }

    final override val localPivotID: Int = View.generateViewId()
    private val groupedDrawables: MutableList<Drawable> = mutableListOf()
    private val normalDrawables: MutableList<Drawable> = mutableListOf()

    private val activatedGroupBoundVisObjectIndices: MutableList<Int> = mutableListOf()

    private var animatorSet: AnimatorSet = AnimatorSet()
    private var currentCenterPolar: Pair<Int, Float> = Pair(-1, 0f)
    private val mappedNotificationIDs: MutableList<Int> = mutableListOf()

    private val groupBoundVisObjects: MutableList<List<AbstractAggregatedVisObject>> = mutableListOf()
    private val normalVisObjects: MutableList<AbstractAggregatedVisObject> = mutableListOf()

    init{
        mappingRules.forEachIndexed{ index, mappingRule ->
            if(mappingRule.groupProperty == null){
                //no group property
                normalVisObjects.add(
                        AggregatedVisObject(
                                mappingRule.visMapping,
                                objVisualParameters[index],
                                objDataParameters[index],
                                objAnimationParameters[index]
                        ).apply{
                            setID(View.generateViewId())
                        }
                )
            }
            else{

                val size = when(mappingRule.groupProperty){
                    NotiProperty.IMPORTANCE -> objDataParameters[index].binNums
                    NotiProperty.CONTENT -> objDataParameters[index].keywordGroups.size
                    NotiProperty.LIFE_STAGE -> EnhancedNotificationLife.values().size }

                groupBoundVisObjects.add(
                        List(size){
                            AggregatedVisObject(
                                    mappingRule.visMapping,
                                    objVisualParameters[index],
                                    objDataParameters[index],
                                    objAnimationParameters[index]).apply{

                                setID(View.generateViewId())
                                setNotiPropertyGroupedBy(mappingRule.groupProperty)
                            }
                        }
                )
            }
        }
    }

    final override fun getGroupBoundVisObjects(): List<AbstractAggregatedVisObject> = if(groupBoundVisObjects.isEmpty()) emptyList() else groupBoundVisObjects[0]
    final override fun getNormalVisObjects(): List<AbstractAggregatedVisObject> = normalVisObjects
    final override fun getSizeOfGroupBoundVisObjects(): Int = if(groupBoundVisObjects.isEmpty()) 0 else groupBoundVisObjects[0].size
    final override fun getSizeOfNormalVisObjects(): Int = normalVisObjects.size
    final override fun setVisMapping(aggrMappingRules: List<AggregatedVisMappingRule>) {
        mappingRules = aggrMappingRules
        normalVisObjects.clear()
        groupBoundVisObjects.clear()

        mappingRules.forEachIndexed{ index, mappingRule ->
            if(mappingRule.groupProperty == null){
                //no group property
                normalVisObjects.add(
                        AggregatedVisObject(
                                mappingRule.visMapping,
                                objVisualParameters[index],
                                objDataParameters[index],
                                objAnimationParameters[index]
                        ).apply{
                            setID(View.generateViewId())
                        }
                )
            }
            else{

                val labels:List<Any> = when(mappingRule.groupProperty){
                    NotiProperty.IMPORTANCE -> MapFunctionUtilities.bin(objDataParameters[index].givenImportanceRange, objDataParameters[index].binNums)
                    NotiProperty.CONTENT -> objDataParameters[index].keywordGroups
                    NotiProperty.LIFE_STAGE -> EnhancedNotificationLife.values().toList()
                }

                groupBoundVisObjects.add(
                        List(labels.size){
                            AggregatedVisObject(
                                    mappingRule.visMapping,
                                    objVisualParameters[index],
                                    objDataParameters[index],
                                    objAnimationParameters[index]).apply{

                                setGroupLabel(labels[it])
                                setID(View.generateViewId())
                                setNotiPropertyGroupedBy(mappingRule.groupProperty)
                            }
                        }
                )
            }
        }
    }

    final override fun getMappedNotificationID(): List<Int> = mappedNotificationIDs.toList()
    final override fun getDrawables(): List<Drawable> = groupedDrawables.toList()
    final override fun getAnimatorSet() = animatorSet
    final override fun setEnhancedNotification(enhancedNotifications: List<EnhancedNotification>) {

        mappedNotificationIDs.clear()
        mappedNotificationIDs.addAll(enhancedNotifications.map{it.id})
        groupedDrawables.clear()
        normalDrawables.clear()
        activatedGroupBoundVisObjectIndices.clear()

        val animatorCollection = mutableListOf<Animator>()
        groupBoundVisObjects.forEach{ visObjects ->
            val groupByProp = visObjects[0].getNotiPropertyGroupedBy()
            val groupResult: Map<Any, List<EnhancedNotification>> = group(
                    enhancedNotifications,
                    groupByProp!!,
                    visObjects[0].getDataParams())

            visObjects.forEachIndexed{ index, visObject ->
                if(visObject.getGroupLabel() !in groupResult.keys){
                    // do nothing
                }
                else{
                    val dataToAggregate = groupResult[visObject.getGroupLabel()]!!
                    val tempResult = visObject.getVisMapping().values.map{ aggregationTypeAndTargetProperty ->
                        val aggrType = aggregationTypeAndTargetProperty.first
                        val targetProp = aggregationTypeAndTargetProperty.second
                        val aggregationResult = aggregate(dataToAggregate, targetProp, aggrType, visObject.getDataParams())
                        Pair(aggregationTypeAndTargetProperty, aggregationResult)
                    }.toMap()
                    val (drawable, animator) = visObject.getDrawableWithAnimator(tempResult)
                    groupedDrawables.add(drawable)
                    activatedGroupBoundVisObjectIndices.add(index)
                    animatorCollection.addAll(
                            animator.childAnimations.map{anim -> anim.also{it.setTarget(drawable)}}
                    )
                }
            }

            /*
            if(groupResult.isNotEmpty()){
                visObjects.forEachIndexed{ index, visObject ->
                    visObject.setGroupLabel(groupResult.keys.toList()[index])
                    val tempResult = visObject.getVisMapping().values.map{ aggregationTypeAndTargetProperty ->
                        val aggrType = aggregationTypeAndTargetProperty.first
                        val targetProp = aggregationTypeAndTargetProperty.second
                        val aggregationResult = aggregate(groupResult.values.toList()[index], targetProp, aggrType, visObject.getDataParams())
                        Pair(aggregationTypeAndTargetProperty, aggregationResult)
                    }.toMap()
                    val (drawable, animator) = visObject.getDrawableWithAnimator(tempResult)
                    groupedDrawables.add(drawable)
                    animatorCollection.addAll(
                            animator.childAnimations.map{anim -> anim.also{it.setTarget(drawable)}}
                    )
                }
            }
            */

        }

        normalVisObjects.forEach{ visObject ->
            val tempResult = visObject.getVisMapping().values.map{ aggregationTypeAndTargetProperty ->
                val aggrType = aggregationTypeAndTargetProperty.first
                val targetProp = aggregationTypeAndTargetProperty.second
                val aggregationResult = aggregate(enhancedNotifications, targetProp, aggrType, visObject.getDataParams())
                Pair(aggregationTypeAndTargetProperty, aggregationResult)
            }.toMap()
            val (drawable, animator) = visObject.getDrawableWithAnimator(tempResult)
            normalDrawables.add(drawable)
            animatorCollection.addAll(
                    animator.childAnimations.map{anim -> anim.also{it.setTarget(drawable)}}
            )
        }
        animatorSet.playTogether(animatorCollection)
    }

    private fun group(
            enhancedNotifications: List<EnhancedNotification>,
            groupByProp: NotiProperty,
            objectDataParams: AggregatedVisObjectDataParams
    ): Map<Any, List<EnhancedNotification>>{
        return enhancedNotifications.groupBy {
            when(groupByProp){
                NotiProperty.LIFE_STAGE -> { it.lifeCycle }
                NotiProperty.IMPORTANCE -> {
                    val bins = MapFunctionUtilities.bin(Pair(0.0, 1.0), objectDataParams.binNums)
                    val ratio = it.currEnhancement
                    bins.filter{range -> range.first <= ratio && ratio < range.second}.let{ filterResult->
                        if(filterResult.isEmpty()) exceptionInvalidGrouping(groupByProp) else filterResult.first()
                    }
                }
                NotiProperty.CONTENT-> {
                    val keywordGroupMap = objectDataParams.keywordGroupMap
                    keywordGroupMap.mapValues{group ->
                        val groupName = group.key
                        val members = group.value
                        members.fold(0, {acc:Int, member:String -> if(it.notiContent.contains(member)) acc + 1 else acc})
                    }.maxBy{groupHitCount -> groupHitCount.value}.let{ result -> result?.key ?: "default" }
                }
            }
        }
    }

    private fun aggregate(
            groupedNotification: List<EnhancedNotification>,
            targetProp: NotiProperty?,
            op: NotiAggregationType,
            dataParams: AggregatedVisObjectDataParams
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
                        group(groupedNotification, targetProp, dataParams).toList().sortedBy { (_, value) -> value.size }.last().first as EnhancedNotificationLife
                    }
                    NotiProperty.CONTENT -> {
                        group(groupedNotification, targetProp, dataParams).toList().sortedBy { (_, value) -> value.size }.last().first as String
                    }
                    else -> {
                        exceptionInvalidAggregation(targetProp, op)
                    }
                }
            }
        }
    }

    /*
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
    */

    final override fun placeVisObjectsInLayout(constraintLayout: ConstraintLayout, aggregatedPivotLayoutParams: Pair<List<ConstraintLayout.LayoutParams>, List<ConstraintLayout.LayoutParams>>) {

        if(groupedDrawables.isEmpty() && normalDrawables.isEmpty())
            return

        //local pivot 기준의 극좌표계 배치. 법선 방향이 꼭대기가 되도록 회전
        groupBoundVisObjects[0].let{ visObjs ->
            visObjs.forEachIndexed{ index, visObj ->
                val layoutParams = aggregatedPivotLayoutParams.first[index]
                if(constraintLayout.findViewById<ImageView>(visObj.getID()) == null){
                    val imageView = ImageView(constraintLayout.context).also{
                        it.id = visObj.getID()
                        it.setImageDrawable(groupedDrawables[index])
                        /*
                        if(index in activatedGroupBoundVisObjectIndices)
                            it.setImageDrawable(groupedDrawables[activatedGroupBoundVisObjectIndices.indexOf(index)])
                        */

                        /*
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
                        */
                    }
                    constraintLayout.addView(imageView, layoutParams)
                }
                else{
                    constraintLayout.findViewById<ImageView>(visObj.getID()).also{
                        it.setImageDrawable(groupedDrawables[index])
                        it.layoutParams = layoutParams
                    }
                }
            }
        }

        normalVisObjects.forEachIndexed{ index, visObj ->
            val layoutParams = aggregatedPivotLayoutParams.second[index]
            if(constraintLayout.findViewById<ImageView>(visObj.getID()) == null){
                val imageView = ImageView(constraintLayout.context).also{
                    it.id = visObj.getID()
                    it.setImageDrawable(normalDrawables[index])
                    /*
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
                    */
                }
                constraintLayout.addView(imageView, layoutParams)
            }
            else{
                constraintLayout.findViewById<ImageView>(visObj.getID()).also{
                    it.setImageDrawable(normalDrawables[index])
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