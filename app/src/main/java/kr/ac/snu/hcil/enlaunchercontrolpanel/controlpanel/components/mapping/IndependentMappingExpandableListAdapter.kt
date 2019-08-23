package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.*
import kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType

class IndependentMappingExpandableListAdapter: BaseExpandableListAdapter() {
    companion object{
        private const val TAG = "Expandable_Mapping_Adapter"
        private fun exceptionVisVarDuplicated(visVar: NotiVisVariable) = Exception("$TAG: $visVar Duplicated")
    }

    interface MappingParameterChangedListener{
        fun onShapeParameterChanged(index: Int, visShapeType: VisShapeType)
    }

    var shapeMappingParameterChangedLister: MappingParameterChangedListener? = null

    private val visVartoNotiPropMappings: MutableList<Pair<NotiVisVariable, NotiProperty?>> = mutableListOf()
    private var viewModel: AppHaloConfigViewModel? = null

    fun setViewModel(appConfigViewModel: AppHaloConfigViewModel){
        viewModel = appConfigViewModel

        appConfigViewModel.appHaloConfigLiveData.value?.let{appHaloConfig ->
            visVartoNotiPropMappings.clear()
            visVartoNotiPropMappings.addAll(appHaloConfig.independentVisualMappings[0].toList())
        }
        notifyDataSetChanged()
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

    override fun getGroup(groupPosition: Int): Pair<NotiVisVariable, NotiProperty?> {
        return visVartoNotiPropMappings[groupPosition]
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val (visVar, notiProp) = getGroup(groupPosition)
        val context = parent!!.context

        return (convertView as IndependentMappingParentLayout?)?.apply{
            setProperties(visVar, notiProp, 0, viewModel)
            setMappingChangedListener(object: IndependentMappingParentLayout.GroupViewInteractionListener {
                override fun onMappingUpdate(visVar: NotiVisVariable, notiProp: NotiProperty?) {
                    visVartoNotiPropMappings[visVartoNotiPropMappings.indexOfFirst{it.first == visVar}] = Pair(visVar, notiProp)
                    notifyDataSetChanged()
                }
            })
        } ?: IndependentMappingParentLayout(context).apply{
            setProperties(visVar, notiProp, 0, viewModel)
            setMappingChangedListener(object: IndependentMappingParentLayout.GroupViewInteractionListener {
                override fun onMappingUpdate(visVar: NotiVisVariable, notiProp: NotiProperty?) {
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

        return (convertView as IndependentMappingChildLayout?)?.apply{
            setProperties(visVar, notiProp, 0, viewModel)
            setMappingContentsChangedListener(object : IndependentMappingChildLayout.ChildViewInteractionListener {
                override fun onShapeMappingContentsUpdated(componentIndex: Int, shapeType: VisShapeType) {
                    shapeMappingParameterChangedLister?.onShapeParameterChanged(componentIndex, shapeType)
                }
            })
        }?:IndependentMappingChildLayout(context).apply {
            setProperties(visVar, notiProp, 0, viewModel)
            setMappingContentsChangedListener(object : IndependentMappingChildLayout.ChildViewInteractionListener {
                override fun onShapeMappingContentsUpdated(componentIndex: Int, shapeType: VisShapeType) {
                    shapeMappingParameterChangedLister?.onShapeParameterChanged(componentIndex, shapeType)
                }
            })
        }
    }
}