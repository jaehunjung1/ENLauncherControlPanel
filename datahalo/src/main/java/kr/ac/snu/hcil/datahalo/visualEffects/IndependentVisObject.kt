package kr.ac.snu.hcil.datahalo.visualEffects

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.utils.TextDrawable
import kr.ac.snu.hcil.datahalo.visconfig.*
import kotlin.math.roundToInt

interface InterfaceVisObject{

    fun setID(id: Int)
    fun getID(): Int

    fun getPosition(): Pair<Double, Double>
    fun setPosition(pos: Pair<Double, Double>)

    fun getMappingState(visVar: NotiVisVariable): MappingState?
    fun getVisVarsOf(mappingState: MappingState): List<NotiVisVariable>

    fun getVisMapping(): Map<NotiVisVariable, NotiProperty?>
    fun setVisMapping(mapping: Map<NotiVisVariable, NotiProperty?>)

    fun getDataParams(): IndependentVisObjectDataParams
    fun setDataParams(params: IndependentVisObjectDataParams)

    fun getImportanceEnhancementPatterns(): KeywordGroupImportancePatterns
    fun setImportanceEnhancementPatterns(keywordGroupImportancePatterns: KeywordGroupImportancePatterns)

    fun getAnimatorsByLifeStage(): Map<EnhancedNotificationLife, AnimatorSet>
    fun setAnimParams(vararg params: IndependentVisObjectAnimParams)

    fun getVisParams(): IndependentVisObjectVisParams
    fun setVisParams(params: IndependentVisObjectVisParams)


    fun conversionForPredefinedVisVar(predefinedVisVar: NotiVisVariable)
    fun conversionForBoundVisVar(boundVisVar: NotiVisVariable, notiProp: NotiProperty): (Any) -> Any?
    fun conversionForTransparentVisVar(transparentVisVar: NotiVisVariable): Any

    fun getDrawableWithAnimator(input:Map<NotiProperty, Any>): Pair<Drawable, AnimatorSet>
}

open class IndependentVisObject(
        visualMapping: Map<NotiVisVariable, NotiProperty?>,
        importanceEnhancementPatterns: KeywordGroupImportancePatterns,
        visualParameters: IndependentVisObjectVisParams,
        dataParameters: IndependentVisObjectDataParams,
        animationParameters: List<IndependentVisObjectAnimParams>)
    : AbstractIndependentVisObject(
        customizabilitySpec = mapOf(
                NotiVisVariable.POSITION to VisVarCustomizability.CUSTOMIZABLE,
                NotiVisVariable.COLOR to VisVarCustomizability.CUSTOMIZABLE,
                NotiVisVariable.MOTION to VisVarCustomizability.CUSTOMIZABLE,
                NotiVisVariable.SHAPE to VisVarCustomizability.CUSTOMIZABLE,
                NotiVisVariable.SIZE to VisVarCustomizability.CUSTOMIZABLE
        ),
        transformationRule = mapOf(
                NotiVisVariable.POSITION to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT),
                NotiVisVariable.COLOR to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT),
                NotiVisVariable.MOTION to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT),
                NotiVisVariable.SHAPE to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT),
                NotiVisVariable.SIZE to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT)
        ),
        visualMapping = visualMapping,
        importanceEnhancementPatterns = importanceEnhancementPatterns,
        visualParameters =  visualParameters,
        dataParameters =  dataParameters,
        animationParameters = animationParameters)


