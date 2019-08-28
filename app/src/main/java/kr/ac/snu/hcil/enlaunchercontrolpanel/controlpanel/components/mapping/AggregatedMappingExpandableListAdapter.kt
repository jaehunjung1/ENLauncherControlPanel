package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.*
import kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType

class AggregatedMappingExpandableListAdapter: BaseExpandableListAdapter() {

    companion object{
        private const val TAG = "Expandable_Mapping_Adapter"
        private fun exceptionVisVarDuplicated(visVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable) = Exception("$TAG: $visVar Duplicated")
    }

    interface MappingParameterChangedListener{
        fun onShapeParameterChanged(index: Int, visShapeType: kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType)
    }

    var shapeMappingParameterChangedListener: MappingParameterChangedListener? = null

    private var groupByNotiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty? = null
    private val visVarsToAggregatedNotiProperty: MutableList<Pair<kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, Pair<kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType, kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?>>> = mutableListOf()
    private var viewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel? = null

    fun setViewModel(appConfigViewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel){
        viewModel = appConfigViewModel

        appConfigViewModel.appHaloConfigLiveData.value?.let{ appHaloConfig ->
            val rule = appHaloConfig.aggregatedVisualMappings[0]
            groupByNotiProp = rule.groupProperty
            visVarsToAggregatedNotiProperty.clear()
            visVarsToAggregatedNotiProperty.addAll(rule.visMapping.toList())
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
        return visVarsToAggregatedNotiProperty.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroup(groupPosition: Int): Pair<kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, Pair<kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType, kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?>> {
        return visVarsToAggregatedNotiProperty[groupPosition]
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val (visVar, aggregatedNotiProp) = getGroup(groupPosition)
        val context = parent!!.context

        return (convertView as AggregatedMappingParentLayout?)?.apply{
            setProperties(groupByNotiProp, visVar, aggregatedNotiProp.first, aggregatedNotiProp.second, 0, viewModel )
            setMappingChangedListener(object: AggregatedMappingParentLayout.GroupViewInteractionListener {
                override fun onMappingUpdate(visVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, aggrOp: kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType, notiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?) {
                    visVarsToAggregatedNotiProperty[visVarsToAggregatedNotiProperty.indexOfFirst{it.first == visVar}] = Pair(visVar, Pair(aggrOp, notiProp))
                    notifyDataSetChanged()
                }
            })
        } ?: AggregatedMappingParentLayout(context).apply{
            setProperties(groupByNotiProp, visVar, aggregatedNotiProp.first, aggregatedNotiProp.second, 0, viewModel )
            setMappingChangedListener(object: AggregatedMappingParentLayout.GroupViewInteractionListener {
                override fun onMappingUpdate(visVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, aggrOp: kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType, notiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?) {
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

    override fun getChild(groupPosition: Int, childPosition: Int): Pair<kr.ac.snu.hcil.datahalo.visconfig.AggregatedVisObjectVisParams, kr.ac.snu.hcil.datahalo.visconfig.AggregatedVisObjectDataParams>? {
        return viewModel?.appHaloConfigLiveData?.value?.let{
            Pair(it.aggregatedVisualParameters[0], it.aggregatedDataParameters[0])
        }
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val (visVar, aggregatedNotiProp) = getGroup(groupPosition)
        val context = parent!!.context

        return (convertView as AggregatedMappingChildLayout?)?.apply{
            setProperties(groupByNotiProp, visVar, aggregatedNotiProp.first, aggregatedNotiProp.second, 0, viewModel)
            setMappingContentsChangedListener(object: AggregatedMappingChildLayout.ChildViewInteractionListener{
                override fun onShapeMappingContentsUpdated(componentIndex: Int, shapeType: kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType) {
                    shapeMappingParameterChangedListener?.onShapeParameterChanged(componentIndex, shapeType)
                }
            })
        }?: AggregatedMappingChildLayout(context).apply{
            setProperties(groupByNotiProp, visVar, aggregatedNotiProp.first, aggregatedNotiProp.second, 0, viewModel)
            setMappingContentsChangedListener(object: AggregatedMappingChildLayout.ChildViewInteractionListener{
                override fun onShapeMappingContentsUpdated(componentIndex: Int, shapeType: kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType) {
                    shapeMappingParameterChangedListener?.onShapeParameterChanged(componentIndex, shapeType)
                }
            })
        }
    }
}