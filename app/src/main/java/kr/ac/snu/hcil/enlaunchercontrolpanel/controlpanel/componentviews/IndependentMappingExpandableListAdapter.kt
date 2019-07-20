package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.componentviews

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.*

class IndependentMappingExpandableListAdapter: BaseExpandableListAdapter() {
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

        return (convertView as IndependentMappingParentLayout?)?.apply{
            setProperties(visVar, notiProp, 0, viewModel)
        } ?: IndependentMappingParentLayout(context).apply{
            setProperties(visVar, notiProp, 0, viewModel)
            setMappingChangedListener(object: IndependentMappingParentLayout.GroupViewInteractionListener{
                override fun onMappingUpdate(visVar: NuNotiVisVariable, notiProp: NotiProperty?) {
                    visVartoNotiPropMappings[visVartoNotiPropMappings.indexOfFirst{it.first == visVar}] = Pair(visVar, notiProp)
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