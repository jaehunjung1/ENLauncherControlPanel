package kr.ac.snu.hcil.datahalo.visualEffects

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.Gravity
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.utils.TextDrawable
import kr.ac.snu.hcil.datahalo.visconfig.MappingState
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable
import kr.ac.snu.hcil.datahalo.visconfig.VisVarCustomizability
import kr.ac.snu.hcil.datahalo.visconfig.IndependentVisObjectAnimParams
import kr.ac.snu.hcil.datahalo.visconfig.IndependentVisObjectDataParams
import kr.ac.snu.hcil.datahalo.visconfig.IndependentVisObjectVisParams

interface InterfaceVisObject{

    fun setID(id: Int)
    fun getID(): Int

    fun getPosition(): Pair<Double, Double>

    fun getMappingState(visVar: NuNotiVisVariable): MappingState?
    fun getVisVarsOf(mappingState: MappingState): List<NuNotiVisVariable>

    fun getVisMapping(): Map<NuNotiVisVariable, NotiProperty?>
    fun setVisMapping(mapping: Map<NuNotiVisVariable, NotiProperty?>)

    fun getDataParams(): IndependentVisObjectDataParams
    fun setDataParams(params: IndependentVisObjectDataParams)

    fun getAnimatorsByLifeStage(): Map<EnhancedNotificationLife, AnimatorSet>
    fun setAnimParams(vararg params: IndependentVisObjectAnimParams)

    fun getVisParams(): IndependentVisObjectVisParams
    fun setVisParams(params: IndependentVisObjectVisParams)


    fun conversionForPredefinedVisVar(predefinedVisVar: NuNotiVisVariable)
    fun conversionForBoundVisVar(boundVisVar: NuNotiVisVariable, notiProp:NotiProperty): (Any) -> Any?
    fun conversionForTransparentVisVar(transparentVisVar: NuNotiVisVariable): Any

    fun getDrawableWithAnimator(input:Map<NotiProperty, Any>): Pair<Drawable, AnimatorSet>
}

class IndependentVisObject(
        visualMapping: Map<NuNotiVisVariable, NotiProperty?>,
        visualParameters: IndependentVisObjectVisParams,
        dataParameters: IndependentVisObjectDataParams,
        animationParameters: List<IndependentVisObjectAnimParams>)
    : AbstractIndependentVisObject(
        customizabilitySpec = mapOf(
                NuNotiVisVariable.POSITION to VisVarCustomizability.CUSTOMIZABLE,
                NuNotiVisVariable.COLOR to VisVarCustomizability.CUSTOMIZABLE,
                NuNotiVisVariable.MOTION to VisVarCustomizability.CUSTOMIZABLE,
                NuNotiVisVariable.SHAPE to VisVarCustomizability.CUSTOMIZABLE,
                NuNotiVisVariable.SIZE to VisVarCustomizability.CUSTOMIZABLE
        ),
        transformationRule = mapOf(
                NuNotiVisVariable.POSITION to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT),
                NuNotiVisVariable.COLOR to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT),
                NuNotiVisVariable.MOTION to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT),
                NuNotiVisVariable.SHAPE to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT),
                NuNotiVisVariable.SIZE to listOf(NotiProperty.IMPORTANCE, NotiProperty.LIFE_STAGE, NotiProperty.CONTENT)
        ),
        visualMapping = visualMapping,
        visualParameters =  visualParameters,
        dataParameters =  dataParameters,
        animationParameters = animationParameters)


