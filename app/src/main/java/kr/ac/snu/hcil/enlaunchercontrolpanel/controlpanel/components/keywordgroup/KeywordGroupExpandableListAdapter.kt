package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.*

class KeywordGroupExpandableListAdapter(
        private var viewModel: AppHaloConfigViewModel
): BaseExpandableListAdapter() {
    companion object {
        private const val TAG = "ExpandKeywordAdapter"
    }

    private lateinit var keywordGroupImportancePatterns: KeywordGroupImportancePatterns
    private lateinit var observationWindowFilter: Map<WGBFilterVar, Any>

    init{
        viewModel.appHaloConfigLiveData.value?.let{ config ->
            keywordGroupImportancePatterns = config.keywordGroupPatterns
            observationWindowFilter = config.filterObservationWindowConfig
        }
    }

    fun setAppConfig(appHaloConfig: AppHaloConfig){
        keywordGroupImportancePatterns = appHaloConfig.keywordGroupPatterns
        observationWindowFilter = appHaloConfig.filterObservationWindowConfig
        notifyDataSetInvalidated()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getGroupCount(): Int{
        return (keywordGroupImportancePatterns.getOrderedKeywordGroupImportancePatternsWithRemainder().size)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return if(groupPosition == groupCount - 1)
            keywordGroupImportancePatterns.getRemainderKeywordGroupPattern().id
        else
            keywordGroupImportancePatterns.getGroupOfRank(groupPosition)!!.id
    }

    override fun getGroup(groupPosition: Int): KeywordGroupImportance {
        return if(groupPosition == groupCount - 1) {
            keywordGroupImportancePatterns.getRemainderKeywordGroupPattern()
        }
        else {
            Log.i(TAG, "groupPosition: $groupPosition, $groupCount")
            keywordGroupImportancePatterns.getGroupOfRank(groupPosition)!!
        }
    }

    private fun createGroupViewListener(groupPosition: Int, groupData: KeywordGroupImportance): KeywordGroupParentView.KeywordGroupParentInteractionListener{
        return object: KeywordGroupParentView.KeywordGroupParentInteractionListener{
            override fun onMappingUpdate(patternName: String) {
                if(patternName != VisDataManager.CUSTOM_PATTERN){
                    //Predefined Pattern으로의 변경
                    if(groupPosition == groupCount - 1)
                        keywordGroupImportancePatterns.setRemainderKeywordGroupEnhancementParams(patternName)
                    else
                        keywordGroupImportancePatterns.setEnhancementParamOfGroup(groupData.group, patternName)

                    viewModel.appHaloConfigLiveData.value?.let{ config ->
                        if(groupPosition == groupCount - 1)
                            config.keywordGroupPatterns.setRemainderKeywordGroupEnhancementParams(patternName)
                        else
                            config.keywordGroupPatterns.setEnhancementParamOfGroup(groupData.group, patternName)
                        viewModel.appHaloConfigLiveData.value = config
                    }
                }
                else{
                    //Custom Pattern으로의 변경
                    if(groupPosition == groupCount - 1)
                        keywordGroupImportancePatterns.setRemainderKeywordGroupEnhancementParams(groupData.enhancementParam)
                    else
                        keywordGroupImportancePatterns.setEnhancementParamOfGroup(groupData.group, groupData.enhancementParam)

                    viewModel.appHaloConfigLiveData.value?.let{ config ->
                        if(groupPosition == groupCount - 1)
                            config.keywordGroupPatterns.setRemainderKeywordGroupEnhancementParams(groupData.enhancementParam)
                        else
                            config.keywordGroupPatterns.setEnhancementParamOfGroup(groupData.group, groupData.enhancementParam)
                        viewModel.appHaloConfigLiveData.value = config

                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupData = getGroup(groupPosition)
        val context = parent!!.context

        return (convertView as KeywordGroupParentView?)?.apply{
            setProperties(groupData)
            notificationEnhancementParamChangedListener = createGroupViewListener(groupPosition, groupData)
        } ?: KeywordGroupParentView(context).apply{
            setProperties(groupData)
            notificationEnhancementParamChangedListener = createGroupViewListener(groupPosition, groupData)
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

    private fun createChildViewListener(groupPosition: Int, groupData:KeywordGroupImportance): KeywordGroupChildView.KeywordGroupChildInteractionListener {
        return object: KeywordGroupChildView.KeywordGroupChildInteractionListener{
            override fun onEnhancementParamUpdated(pattern: NotificationEnhacementParams) {
                if(groupPosition == groupCount - 1)
                    keywordGroupImportancePatterns.setRemainderKeywordGroupEnhancementParams(pattern)
                else
                    keywordGroupImportancePatterns.setEnhancementParamOfGroup(groupData.group, pattern)
                viewModel.appHaloConfigLiveData.value?.let{ config ->

                    if(groupPosition == groupCount - 1)
                        config.keywordGroupPatterns.setRemainderKeywordGroupEnhancementParams(pattern)
                    else
                        config.keywordGroupPatterns.setEnhancementParamOfGroup(groupData.group, pattern)
                    viewModel.appHaloConfigLiveData.value = config
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupData = getGroup(groupPosition)
        val context = parent!!.context

        return (convertView as KeywordGroupChildView?)?.apply{
           setProperties(observationWindowFilter, groupData)
            keywordGroupChildInteractionListener = createChildViewListener(groupPosition, groupData)

        } ?: KeywordGroupChildView(context).apply{
            setProperties(observationWindowFilter, groupData)
            keywordGroupChildInteractionListener = createChildViewListener(groupPosition, groupData)
        }

    }




}