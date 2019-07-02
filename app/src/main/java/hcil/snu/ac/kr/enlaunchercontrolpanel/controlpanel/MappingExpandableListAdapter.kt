package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.IndependentVisObjectDataParams
import kr.ac.snu.hcil.datahalo.visconfig.IndependentVisObjectVisParams
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable

class MappingExpandableListAdapter: BaseExpandableListAdapter() {
    companion object{
        private const val TAG = "Expandable_Mapping_Adapter"
        private fun exceptionVisVarDuplicated(visVar: NuNotiVisVariable) = Exception("$TAG: $visVar Duplicated")
    }

    private val visVartoNotiPropMappings: MutableList<Pair<NuNotiVisVariable, NotiProperty?>> = mutableListOf()
    private var viewModel: AppHaloConfigViewModel? = null



    fun setViewModel(appConfigViewModel: AppHaloConfigViewModel){
        viewModel = appConfigViewModel
        appConfigViewModel.appHaloConfigLiveData.value?.let{appHaloConfig ->
            visVartoNotiPropMappings.clear()
            visVartoNotiPropMappings.addAll(appHaloConfig.independentVisualMappings[0].toList())
            //mapping 맞춰서 가져오기
        }
        notifyDataSetInvalidated()
    }

    /*

    private fun setUnitMappAndContents(mapping: Pair<NuNotiVisVariable, NotiProperty?>, contents: List<Pair<Any, Any>>){
        when(visVartoNotiPropMappings.filter{mapping.first == it.first}.size){
            0 -> {
                visVartoNotiPropMappings.add(mapping)
                visValtoNotiValMappings.add(contents)
            }
            1 -> {
                val index = visVartoNotiPropMappings.indexOfFirst {mapping.first == it.first}
                visVartoNotiPropMappings[index] = mapping
                visValtoNotiValMappings[index] = contents
            }
            else -> {
                exceptionVisVarDuplicated(mapping.first)
            }
        }
    }

    fun setMappingAndContents(mapping: Pair<NuNotiVisVariable, NotiProperty?>, contents: List<Pair<Any, Any>>) {
        setUnitMappAndContents(mapping, contents)
        notifyDataSetChanged()
    }

    fun setMappingAndContents(mappingList: List<Pair<NuNotiVisVariable, NotiProperty?>>, contentsList: List<List<Pair<Any, Any>>>){
        for(i in mappingList.indices){
            val mapping = mappingList[i]
            val contents = contentsList[i]
            setUnitMappAndContents(mapping, contents)
        }
        notifyDataSetChanged()
    }

    fun clearMappingAndContents(){
        visVartoNotiPropMappings.clear()
        visValtoNotiValMappings.clear()
        notifyDataSetInvalidated()
    }
    */

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getGroupCount(): Int {
        return visVartoNotiPropMappings.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroup(groupPosition: Int): Pair<NuNotiVisVariable, NotiProperty?> {
        return visVartoNotiPropMappings[groupPosition]
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val (visVar, notiProp) = getGroup(groupPosition)
        val context = parent!!.context

        return IndependentMappingParentLayout(context).apply{
            setProperties(visVar, notiProp, 0, viewModel)
        }
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Pair<IndependentVisObjectVisParams, IndependentVisObjectDataParams>? {
        return viewModel?.appHaloConfigLiveData?.value?.let{
            Pair(it.independentVisualParameters[0], it.independentDataParameters[0])
        }
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val (visVar, notiProp) = getGroup(groupPosition)
        val context = parent!!.context

        return IndependentMappingChildLayout(context).apply{
            setProperties(visVar, notiProp, 0, viewModel)
        }
    }
}