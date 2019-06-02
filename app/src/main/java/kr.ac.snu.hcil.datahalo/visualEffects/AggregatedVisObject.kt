package kr.ac.snu.hcil.datahalo.visualEffects

import android.animation.AnimatorSet
import android.graphics.drawable.Drawable
import kr.ac.snu.hcil.datahalo.visconfig.*

interface InterfaceAggregatedVisObject{
    fun setID(id: Int)
    fun getID(): Int

    fun setPosition(x: Double, y: Double)
    fun getPosition(): Pair<Double, Double>

    fun getMappingState(visVar: NuNotiVisVariable): MappingState?
    fun getVisVarsOf(mappingState: MappingState): List<NuNotiVisVariable>

    fun getNotiPropertyGroupedBy(): NotiProperty?
    fun setNotiPropertyGroupedBy(groupNotiProp: NotiProperty?)

    fun getGroupLabel(): Any
    fun setGroupLabel(label: Any)

    fun getVisMapping(): Map<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>
    fun setVisMapping(mapping: Map<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>)

    fun getVisParams(): Map<String, Any>
    fun setVisParams(params: Map<String, Any>)

    fun conversionForPredefinedVisVar(predefinedVisVar: NuNotiVisVariable, params: Map<String, Any>)
    fun conversionForBoundVisVar(boundVisVar: NuNotiVisVariable, notiProp: Pair<NotiAggregationType, NotiProperty?>, params: Map<String, Any>): (Any) -> Unit
    fun conversionForTransparentVisVar(transparentVisVar: NuNotiVisVariable, params: Map<String, Any>): (Any) -> Unit

    fun getDrawableWithAnimator(input:Map<Pair<NotiAggregationType, NotiProperty?>, Any>): Pair<Drawable, AnimatorSet>
}

abstract class AbstractAggregatedVisObject(
        val customizabilitySpec: Map<NuNotiVisVariable, VisVarCustomizability>,
        val transformationRule: Map<NuNotiVisVariable, List<AggregatedNotiProperty>>,
        visualMapping: Map<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>,
        visualParameters: Map<String, Any>,
        dataParameters: Map<String, Any>,
        animParameters: Map<String, Any>
):InterfaceAggregatedVisObject
{
    companion object {
        val exceptionInvalidCustomizability = Exception("CustomizabilitySpec May Lack Information.")
        val exceptionInvalidMappingInput = Exception("Input Mapping is Invalid")
        val exceptionNotInitialized = Exception("Object is Not Initialized. Set Mapping First.")
        val exceptionVisVariable = {visVar:NuNotiVisVariable -> Exception("Usage of $visVar is Invalid.")}
        val exceptionNotSupportedTransformation = {visVar:NuNotiVisVariable, notiProp: AggregatedNotiProperty -> Exception("$notiProp -> $visVar Mapping Does Not Exist.")}
    }

    private var id: Int = -1
    private var visParams: Map<String, Any> = visualParameters
    private var notiPropGroupedBy: NotiProperty? = null
    private var groupLabel: Any = -1
    private lateinit var currMapping: Map<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>
    lateinit var boundConversions: Map<NuNotiVisVariable, (Any) -> Unit>
    lateinit var userDefinedConversions: Map<NuNotiVisVariable, (Any) -> Unit>
    private var position: Pair<Double, Double> = Pair(0.0, 0.0)

    init{
        customizabilitySpec.filter{it.value == VisVarCustomizability.PREDEFINED}.keys.map{
            conversionForPredefinedVisVar(it, visualParameters)
        }
        setVisMapping(visualMapping)
    }

    final override fun getID(): Int = id
    final override fun setID(id: Int) { this.id = id }

    final override fun getPosition(): Pair<Double, Double> = position
    final override fun setPosition(x:Double, y:Double){ position = Pair(x, y) }

    final override fun getNotiPropertyGroupedBy(): NotiProperty? = notiPropGroupedBy
    final override fun setNotiPropertyGroupedBy(groupNotiProp: NotiProperty?) { notiPropGroupedBy = groupNotiProp}

    final override fun getGroupLabel(): Any = groupLabel
    final override fun setGroupLabel(label: Any){ groupLabel = label}

    final override fun getVisParams(): Map<String, Any> = visParams
    final override fun setVisParams(params: Map<String, Any>) {
        visParams = params
        customizabilitySpec.filter{it.value == VisVarCustomizability.PREDEFINED}.keys.map{
            conversionForPredefinedVisVar(it, visParams)
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

    final override fun getVisMapping(): Map<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>> = currMapping
    final override fun setVisMapping(mapping: Map<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>){
        currMapping = mapping

        //check if mapping is absurd
        val isValid = currMapping.keys.fold(true){
            acc:Boolean, el:NuNotiVisVariable ->
            val truth = el in customizabilitySpec.filter{it.value == VisVarCustomizability.CUSTOMIZABLE}.keys
            acc && truth
        }

        if(!isValid)
            exceptionInvalidMappingInput

        val boundVisVars = currMapping.keys

        boundConversions = currMapping.mapValues{
            val visVar = it.key
            val notiProp = it.value
            val f: (Any) -> Unit = conversionForBoundVisVar(visVar, notiProp, visParams)
            f
        }


        val userDefinedVisVars = customizabilitySpec.filter{
            it.value == VisVarCustomizability.CUSTOMIZABLE}.keys.filter{
            it !in boundVisVars
        }

        userDefinedConversions = userDefinedVisVars.map{ visVar ->
            val f: (Any) -> Unit = conversionForTransparentVisVar(visVar, visParams)
            visVar to f
        }.toMap()
    }

}