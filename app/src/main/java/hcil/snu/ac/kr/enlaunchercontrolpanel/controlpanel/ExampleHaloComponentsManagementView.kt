package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloItemDetailsLookup
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponent
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponentAdapter
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

class ExampleHaloComponentsManagementView : LinearLayout {

    private val _exampleDataList: MutableList<HaloVisComponent> = mutableListOf()
    private var viewModel: AppHaloConfigViewModel? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: HaloVisComponentAdapter
    private var tracker: SelectionTracker<Long>? = null

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
            recyclerViewAdapter.notifyDataSetChanged()
        }

    fun setViewModel(appConfigViewModel: AppHaloConfigViewModel){
        viewModel = appConfigViewModel
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        inflate(context, R.layout.layout_example_components_management_view, this)

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.ExampleManagementView, defStyle, 0)
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
        ).build().apply{
            addObserver(object: SelectionTracker.SelectionObserver<Long>(){
                override fun onSelectionChanged() {
                    this@apply.selection.map{id ->
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
                                    //appConfig의 layout 이름 바꾸면 런타임에 알아서 가져오는듯?
                                    appHaloConfig.haloLayoutMethodName = selected.label!!
                                }
                                HaloVisComponent.HaloVisComponentType.IMPORTANCE_SATURATION -> {
                                    VisDataManager.getExampleSaturationPattern(selected.label!!)?.let{
                                        appHaloConfig.notificationEnhancementParams = it
                                    }
                                }
                            }
                            viewModel?.appHaloConfigLiveData?.value = appHaloConfig
                        }
                    }
                }
            })
        }
    }

    private fun invalidateExampleList(){

    }
}
