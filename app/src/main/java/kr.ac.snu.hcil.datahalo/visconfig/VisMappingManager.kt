package kr.ac.snu.hcil.datahalo.visconfig

class VisMappingManager{

    data class NotiPropertyAggregationRule(
            val property: NotiProperty,
            val aggType: NotiAggregationType,
            val groupByProperty: NotiProperty?)


    interface VisObjectMapping<T>{
        val customizability: Map<NuNotiVisVariable, VisVarCustomizability>
        val currentMap: MutableMap<NuNotiVisVariable, T>
    }

    data class IndependentVisObjectMapping(
            override val customizability: Map<NuNotiVisVariable, VisVarCustomizability>,
            override val currentMap: MutableMap<NuNotiVisVariable, NotiProperty>)
        : VisObjectMapping<NotiProperty>
    data class AggregatedVisObjectMapping(
            override val customizability: Map<NuNotiVisVariable, VisVarCustomizability>,
            override val currentMap: MutableMap<NuNotiVisVariable, NotiPropertyAggregationRule>)
        : VisObjectMapping<NotiPropertyAggregationRule>

    interface VisMapping<T>
            where T : VisObjectMapping<*>{
        val mappings: MutableList<T>
    }

    class IndependentVisMapping: VisMapping<IndependentVisObjectMapping>{
        override val mappings = mutableListOf<IndependentVisObjectMapping>()
    }

    class AggregatedVisMapping: VisMapping<AggregatedVisObjectMapping>{
        override val mappings: MutableList<AggregatedVisObjectMapping> = mutableListOf()
    }
}