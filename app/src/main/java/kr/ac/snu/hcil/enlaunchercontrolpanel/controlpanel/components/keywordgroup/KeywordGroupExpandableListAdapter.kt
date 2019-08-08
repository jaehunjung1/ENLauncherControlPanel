package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.KeywordGroupImportance
import kr.ac.snu.hcil.datahalo.visconfig.KeywordGroupImportancePatterns
import kr.ac.snu.hcil.datahalo.visconfig.NotificationEnhacementParams
import kr.ac.snu.hcil.datahalo.visconfig.WGBFilterVar

class KeywordGroupExpandableListAdapter(
        private var viewModel: AppHaloConfigViewModel
): BaseExpandableListAdapter() {
    companion object {
        private const val TAG = "Expandable_Keyword_Adapter"
    }

    private lateinit var orderedKeywordGroupImportancePatterns: KeywordGroupImportancePatterns
    private lateinit var observationWindowFilter: Map<WGBFilterVar, Any>

    init{
        viewModel.appHaloConfigLiveData.value?.let{ config ->
            orderedKeywordGroupImportancePatterns = config.keywordGroupPatterns
            observationWindowFilter = config.filterObservationWindowConfig
        }
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getGroupCount(): Int{
        return orderedKeywordGroupImportancePatterns.getOrderedKeywordGroups().size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return orderedKeywordGroupImportancePatterns.getGroupOfRank(groupPosition)!!.id
    }

    override fun getGroup(groupPosition: Int): KeywordGroupImportance {
        return orderedKeywordGroupImportancePatterns.getGroupOfRank(groupPosition)!!
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupData = getGroup(groupPosition)
        val context = parent!!.context

        return (convertView as KeywordGroupParentView?)?.apply{
            setProperties(groupData) //이거 뭔가 이상
        } ?: KeywordGroupParentView(context).apply{
            setProperties(groupData)
            notificationEnhacementParamChangedListener = object: KeywordGroupParentView.KeywordGroupParentInteractionListener{
                override fun onMappingUpdate(patternName: String) {
                    if(patternName != VisDataManager.CUSTOM_PATTERN){
                        orderedKeywordGroupImportancePatterns.setEnhancementParamOfGroup(groupData.group, patternName)
                        viewModel.appHaloConfigLiveData.value?.let{ config ->
                            config.keywordGroupPatterns.setEnhancementParamOfGroup(groupData.group, patternName)
                            viewModel.appHaloConfigLiveData.value = config
                        }
                    }
                    else{
                        orderedKeywordGroupImportancePatterns.setEnhancementParamOfGroup(groupData.group, groupData.enhancementParam)
                        viewModel.appHaloConfigLiveData.value?.let{ config ->
                            config.keywordGroupPatterns.setEnhancementParamOfGroup(groupData.group, groupData.enhancementParam)
                            viewModel.appHaloConfigLiveData.value = config
                        }
                    }
                }
            }
        }
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Pair<Set<String>, NotificationEnhacementParams>? {
        return getGroup(groupPosition)?.let{ Pair(it.keywords, it.enhancementParam) }
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupData = getGroup(groupPosition)
        val context = parent!!.context

        return (convertView as KeywordGroupChildView?)?.apply{
            setProperties(observationWindowFilter, groupData) //이거 뭔가 이상
        } ?: KeywordGroupChildView(context).apply{
            setProperties(observationWindowFilter, groupData)
            keywordGroupChildInteractionListener = object: KeywordGroupChildView.KeywordGroupChildInteractionListener{
                override fun onEnhancementParamUpdated(pattern: NotificationEnhacementParams) {
                    orderedKeywordGroupImportancePatterns.setEnhancementParamOfGroup(groupData.group, pattern)
                    viewModel.appHaloConfigLiveData.value?.let{ config ->
                        config.keywordGroupPatterns.setEnhancementParamOfGroup(groupData.group, pattern)
                        viewModel.appHaloConfigLiveData.value = config
                    }
                }
            }
        }

    }




}