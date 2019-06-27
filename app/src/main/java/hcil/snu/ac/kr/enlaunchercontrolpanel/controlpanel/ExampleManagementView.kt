package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloItemDetailsLookup
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponent
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponentAdapter
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

/**
 * TODO: document your custom view class.
 */
class ExampleManagementView : LinearLayout {

    private val _exampleDataList: MutableList<HaloVisComponent> = mutableListOf()
    private var viewModel: AppHaloConfigViewModel? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: HaloVisComponentAdapter
    private var tracker: SelectionTracker<Long>? = null

    private var currentHaloComponent: HaloVisComponent? = null
    private var tempCounter: Int = 1

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
            invalidateExampleList()
        }

    fun setViewModel(appConfigViewModel: AppHaloConfigViewModel){
        viewModel = appConfigViewModel
        invalidateExampleList()
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        inflate(context, R.layout.layout_example_management_view, this)

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.ExampleManagementView, defStyle, 0)
        a.recycle()

        recyclerView = findViewById(R.id.exampleRecyclerView)
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

                }
            })
        }

        val addButon = findViewById<Button>(R.id.addButton).apply{
            setOnClickListener {
                if(currentHaloComponent != null){

                }
                _exampleDataList.add(HaloVisComponent("custom_${tempCounter++}", R.drawable.kakaotalk_logo))
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun invalidateExampleList(){

    }
}
