package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable

class IndependentMappingParentLayout : LinearLayout {
    companion object{
        private const val TAG = "IndepMappingLayout"
    }

    private var notiVisVar: NuNotiVisVariable = NuNotiVisVariable.MOTION
    private var notiDataProp: NotiProperty? = null
    private var objIndex: Int = -1
    private var viewModel: AppHaloConfigViewModel? = null

    private var initialSetFinished: Boolean = false

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    fun setProperties(visVar:NuNotiVisVariable, notiProp: NotiProperty?, index:Int, appConfigViewModel: AppHaloConfigViewModel? = null){
        notiVisVar = visVar
        notiDataProp = notiProp
        objIndex = index
        viewModel = appConfigViewModel
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.IndependentMappingParentLayout, defStyle, 0)

        a.recycle()
        View.inflate(context, R.layout.item_parent_expandablecontrolpanel, this)

        findViewById<Spinner>(R.id.selected_noti_prop_spinner).let{spinner ->

            val spinnerValues = NotiProperty.values().map{it.name}.toMutableList().let{
                it.add(0, "none")
                it.toList()
            }

            val notiPropSpinnerAdapter = getArrayAdapter(spinnerValues)
            notiPropSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner)

            spinner.adapter = notiPropSpinnerAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    val spinnerVal = adapterView.getItemAtPosition(i) as String
                    val propVal = if (spinnerVal == "none") null else NotiProperty.valueOf(spinnerVal)
                    notiDataProp = propVal
                    if(initialSetFinished){
                        invalidateObjAndViewModel()
                    }
                    initialSetFinished = true
                }
                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }
            spinner.setSelection(0)
        }
    }

    private fun invalidateObjAndViewModel(){
        findViewById<TextView>(R.id.selected_vis_var_text_view).text = notiVisVar.name

        viewModel?.appHaloConfigLiveData?.value?.let{appConfig ->
            val newMapping: Map<NuNotiVisVariable, NotiProperty?> = appConfig.independentVisualMappings[objIndex].mapValues{
                if(it.key == notiVisVar) notiDataProp else it.value
            }
            appConfig.independentVisualMappings[objIndex] = newMapping
            viewModel?.appHaloConfigLiveData?.value = appConfig
        }
    }

    private fun getArrayAdapter(stringList: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(context, R.layout.item_spinner, stringList) {
            override fun isEnabled(position: Int): Boolean {
                return true
            }

            override fun getDropDownView(position: Int, convertView: View?,
                                         parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }
        }
    }


}
