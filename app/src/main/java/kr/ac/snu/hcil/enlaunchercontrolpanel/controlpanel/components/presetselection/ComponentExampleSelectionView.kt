package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

class ComponentExampleSelectionView : LinearLayout {

    companion object{
        private const val TAG = "ComponentExamplesView"
    }
    private val _exampleDataList: MutableList<HaloVisComponent> = mutableListOf()
    private var viewModel: AppHaloConfigViewModel? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: HaloVisComponentAdapter
    private var tracker: SelectionTracker<Long>? = null
    private var componentType: HaloVisComponent.HaloVisComponentType? = null
    private var initialized: Boolean = false

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    var exampleDataList : List<HaloVisComponent>
        get() = _exampleDataList.toList()
        set(value){
            _exampleDataList.clear()
            _exampleDataList.addAll(value)
            componentType = value[0].componentType
            recyclerViewAdapter.notifyDataSetChanged()
        }

    fun setViewModel(appConfigViewModel: AppHaloConfigViewModel){
        viewModel = appConfigViewModel
        tracker?.apply{
            appConfigViewModel.appHaloConfigLiveData.value?.let{ config ->
                initialized = false
                when (componentType){
                    HaloVisComponent.HaloVisComponentType.AGGREGATED_VISEFFECT -> {
                        val selectedId = VisEffectManager.availableAggregatedVisEffects.indexOf(config.aggregatedVisEffectName).toLong()
                        select(selectedId)
                    }
                    HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT -> {
                        val selectedId = VisEffectManager.availableIndependentVisEffects.indexOf(config.independentVisEffectName).toLong()
                        select(selectedId)
                    }
                    HaloVisComponent.HaloVisComponentType.VISEFFECT_LAYOUT -> {
                        val selectedId = AppHaloLayoutMethods.availiableLayouts.indexOf(config.haloLayoutMethodName).toLong()
                        select(selectedId)
                    }
                    HaloVisComponent.HaloVisComponentType.IMPORTANCE_SATURATION -> {
                        val selectedId = VisDataManager.exampleImportanceSaturationPatterns.toList().indexOfFirst { it.first == config.notificationEnhancementExamplePatternName}.toLong()
                        select(selectedId)
                    }
                    else -> {
                        //이리로 오게 된다는 얘기는 data call 순서에 문제가 있다는 뜻
                    }
                }
            }
        }
    }

    fun loadSelection(bundle: Bundle?){
        tracker?.apply{
            onRestoreInstanceState(bundle)
        }
    }
    fun saveSelection(bundle: Bundle){
        tracker?.onSaveInstanceState(bundle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        inflate(context, R.layout.layout_example_components_management_view, this)

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.ComponentExampleSelectionView, defStyle, 0)
        a.recycle()

        recyclerView = findViewById(R.id.exampleHaloVisComponentsRecyclerView)
        recyclerViewAdapter = HaloVisComponentAdapter(context, _exampleDataList)
        recyclerView.apply{
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        tracker = SelectionTracker.Builder<Long>(
                "haloComponentSelection",
                recyclerView,
                StableIdKeyProvider(recyclerView),
                HaloItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectSingleAnything()
        //selection predicate customization 할 수 있음
        ).build()


        tracker?.addObserver(object: SelectionTracker.SelectionObserver<Long>(){
                override fun onSelectionChanged() {
                    if(initialized)
                    {
                        Log.d(TAG, "On Selection Changed ${tracker?.selection?.size()}")
                        tracker?.selection?.map{id ->
                            val position = id.toInt()
                            val selected: HaloVisComponent = recyclerViewAdapter.componentList[position]
                            viewModel?.appHaloConfigLiveData?.value?.let{ appHaloConfig ->
                                when(selected.componentType){
                                    HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT -> {
                                        appHaloConfig.independentVisEffectName = selected.label!!
                                    }
                                    HaloVisComponent.HaloVisComponentType.AGGREGATED_VISEFFECT -> {
                                        appHaloConfig.aggregatedVisEffectName = selected.label!!
                                    }
                                    HaloVisComponent.HaloVisComponentType.VISEFFECT_LAYOUT -> {
                                        appHaloConfig.haloLayoutMethodName = selected.label!!
                                    }
                                    HaloVisComponent.HaloVisComponentType.IMPORTANCE_SATURATION -> {
                                        appHaloConfig.notificationEnhancementExamplePatternName = selected.label!!
                                        VisDataManager.getExampleSaturationPattern(selected.label!!)?.let{
                                            appHaloConfig.notificationEnhancementParams = it
                                        }
                                    }
                                    else ->{

                                    }
                                }
                                viewModel?.appHaloConfigLiveData?.value = appHaloConfig
                            }
                        }
                    }
                    else{ initialized = true }
                }
                override fun onItemStateChanged(key: Long, selected: Boolean) {
                    Log.d(TAG, "On Item at $key is Changed to $selected")
                    super.onItemStateChanged(key, selected)
                }

                override fun onSelectionRefresh() {
                    Log.d(TAG, "On Selection is Refreshed")
                    super.onSelectionRefresh()
                }
            })

        recyclerViewAdapter.tracker = tracker
    }

    private fun invalidateExampleList(){

    }
}