abstract class AbstractIndependentVisObject(
        val customizabilitySpec: Map<NuNotiVisVariable, VisVarCustomizability>,
        val transformationRule: Map<NuNotiVisVariable, List<NotiProperty>>,
        visualMapping: Map<NuNotiVisVariable, NotiProperty?>,
        visualParameters: IndependentVisObjectVisParams,
        dataParameters: IndependentVisObjectDataParams,
        animationParameters: List<IndependentVisObjectAnimParams>
): InterfaceVisObject{
    companion object {
        val exceptionInvalidCustomizability = Exception("CustomizabilitySpec May Lack Information.")
        val exceptionInvalidMappingInput = Exception("Input Mapping is Invalid")
        val exceptionNotInitialized = Exception("Object is Not Initialized. Set Mapping First.")
        val exceptionVisVariable = {visVar:NuNotiVisVariable -> Exception("Usage of $visVar is Invalid.")}
        val exceptionNotSupportedTransformation = {visVar:NuNotiVisVariable, notiProp:NotiProperty -> Exception("$notiProp -> $visVar Mapping Does Not Exist.")}
    }

    private var id: Int = -1
    private var visParams: IndependentVisObjectVisParams = visualParameters
    private var dataParams: IndependentVisObjectDataParams = dataParameters

    private lateinit var currMapping: Map<NuNotiVisVariable, NotiProperty?>
    lateinit var boundConversions: Map<NuNotiVisVariable, (Any) -> Any?>
    lateinit var userDefinedConversions: Map<NuNotiVisVariable, Any>
    private var position: Pair<Double, Double> = Pair(0.0, 0.0)
    private lateinit var animationMap: Map<EnhancedNotificationLife, AnimatorSet>

    init{
        setVisMapping(visualMapping)
        setAnimParams(*animationParameters.toTypedArray())
    }

    final override fun getAnimatorsByLifeStage(): Map<EnhancedNotificationLife, AnimatorSet> = animationMap
    final override fun setAnimParams(vararg params: IndependentVisObjectAnimParams) {
        val myMap: Map<EnhancedNotificationLife, MutableList<Animator>> = mapOf(
                EnhancedNotificationLife.STATE_1 to mutableListOf(),
                EnhancedNotificationLife.STATE_2 to mutableListOf(),
                EnhancedNotificationLife.STATE_3 to mutableListOf(),
                EnhancedNotificationLife.STATE_4 to mutableListOf(),
                EnhancedNotificationLife.STATE_5 to mutableListOf()
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
    }

    final override fun getMappingState(visVar: NuNotiVisVariable): MappingState? {
        if (customizabilitySpec[visVar] == null)
            exceptionInvalidCustomizability
        return when (customizabilitySpec[visVar]){
            VisVarCustomizability.PREDEFINED -> MappingState.PREDEFINED
            VisVarCustomizability.CUSTOMIZABLE -> {if(visVar in currMapping.keys) MappingState.BOUND else MappingState.TRANSPARENT}
            else -> null
        }
    }

    final override fun getVisVarsOf(mappingState: MappingState): List<NuNotiVisVariable> {
        return when (mappingState){
            MappingState.PREDEFINED -> { customizabilitySpec.filter{ it.value == VisVarCustomizability.PREDEFINED}.keys.toList()}
            MappingState.BOUND -> {boundConversions.keys.toList()}
            MappingState.TRANSPARENT -> {userDefinedConversions.keys.toList()}
        }
    }

    final override fun getVisMapping(): Map<NuNotiVisVariable, NotiProperty?> = currMapping
    final override fun setVisMapping(mapping: Map<NuNotiVisVariable, NotiProperty?>){
        currMapping = mapping

        //check if mapping is absurd
        val isValid = currMapping.keys.fold(true){
            acc:Boolean, el:NuNotiVisVariable ->
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

    final override fun conversionForBoundVisVar(boundVisVar: NuNotiVisVariable, notiProp: NotiProperty): (Any) -> Any?{
        val dataParams = getDataParams()
        val visualParams = getVisParams()

        when(boundVisVar){
            NuNotiVisVariable.MOTION -> {
                val motions = visualParams.motionList
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.importanceRange, motions)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        if(motions.size != dataParams.contentGroupList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.lifeList, motions)
                    }
                    NotiProperty.CONTENT -> {
                        if(motions.size != dataParams.contentGroupList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.contentGroupList, motions)
                    }
                    else -> {
                        return {x -> motions[0]}
                    }
                }
            }
            NuNotiVisVariable.SHAPE -> {
                val shape = visualParams.shapeList
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.importanceRange, shape)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        if(shape.size != dataParams.lifeList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.lifeList, shape)

                    }
                    NotiProperty.CONTENT -> {
                        if(shape.size != dataParams.contentGroupList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.contentGroupList, shape)
                    }
                    else -> {
                        return {x -> shape[0]}
                    }
                }
            }
            NuNotiVisVariable.POSITION -> {
                val posRange = visualParams.posRange// 이값은 어디선가 와야겠지?
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.importanceRange, posRange)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.lifeList, posRange)
                    }
                    NotiProperty.CONTENT -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.contentGroupList, posRange)
                    }
                    else -> {
                        return {x -> (posRange.first + posRange.second) / 2}
                    }
                }
            }
            NuNotiVisVariable.COLOR -> {
                val colors = visualParams.colorList
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.importanceRange, colors)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        if(colors.size != dataParams.lifeList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.lifeList, colors)
                    }
                    NotiProperty.CONTENT -> {
                        if(colors.size != dataParams.contentGroupList.size)
                            throw exceptionVisVariable(boundVisVar)
                        return MapFunctionUtilities.createMapFunc(dataParams.contentGroupList, colors)
                    }
                    else -> {
                        return {x -> colors[0]}
                    }
                }

            }
            NuNotiVisVariable.SIZE -> {
                val sizeRange = visualParams.sizeRange
                when(notiProp){
                    NotiProperty.IMPORTANCE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.importanceRange, sizeRange)
                    }
                    NotiProperty.LIFE_STAGE -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.lifeList, sizeRange)
                    }
                    NotiProperty.CONTENT -> {
                        return MapFunctionUtilities.createMapFunc(dataParams.contentGroupList, sizeRange)
                    }
                    else -> {
                        return {x -> (sizeRange.first + sizeRange.second) / 2}
                    }
                }

            }
        }
    }
    final override fun conversionForTransparentVisVar(transparentVisVar: NuNotiVisVariable): Any {
        val visualParams = getVisParams()

        when (transparentVisVar){
            NuNotiVisVariable.SIZE -> {
                return visualParams.selecteSize
            }
            NuNotiVisVariable.COLOR -> {
                return visualParams.selectedColor
            }
            NuNotiVisVariable.POSITION -> {
                return visualParams.selectedPos
            }
            NuNotiVisVariable.SHAPE -> {
                return visualParams.selectedShape
            }
            NuNotiVisVariable.MOTION -> {
                return visualParams.selectedMotion
            }
        }
    }

    final override fun conversionForPredefinedVisVar(predefinedVisVar: NuNotiVisVariable) {}
    final override fun getDrawableWithAnimator(input: Map<NotiProperty, Any>): Pair<Drawable, AnimatorSet> {

        val visualParams = getVisParams()

        var pos: Double = visualParams.selectedPos
        var size: Double = visualParams.selecteSize
        var shape: VisObjectShape = visualParams.selectedShape
        var color: Int = visualParams.selectedColor
        var motion: AnimatorSet = visualParams.selectedMotion

        input.forEach{ propertyToValue ->
            val notiProp: NotiProperty = propertyToValue.key
            val visVar: NuNotiVisVariable = currMapping.filter{ it.value == notiProp}.toList()[0].first
            val notiVal: Any = propertyToValue.value
            when(visVar){
                in boundConversions -> {
                    val f = boundConversions[visVar]!!
                    f(notiVal)?.let{ visVal ->
                        when(visVar){
                            NuNotiVisVariable.SIZE -> size = visVal as Double
                            NuNotiVisVariable.MOTION -> motion = visVal as AnimatorSet
                            NuNotiVisVariable.SHAPE -> shape = visVal as VisObjectShape
                            NuNotiVisVariable.POSITION -> pos = visVal as Double
                            NuNotiVisVariable.COLOR -> color = visVal as Int
                        }
                    }
                }
                in userDefinedConversions -> {
                    val visVal = userDefinedConversions[visVar]!!
                    when(visVar){
                        NuNotiVisVariable.SIZE -> size = visVal as Double
                        NuNotiVisVariable.MOTION -> motion = visVal as AnimatorSet
                        NuNotiVisVariable.SHAPE -> shape = visVal as VisObjectShape
                        NuNotiVisVariable.POSITION -> pos = visVal as Double
                        NuNotiVisVariable.COLOR -> color = visVal as Int
                    }
                }
                else -> {}
            }
        }

        position = Pair(
                visualParams.posRange.first + (visualParams.posRange.second - visualParams.posRange.first) * pos,
                visualParams.posRange.first + (visualParams.posRange.second - visualParams.posRange.first) * pos
        )

        //val width: Double = visualParams.wRange.first + (visualParams.wRange.second - visualParams.wRange.first) * size
        //val height: Double = visualParams.hRange.first + (visualParams.hRange.second - visualParams.hRange.first) * size

        val resultDrawable = ScaleDrawable(
                when(shape.type){
                    NewVisShape.RECT -> {
                        (shape.drawable as ShapeDrawable).also{
                            it.paint.color = color
                            //it.intrinsicWidth = Math.round(width).toInt()
                            //it.intrinsicHeight = Math.round(height).toInt()
                        }
                    }
                    NewVisShape.OVAL -> {
                        (shape.drawable as ShapeDrawable).also{
                            it.paint.color = color
                        }
                    }
                    NewVisShape.PATH -> {
                        (shape.drawable as ShapeDrawable).also{
                            it.paint.color = color
                        }
                    }
                    NewVisShape.IMAGE -> {
                        ShapeDrawable()
                    }
                    NewVisShape.RAW -> {
                        (shape.drawable as TextDrawable).also{
                            it.setColor(color)
                        }
                    }
                },
                Gravity.CENTER,
                1.0f,
                1.0f
        ).also{
            it.level = Math.round(10000 * size).toInt()
        }

        return Pair(resultDrawable, motion.also{it.setTarget(resultDrawable)})
    }

}