abstract class AbstractIndependentVisObject(
        val customizabilitySpec: Map<NotiVisVariable, VisVarCustomizability>,
        val transformationRule: Map<NotiVisVariable, List<NotiProperty>>,
        visualMapping: Map<NotiVisVariable, NotiProperty?>,
        importanceEnhancementPatterns: KeywordGroupImportancePatterns,
        visualParameters: IndependentVisObjectVisParams,
        dataParameters: IndependentVisObjectDataParams,
        animationParameters: List<IndependentVisObjectAnimParams>
): InterfaceVisObject {
    companion object {
        val exceptionInvalidCustomizability = Exception("CustomizabilitySpec May Lack Information.")
        val exceptionInvalidMappingInput = Exception("Input Mapping is Invalid")
        val exceptionNotInitialized = Exception("Object is Not Initialized. Set Mapping First.")
        val exceptionVisVariable = {visVar: NotiVisVariable -> Exception("Usage of $visVar is Invalid.")}
        val exceptionNotSupportedTransformation = { visVar: NotiVisVariable, notiProp: NotiProperty -> Exception("$notiProp -> $visVar Mapping Does Not Exist.")}
    }

    private var id: Int = -1

    private var enhancementPatterns = importanceEnhancementPatterns
    private var visParams: IndependentVisObjectVisParams = visualParameters
    private var dataParams: IndependentVisObjectDataParams = dataParameters

    lateinit var currMapping: Map<NotiVisVariable, NotiProperty?>
    lateinit var boundConversions: Map<NotiVisVariable, (Any) -> Any?>
    lateinit var userDefinedConversions: Map<NotiVisVariable, Any>
    private var position: Pair<Double, Double> = Pair(0.0, 0.0)
    private lateinit var animationMap: Map<EnhancedNotificationLife, AnimatorSet>


    init{
        setVisMapping(visualMapping)
        setAnimParams(*animationParameters.toTypedArray())
    }

    final override fun getAnimatorsByLifeStage(): Map<EnhancedNotificationLife, AnimatorSet> = animationMap
    final override fun setAnimParams(vararg params: IndependentVisObjectAnimParams) {
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

    final override fun getID(): Int = id
    final override fun setID(id: Int) { this.id = id }

    final override fun getPosition(): Pair<Double, Double> = position
    final override fun setPosition(pos: Pair<Double, Double>) { this.position = pos}

    final override fun getImportanceEnhancementPatterns(): KeywordGroupImportancePatterns = enhancementPatterns
    final override fun setImportanceEnhancementPatterns(keywordGroupImportancePatterns: KeywordGroupImportancePatterns) {
        enhancementPatterns = keywordGroupImportancePatterns
        customizabilitySpec.filter{it.value == VisVarCustomizability.PREDEFINED}.keys.map{
            conversionForPredefinedVisVar(it)
        }
    }

    final override fun getDataParams() = dataParams
    final override fun setDataParams(params: IndependentVisObjectDataParams){
        dataParams = params
        customizabilitySpec.filter{it.value == VisVarCustomizability.PREDEFINED}.keys.map{
            conversionForPredefinedVisVar(it)
        }
    }

    final override fun getVisParams() = visParams
    final override fun setVisParams(params: IndependentVisObjectVisParams) {
        visParams = params
        customizabilitySpec.filter{it.value == VisVarCustomizability.PREDEFINED}.keys.map{
            conversionForPredefinedVisVar(it)
        }
        updateMappingFunction()
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

    final override fun getVisMapping(): Map<NotiVisVariable, NotiProperty?> = currMapping
    final override fun setVisMapping(mapping: Map<NotiVisVariable, NotiProperty?>){
        currMapping = mapping
        updateMappingFunction()
    }
    private fun updateMappingFunction(){
        //check if mapping is absurd
        val isValid = currMapping.keys.fold(true){
            acc:Boolean, el: NotiVisVariable ->
            val truth = el in customizabilitySpec.filter{it.value == VisVarCustomizability.CUSTOMIZABLE}.keys
            acc && truth
        }

        if(!isValid)
            exceptionInvalidMappingInput

        val boundMappings = currMapping.filter{it.value != null}
        val unboundMappings = currMapping.filterNot{it.value != null}

        boundConversions = boundMappings.mapValues{
            val visVar = it.key
            val notiProp = it.value
            val f: (Any) -> Any? = conversionForBoundVisVar(visVar, notiProp!!)
            f
        }

        userDefinedConversions = unboundMappings.mapValues{
            val visVar = it.key
            conversionForTransparentVisVar(visVar)
        }.toMap()
    }

    final override fun conversionForBoundVisVar(boundVisVar: NotiVisVariable, notiProp: NotiProperty): (Any) -> Any?{
        val keywordGroups = getImportanceEnhancementPatterns().getOrderedKeywordGroupImportancePatternsWithRemainder().map{it.group}
        val dataParams = getDataParams()
        val visualParams = getVisParams()

        when(boundVisVar){
            NotiVisVariable.MOTION -> {
                val motions = visualParams.selectedMotionList
                when(notiProp){
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
                        return MapFunctionUtilities.createMapFunc(keywordGroups, motions)
                    }
                    else -> {
                        return {x -> motions[0]}
                    }
                }
            }
            NotiVisVariable.SHAPE -> {
                val shape = visualParams.selectedShapeList
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createBinnedNumericRangeMapFunc(dataParams.selectedImportanceRangeList, shape)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        if(shape.size != dataParams.givenLifeList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.givenLifeList, shape)

                    }
                    NotiProperty.CONTENT -> {
                        if(shape.size < keywordGroups.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(keywordGroups, shape)
                    }
                    else -> {
                        return {x -> shape[0]}
                    }
                }
            }
            NotiVisVariable.POSITION -> {
                val posRange = visualParams.selectedPosRange// 이값은 어디선가 와야겠지?
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.selectedImportanceRange, posRange)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        return MapFunctionUtilities.createMapFuncToBinnedNumericRange(dataParams.givenLifeList, visualParams.getSelectedPosRangeList(dataParams.givenLifeList.size))
                    }
                    NotiProperty.CONTENT -> {
                        return MapFunctionUtilities.createMapFuncToBinnedNumericRange(keywordGroups, visualParams.getSelectedPosRangeList(keywordGroups.size))
                    }
                    else -> {
                        return {x -> (posRange.first + posRange.second) / 2}
                    }
                }
            }
            NotiVisVariable.COLOR -> {
                val colors = visualParams.selectedColorList
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createBinnedNumericRangeMapFunc(dataParams.selectedImportanceRangeList, colors)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        if(colors.size != dataParams.givenLifeList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.givenLifeList, colors)
                    }
                    NotiProperty.CONTENT -> {
                        if(colors.size < keywordGroups.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(keywordGroups, colors)
                    }
                    else -> {
                        return {x -> colors[0]}
                    }
                }

            }
            NotiVisVariable.SIZE -> {
                val sizeRange = visualParams.selectedSizeRange
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.selectedImportanceRange, sizeRange)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        return MapFunctionUtilities.createMapFuncToBinnedNumericRange(dataParams.givenLifeList, visualParams.getSelectedSizeRangeList(dataParams.givenLifeList.size))
                    }
                    NotiProperty.CONTENT -> {
                        return MapFunctionUtilities.createMapFuncToBinnedNumericRange(keywordGroups, visualParams.getSelectedSizeRangeList(keywordGroups.size))
                    }
                    else -> {
                        return {x -> (sizeRange.first + sizeRange.second) / 2}
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

    internal fun computeVisVarVal(input: Map<NotiProperty, Any>): Map<NotiVisVariable, Any>{

        val visualParams = getVisParams()
        var pos: Double = visualParams.selectedPos
        var size: Double = visualParams.selectedSize
        var shape: VisObjectShape = visualParams.selectedShape
        var color: Int = visualParams.selectedColor
        var motion: AnimatorSet = visualParams.selectedMotion

        input.forEach{ propertyToValue ->
            val notiProp: NotiProperty = propertyToValue.key
            val notiVal: Any = propertyToValue.value

            val visVars = currMapping.filter{it.value == notiProp}.map{ it.key }.toList()
            visVars.forEach{ visVar ->
                when(visVar){
                    in boundConversions -> {
                        val f = boundConversions[visVar]!!
                        f(notiVal)?.let{ visVal ->
                            when(visVar){
                                NotiVisVariable.SIZE -> size = visVal as Double
                                NotiVisVariable.MOTION -> motion = visVal as AnimatorSet
                                NotiVisVariable.SHAPE -> { shape = (visVal as VisObjectShape).also{it.raw = notiVal} }
                                NotiVisVariable.POSITION -> pos = visVal as Double
                                NotiVisVariable.COLOR -> color = visVal as Int
                            }
                        }
                    }
                    in userDefinedConversions -> {
                        val visVal = userDefinedConversions[visVar]!!
                        when(visVar){
                            NotiVisVariable.SIZE -> size = visVal as Double
                            NotiVisVariable.MOTION -> motion = visVal as AnimatorSet
                            NotiVisVariable.SHAPE -> { shape = (visVal as VisObjectShape).also{it.raw = null} }
                            NotiVisVariable.POSITION -> pos = visVal as Double
                            NotiVisVariable.COLOR -> color = visVal as Int
                        }
                    }
                    else -> {
                        //여기 오면 문제 생김
                    }
                }
            }
        }

        return mapOf(
                NotiVisVariable.POSITION to pos,
                NotiVisVariable.SIZE to size,
                NotiVisVariable.SHAPE to shape,
                NotiVisVariable.COLOR to color,
                NotiVisVariable.MOTION to motion
        )
    }

    override fun getDrawableWithAnimator(input: Map<NotiProperty, Any>): Pair<Drawable, AnimatorSet> {

        val computedResult = computeVisVarVal(input)

        val pos = computedResult[NotiVisVariable.POSITION] as Double
        val size = computedResult[NotiVisVariable.SIZE] as Double
        val shape = computedResult[NotiVisVariable.SHAPE] as VisObjectShape
        val color = computedResult[NotiVisVariable.COLOR] as Int
        val motion = computedResult[NotiVisVariable.MOTION] as AnimatorSet

        position = Pair(pos, pos)

        val mySize = (150 * size).roundToInt()
        val resultDrawable: Drawable = when(shape.type){
            VisShapeType.RECT -> {
                ShapeDrawable().also{
                    it.shape = RectShape()
                    it.paint.color = color
                    it.intrinsicWidth = mySize
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.OVAL -> {
                ShapeDrawable().also{
                    it.shape = OvalShape()
                    it.paint.color = color
                    it.intrinsicWidth = mySize
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.PATH -> {
                ShapeDrawable().also{
                    //it.shape = PathShape()
                    it.paint.color = color
                    it.intrinsicWidth = mySize
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.IMAGE -> {
                (shape.drawable as BitmapDrawable?)?.let{
                    val w = mySize
                    val h = mySize
                    BitmapDrawable(
                            Resources.getSystem(),
                            Bitmap.createScaledBitmap(
                                    it.bitmap,
                                    if(w == 0) 1 else w,
                                    if(h == 0) 1 else h,
                                    true
                            )
                    ).apply{
                        setColorFilter(
                                Color.argb((255 * 0.5).roundToInt(), Color.red(color), Color.green(color), Color.blue(color)),
                                PorterDuff.Mode.OVERLAY
                        )
                    }
                }?: ShapeDrawable().apply{
                    paint.color = color
                    intrinsicWidth = mySize
                    intrinsicHeight = mySize
                }
            }
            VisShapeType.TEXT -> {
                TextDrawable(shape.raw.toString(), mySize.toFloat()).also{
                    it.setText(shape.raw.toString(), mySize.toFloat(), color)
                }
            }
        }

        return Pair(resultDrawable, motion.also{it.setTarget(resultDrawable)})
    }

}

class HeightOnlyIndependentObject(
        visualMapping: Map<NotiVisVariable, NotiProperty?>,
        importanceEnhancementPatterns: KeywordGroupImportancePatterns,
        visualParameters: IndependentVisObjectVisParams,
        dataParameters: IndependentVisObjectDataParams,
        animationParameters: List<IndependentVisObjectAnimParams>)
    : IndependentVisObject(
        visualMapping = visualMapping,
        importanceEnhancementPatterns = importanceEnhancementPatterns,
        visualParameters = visualParameters,
        dataParameters = dataParameters,
        animationParameters = animationParameters
) {
    override fun getDrawableWithAnimator(input: Map<NotiProperty, Any>): Pair<Drawable, AnimatorSet> {

        val computedResult = computeVisVarVal(input)

        val pos = computedResult[NotiVisVariable.POSITION] as Double
        val size = computedResult[NotiVisVariable.SIZE] as Double
        val shape = computedResult[NotiVisVariable.SHAPE] as VisObjectShape
        val color = computedResult[NotiVisVariable.COLOR] as Int
        val motion = computedResult[NotiVisVariable.MOTION] as AnimatorSet

        setPosition(Pair(pos, pos))

        val mySize = (150 * size).roundToInt()

        val resultDrawable: Drawable = when(shape.type){
            VisShapeType.RECT -> {
                ShapeDrawable().also{
                    it.shape = RectShape()
                    it.paint.color = color
                    it.intrinsicWidth = 20
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.OVAL -> {
                ShapeDrawable().also{
                    it.shape = OvalShape()
                    it.paint.color = color
                    it.intrinsicWidth = 20
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.PATH -> {
                (shape.drawable as ShapeDrawable).also{
                    it.paint.color = color
                    it.intrinsicWidth = 20
                    it.intrinsicHeight = mySize
                }
            }
            VisShapeType.IMAGE -> {
                (shape.drawable as BitmapDrawable?)?.let{
                    val h = mySize
                    BitmapDrawable(
                            Resources.getSystem(),
                            Bitmap.createScaledBitmap(
                                    it.bitmap,
                                    20,
                                    if(h == 0) 1 else mySize,
                                    true
                            )
                    ).apply{
                        setColorFilter(
                                Color.argb((255 * 0.5).roundToInt(), Color.red(color), Color.green(color), Color.blue(color)),
                                PorterDuff.Mode.OVERLAY
                        )
                    }
                }?: ShapeDrawable().apply{
                    paint.color = color
                    intrinsicWidth = mySize
                    intrinsicHeight = mySize
                }
            }
            VisShapeType.TEXT -> {
                TextDrawable(shape.raw.toString(), mySize.toFloat()).also{
                    it.setText(shape.raw.toString(), mySize.toFloat(), color, 90f)
                }
            }
        }

        return Pair(resultDrawable, motion.also{it.setTarget(resultDrawable)})
    }
}

