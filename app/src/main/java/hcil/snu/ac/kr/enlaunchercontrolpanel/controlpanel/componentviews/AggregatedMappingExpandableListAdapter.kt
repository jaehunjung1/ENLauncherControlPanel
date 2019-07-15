package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel.componentviews

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.*

class AggregatedMappingExpandableListAdapter: BaseExpandableListAdapter() {

    companion object{
        private const val TAG = "Expandable_Mapping_Adapter"
        private fun exceptionVisVarDuplicated(visVar: NuNotiVisVariable) = Exception("$TAG: $visVar Duplicated")
    }

    private var groupByNotiProp: NotiProperty? = null
    private val visVarsToAggregatedNotiProperty: MutableList<Pair<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>>> = mutableListOf()
    private var viewModel: AppHaloConfigViewModel? = null

    fun setViewModel(appConfigViewModel: AppHaloConfigViewModel){
        viewModel = appConfigViewModel

        appConfigViewModel.appHaloConfigLiveData.value?.let{ appHaloConfig ->
            val rule = appHaloConfig.aggregatedVisualMappings[0]
            groupByNotiProp = rule.groupProperty
            visVarsToAggregatedNotiProperty.clear()
            visVarsToAggregatedNotiProperty.addAll(rule.visMapping.toList())
        }

        notifyDataSetInvalidated()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getGroupCount(): Int {
        return visVarsToAggregatedNotiProperty.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroup(groupPosition: Int): Pair<NuNotiVisVariable, Pair<NotiAggregationType, NotiProperty?>> {
        return visVarsToAggregatedNotiProperty[groupPosition]
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val (visVar, aggregatedNotiProp) = getGroup(groupPosition)
        val context = parent!!.context

        return (convertView as AggregatedMappingParentLayout?)?.apply{
            setProperties(groupByNotiProp, visVar, aggregatedNotiProp.first, aggregatedNotiProp.second, 0, viewModel )
        } ?: AggregatedMappingParentLayout(context).apply{
            setProperties(groupByNotiProp, visVar, aggregatedNotiProp.first, aggregatedNotiProp.second, 0, viewModel )
            setMappingChangedListener(object: AggregatedMappingParentLayout.GroupViewInteractionListener{
                override fun onMappingUpdate(visVar: NuNotiVisVariable, aggrOp: NotiAggregationType, notiProp: NotiProperty?) {
                    visVarsToAggregatedNotiProperty[visVarsToAggregatedNotiProperty.indexOfFirst{it.first == visVar}] = Pair(visVar, Pair(aggrOp, notiProp))
                    notifyDataSetChanged()
                }
            })
        }
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Pair<AggregatedVisObjectVisParams, AggregatedVisObjectDataParams>? {
        return viewModel?.appHaloConfigLiveData?.value?.let{
            Pair(it.aggregatedVisualParameters[0], it.aggregatedDataParameters[0])
        }
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val (visVar, aggregatedNotiProp) = getGroup(groupPosition)
        val context = parent!!.context

        return AggregatedMappingChildLayout(context).apply{
            setProperties(groupByNotiProp, visVar, aggregatedNotiProp.first, aggregatedNotiProp.second, 0, viewModel)
        }
    }
}