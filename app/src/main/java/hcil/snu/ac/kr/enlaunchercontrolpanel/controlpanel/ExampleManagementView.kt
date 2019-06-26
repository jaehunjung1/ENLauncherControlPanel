package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponent
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponentAdapter
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig

/**
 * TODO: document your custom view class.
 */
class ExampleManagementView : LinearLayout {

    private val _exampleDataList: MutableList<HaloVisComponent> = mutableListOf()
    private var viewModel: AppHaloConfigViewModel? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: HaloVisComponentAdapter

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

        val addButon = findViewById<Button>(R.id.addButton).apply{
            setOnClickListener {
                recyclerViewAdapter.onItemClick = {
                    if(it == null){

                    }
                    else{
                        
                    }
                }
                _exampleDataList.add(HaloVisComponent("custom", R.drawable.kakaotalk_logo))
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun invalidateExampleList(){

    }
}
