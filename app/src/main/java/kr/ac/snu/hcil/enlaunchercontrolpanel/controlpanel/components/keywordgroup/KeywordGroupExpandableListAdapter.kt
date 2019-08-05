package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.KeywordGroupImportance
import kr.ac.snu.hcil.datahalo.visconfig.KeywordGroupImportancePatterns
import kr.ac.snu.hcil.datahalo.visconfig.NotificationEnhacementParams

class KeywordGroupExpandableListAdapter(
        private var viewModel: AppHaloConfigViewModel
): BaseExpandableListAdapter() {
    companion object {
        private const val TAG = "Expandable_Keyword_Adapter"
    }

    //여기서 원하는 건
    //Parent Level에서는  그룹, Child Level에서는 pattern의 세부설정과 매핑해야 함
    private lateinit var orderedKeywordGroupImportancePatterns: KeywordGroupImportancePatterns

    init{
        viewModel.appHaloConfigLiveData.value?.let{ config ->
            orderedKeywordGroupImportancePatterns = config.keywordGroupPatterns
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }




}