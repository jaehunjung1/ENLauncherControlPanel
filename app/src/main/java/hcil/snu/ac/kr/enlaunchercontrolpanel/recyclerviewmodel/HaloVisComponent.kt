package hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel

data class HaloVisComponent(var label: String? = null, var drawableId: Int = 0, val componentType: HaloVisComponentType){
    enum class HaloVisComponentType{
        IMPORTANCE_SATURATION,
        VISEFFECT_LAYOUT,
        INDEPENDENT_VISEFFECT,
        AGGREGATED_VISEFFECT
    }
}