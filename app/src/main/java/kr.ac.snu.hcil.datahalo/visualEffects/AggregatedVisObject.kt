package kr.ac.snu.hcil.datahalo.visualEffects

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.view.Gravity
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.utils.TextDrawable
import kr.ac.snu.hcil.datahalo.visconfig.*
import kotlin.math.roundToInt

interface InterfaceAggregatedVisObject{
    fun setID(id: Int)
    fun getID(): Int

    fun getPosition(): Pair<Double, Double>

    fun getMappingState(visVar: NotiVisVariable): MappingState?
    fun getVisVarsOf(mappingState: MappingState): List<NotiVisVariable>

    fun getNotiPropertyGroupedBy(): NotiProperty?
    fun setNotiPropertyGroupedBy(groupNotiProp: NotiProperty?)

    fun getGroupLabel(): Any?
    fun setGroupLabel(label: Any?)

    fun getVisMapping(): Map<NotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>
    fun setVisMapping(mapping: Map<NotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>)

    fun getVisParams(): AggregatedVisObjectVisParams
    fun setVisParams(params: AggregatedVisObjectVisParams)

    fun getDataParams(): AggregatedVisObjectDataParams
    fun setDataParams(params: AggregatedVisObjectDataParams)

    fun getImportanceEnhancementPatterns(): KeywordGroupImportancePatterns
    fun setImportanceEnhancementPatterns(keywordGroupImportancePatterns: KeywordGroupImportancePatterns)

    fun getAnimatorsByLifeStage(): Map<EnhancedNotificationLife, AnimatorSet>
    fun setAnimParams(vararg params: AggregatedVisObjectAnimParams)

    fun conversionForPredefinedVisVar(predefinedVisVar: NotiVisVariable)
    fun conversionForBoundVisVar(boundVisVar: NotiVisVariable, aggrNotiProp: Pair<NotiAggregationType, NotiProperty?>): (Any) -> Any?
    fun conversionForTransparentVisVar(transparentVisVar: NotiVisVariable): Any
    fun getDrawableWithAnimator(input: Map<Pair<NotiAggregationType, NotiProperty?>, Any?>): Pair<Drawable, AnimatorSet>?
}

class AggregatedVisObject(
        visualMapping: Map<NotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>,
        importanceEnhancementPatterns: KeywordGroupImportancePatterns,
        visualParameters: AggregatedVisObjectVisParams,
        dataParameters: AggregatedVisObjectDataParams,
        animationParameters: List<AggregatedVisObjectAnimParams>)
    :AbstractAggregatedVisObject(
        customizabilitySpec = mapOf(
                NotiVisVariable.POSITION to VisVarCustomizability.CUSTOMIZABLE,
                NotiVisVariable.COLOR to VisVarCustomizability.CUSTOMIZABLE,
                NotiVisVariable.MOTION to VisVarCustomizability.CUSTOMIZABLE,
                NotiVisVariable.SHAPE to VisVarCustomizability.CUSTOMIZABLE,
                NotiVisVariable.SIZE to VisVarCustomizability.CUSTOMIZABLE
        ),
        visualMapping = visualMapping,
        importanceEnhancementPatterns = importanceEnhancementPatterns,
        visualParameters =  visualParameters,
        dataParameters =  dataParameters,
        animationParameters = animationParameters
)

