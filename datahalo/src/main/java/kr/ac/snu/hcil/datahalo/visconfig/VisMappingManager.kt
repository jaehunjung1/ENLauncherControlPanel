package kr.ac.snu.hcil.datahalo.visconfig

class VisMappingManager{

    data class NotiPropertyAggregationRule(
            val property: NotiProperty,
            val aggType: NotiAggregationType,
            val groupByProperty: NotiProperty?)


    interface VisObjectMapping<T>{
        val customizability: Map<NotiVisVariable, VisVarCustomizability>
        val currentMap: MutableMap<NotiVisVariable, T>
    }

    data class IndependentVisObjectMapping(
            override val customizability: Map<NotiVisVariable, VisVarCustomizability>,
            override val currentMap: MutableMap<NotiVisVariable, NotiProperty>)
        : VisObjectMapping<NotiProperty>
    data class AggregatedVisObjectMapping(
            override val customizability: Map<NotiVisVariable, VisVarCustomizability>,
            override val currentMap: MutableMap<NotiVisVariable, NotiPropertyAggregationRule>)
        : VisObjectMapping<NotiPropertyAggregationRule>

    interface VisMapping<T>
            where T : VisObjectMapping<*> {
        val mappings: MutableList<T>
    }

    class IndependentVisMapping: VisMapping<IndependentVisObjectMapping> {
        override val mappings = mutableListOf<IndependentVisObjectMapping>()
    }

    class AggregatedVisMapping: VisMapping<AggregatedVisObjectMapping> {
        override val mappings: MutableList<AggregatedVisObjectMapping> = mutableListOf()
    }
}