abstract class AbstractAggregatedVisObject(
        val customizabilitySpec: Map<NotiVisVariable, VisVarCustomizability>,
        importanceEnhancementPatterns: KeywordGroupImportancePatterns,
        visualMapping: Map<NotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>,
        visualParameters: AggregatedVisObjectVisParams,
        dataParameters: AggregatedVisObjectDataParams,
        animationParameters: List<AggregatedVisObjectAnimParams>)
    :InterfaceAggregatedVisObject
{
    companion object {
        val exceptionInvalidCustomizability = Exception("CustomizabilitySpec May Lack Information.")
        val exceptionInvalidMappingInput = Exception("Input Mapping is Invalid")
        val exceptionNotInitialized = Exception("Object is Not Initialized. Set Mapping First.")
        val exceptionVisVariable = {visVar:NotiVisVariable -> Exception("Usage of $visVar is Invalid.")}
    }

    private var id: Int = -1

    private var enhancementPatterns = importanceEnhancementPatterns

    private var visParams: AggregatedVisObjectVisParams = visualParameters
    private var dataParams: AggregatedVisObjectDataParams = dataParameters
    private var notiPropGroupedBy: NotiProperty? = null
    private var groupLabel: Any? = null
    private lateinit var currMapping: Map<NotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>
    lateinit var boundConversions: Map<NotiVisVariable, (Any) -> Any?>
    lateinit var userDefinedConversions: Map<NotiVisVariable, Any>
    private var position: Pair<Double, Double> = Pair(0.0, 0.0)
    private lateinit var animationMap: Map<EnhancedNotificationLife, AnimatorSet>

    init{
        setVisMapping(visualMapping)
        setAnimParams(*animationParameters.toTypedArray())
    }

    final override fun getID(): Int = id
    final override fun setID(id: Int) { this.id = id }

    final override fun getPosition(): Pair<Double, Double> = position

    final override fun getNotiPropertyGroupedBy(): NotiProperty? = notiPropGroupedBy
    final override fun setNotiPropertyGroupedBy(groupNotiProp: NotiProperty?) { notiPropGroupedBy = groupNotiProp}

    final override fun getGroupLabel(): Any? = groupLabel
    final override fun setGroupLabel(label: Any?){ groupLabel = label}

    final override fun getVisParams(): AggregatedVisObjectVisParams = visParams
    final override fun setVisParams(params: AggregatedVisObjectVisParams) {
        visParams = params
        customizabilitySpec.filter{it.value == VisVarCustomizability.PREDEFINED}.keys.map{
            conversionForPredefinedVisVar(it)
        }
    }

    final override fun getDataParams(): AggregatedVisObjectDataParams = dataParams
    final override fun setDataParams(params: AggregatedVisObjectDataParams) {
        dataParams = params
        customizabilitySpec.filter{it.value == VisVarCustomizability.PREDEFINED}.keys.map{
            conversionForPredefinedVisVar(it)
        }
    }

    final override fun getImportanceEnhancementPatterns(): KeywordGroupImportancePatterns = enhancementPatterns
    final override fun setImportanceEnhancementPatterns(keywordGroupImportancePatterns: KeywordGroupImportancePatterns) {
        enhancementPatterns = keywordGroupImportancePatterns
        customizabilitySpec.filter{it.value == VisVarCustomizability.PREDEFINED}.keys.map{
            conversionForPredefinedVisVar(it)
        }
    }

    final override fun getAnimatorsByLifeStage(): Map<EnhancedNotificationLife, AnimatorSet> = animationMap
    final override fun setAnimParams(vararg params: AggregatedVisObjectAnimParams) {
        val myMap: Map<EnhancedNotificationLife, MutableList<Animator>> = mapOf(
                EnhancedNotificationLife.JUST_TRIGGERED to mutableListOf(),
                EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED to mutableListOf(),
                EnhancedNotificationLife.JUST_INTERACTED to mutableListOf(),
                EnhancedNotificationLife.INTERACTED_NOT_DECAYING to mutableListOf(),
                EnhancedNotificationLife.DECAYING to mutableListOf()
        )

        params.forEach{ animParam ->
            animParam.sustained.map{ lifeStage ->
                myMap[lifeStage]!!.add(
                        ObjectAnimator.ofFloat(null,animParam.property, *(animParam.values.toFloatArray())).apply{
                            repeatMode = animParam.repeatMode
                            repeatCount = animParam.repeatCount
                            duration = animParam.duration
                        }
                )
            }
        }

        animationMap = myMap.mapValues{
            AnimatorSet().apply{ playTogether(it.value) }
        }
    }


    final override fun getMappingState(visVar: NotiVisVariable): MappingState? {
        if (customizabilitySpec[visVar] == null)
            exceptionInvalidCustomizability
        return when (customizabilitySpec[visVar]){
            VisVarCustomizability.PREDEFINED -> MappingState.PREDEFINED
            VisVarCustomizability.CUSTOMIZABLE -> {if(visVar in currMapping.keys) MappingState.BOUND else MappingState.TRANSPARENT}
            else -> null
        }
    }

    final override fun getVisVarsOf(mappingState: MappingState): List<NotiVisVariable> {
        return when (mappingState){
            MappingState.PREDEFINED -> { customizabilitySpec.filter{ it.value == VisVarCustomizability.PREDEFINED}.keys.toList()}
            MappingState.BOUND -> {boundConversions.keys.toList()}
            MappingState.TRANSPARENT -> {userDefinedConversions.keys.toList()}
        }
    }

    final override fun getVisMapping(): Map<NotiVisVariable, Pair<NotiAggregationType, NotiProperty?>> = currMapping
    final override fun setVisMapping(mapping: Map<NotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>){
        currMapping = mapping

        //check if mapping is absurd
        val isValid = currMapping.keys.fold(true){
            acc:Boolean, el:NotiVisVariable ->
            val truth = el in customizabilitySpec.filter{it.value == VisVarCustomizability.CUSTOMIZABLE}.keys
            acc && truth
        }

        if(!isValid)
            exceptionInvalidMappingInput

        val boundVisVars = currMapping.keys

        boundConversions = currMapping.mapValues{
            val visVar = it.key
            val notiProp = it.value
            val f: (Any) -> Any? = conversionForBoundVisVar(visVar, notiProp)
            f
        }

        val userDefinedVisVars = customizabilitySpec.filter{
            it.value == VisVarCustomizability.CUSTOMIZABLE}.keys.filter{
            it !in boundVisVars
        }

        userDefinedConversions = userDefinedVisVars.map{ visVar ->
            val f: (Any) = conversionForTransparentVisVar(visVar)
            visVar to f
        }.toMap()
    }

    final override fun conversionForBoundVisVar(boundVisVar: NotiVisVariable, aggrNotiProp: Pair<NotiAggregationType, NotiProperty?>): (Any) -> Any? {
        val keywordGroups = getImportanceEnhancementPatterns().getOrderedKeywordGroupImportancePatternsWithRemainder().map{it.group}
        val dataParams = getDataParams()
        val visualParams = getVisParams()

        when(boundVisVar){
            NotiVisVariable.MOTION -> {
                val motions = visualParams.selectedMotionList

                when(aggrNotiProp.second){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createBinnedNumericRangeMapFunc(dataParams.selectedImportanceRangeList, motions)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        if(motions.size < dataParams.givenLifeList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.givenLifeList, motions)
                    }
                    NotiProperty.CONTENT -> {
                        if(motions.size < keywordGroups.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.keywordGroups, motions)
                    }
                    else -> {
                        return  MapFunctionUtilities.createMapFunc(listOf(1, 3, 5, 7, 10), motions)
                    }
                }
            }
            NotiVisVariable.SHAPE -> {
                val shape = visualParams.selectedShapeList
                when(aggrNotiProp.second){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createBinnedNumericRangeMapFunc(dataParams.selectedImportanceRangeList, shape)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        if(shape.size != dataParams.givenLifeList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.givenLifeList, shape)

                    }
                    NotiProperty.CONTENT -> {
                        if(shape.size != dataParams.keywordGroups.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.keywordGroups, shape)
                    }
                    else -> {
                        return MapFunctionUtilities.createMapFunc(listOf(1, 3, 5, 7, 10), shape)
                    }
                }
            }
            NotiVisVariable.POSITION -> {
                val posRange = visualParams.selectedPosRange// 이값은 어디선가 와야겠지?
                when(aggrNotiProp.second){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.selectedImportanceRange, posRange)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.givenLifeList, posRange)
                    }
                    NotiProperty.CONTENT -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.keywordGroups, posRange)
                    }
                    else -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.countThreshold, posRange)
                    }
                }
            }
            NotiVisVariable.COLOR -> {
                val colors = visualParams.selectedColorList
                when(aggrNotiProp.second){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createBinnedNumericRangeMapFunc(dataParams.selectedImportanceRangeList, colors)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        if(colors.size != dataParams.givenLifeList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.givenLifeList, colors)
                    }
                    NotiProperty.CONTENT -> {
                        if(colors.size != dataParams.keywordGroups.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.keywordGroups, colors)
                    }
                    else -> {
                        return MapFunctionUtilities.createMapFunc(listOf(1, 3, 5, 7, 10), colors)
                    }
                }

            }
            NotiVisVariable.SIZE -> {
                val sizeRange = visualParams.selectedSizeRange
                when(aggrNotiProp.second){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.selectedImportanceRange, sizeRange)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.givenLifeList, sizeRange)
                    }
                    NotiProperty.CONTENT -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.keywordGroups, sizeRange)
                    }
                    else -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.countThreshold, sizeRange)
                    }
                }
            }
        }
    }

    final override fun conversionForTransparentVisVar(transparentVisVar: NotiVisVariable): Any {
        val visualParams = getVisParams()
        when (transparentVisVar){
            NotiVisVariable.SIZE -> {
                return visualParams.selectedSize
            }
            NotiVisVariable.COLOR -> {
                return visualParams.selectedColor
            }
            NotiVisVariable.POSITION -> {
                return visualParams.selectedPos
            }
            NotiVisVariable.SHAPE -> {
                return visualParams.selectedShape
            }
            NotiVisVariable.MOTION -> {
                return visualParams.selectedMotion
            }
        }
    }

    final override fun conversionForPredefinedVisVar(predefinedVisVar: NotiVisVariable) {}

    final override fun getDrawableWithAnimator(input: Map<Pair<NotiAggregationType, NotiProperty?>, Any?>): Pair<Drawable, AnimatorSet>? {
        val visualParams = getVisParams()
        var pos: Double = visualParams.selectedPos
        var size: Double = visualParams.selectedSize
        var shape: VisObjectShape = visualParams.selectedShape
        var color: Int = visualParams.selectedColor
        var motion: AnimatorSet = visualParams.selectedMotion

        input.forEach { mapEntry ->
            mapEntry.value?.let{ notiVal ->
                val visVars: List<NotiVisVariable> = currMapping.filter{ it.value == mapEntry.key }.map{ it.key }
                visVars.forEach { visVar ->
                    when (visVar) {
                        in boundConversions -> {
                            val f = boundConversions[visVar]!!
                            f(notiVal)?.let { visVal ->
                                when (visVar) {
                                    NotiVisVariable.SIZE -> size = visVal as Double
                                    NotiVisVariable.MOTION -> motion = visVal as AnimatorSet
                                    NotiVisVariable.SHAPE -> shape = visVal as VisObjectShape
                                    NotiVisVariable.POSITION -> pos = visVal as Double
                                    NotiVisVariable.COLOR -> color = visVal as Int
                                }
                            }
                        }
                        in userDefinedConversions -> {
                            val visVal = userDefinedConversions[visVar]!!
                            when (visVar) {
                                NotiVisVariable.SIZE -> size = visVal as Double
                                NotiVisVariable.MOTION -> motion = visVal as AnimatorSet
                                NotiVisVariable.SHAPE -> shape = visVal as VisObjectShape
                                NotiVisVariable.POSITION -> pos = visVal as Double
                                NotiVisVariable.COLOR -> color = visVal as Int
                            }
                        }
                        else -> {
                            //여기 오면 문제 생김
                        }
                    }
                }
            } ?: run{ return null }
        }

        position = Pair(pos, pos)

        val mySize = 200
        val shapeDrawable = when(shape.type){
            VisShapeType.RECT -> {
                (shape.drawable as ShapeDrawable).also{
                    it.paint.color = color
                    it.intrinsicWidth = mySize
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.OVAL -> {
                /*
                (shape.drawable as ShapeDrawable).also{
                    it.paint.color = color
                    it.intrinsicWidth = mySize
                    it.intrinsicHeight = mySize
                }
                */
                ShapeDrawable().also{
                    it.shape = OvalShape()
                    it.paint.color = color
                    it.intrinsicWidth = mySize
                    it.intrinsicHeight = mySize
                }
                //TODO(VisConfigParam의 ShapeDrawable 고쳐야 함 공유 문제)
            }
            VisShapeType.PATH -> {
                (shape.drawable as ShapeDrawable).also{
                    it.paint.color = color
                    it.intrinsicWidth = mySize
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.IMAGE -> {
                ShapeDrawable().also{
                    it.intrinsicWidth = mySize
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.TEXT -> {
                (shape.drawable as TextDrawable).also{
                    it.setColor(color)
                }
            }
        }

        val resultDrawable = ScaleDrawable(
                shapeDrawable,
                Gravity.CENTER,
                1.0f,
                1.0f
        ).also{
            it.level = (10000 * size).roundToInt()
        }

        return Pair(shapeDrawable, motion.also{it.setTarget(resultDrawable)})
    }

}