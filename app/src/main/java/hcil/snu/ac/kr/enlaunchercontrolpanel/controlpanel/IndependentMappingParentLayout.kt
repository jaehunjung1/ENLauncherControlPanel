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

    interface GroupViewInteractionListener{
        fun onMappingUpdate(visVar: NuNotiVisVariable, notiProp: NotiProperty?)
    }

    private var notiVisVar: NuNotiVisVariable = NuNotiVisVariable.MOTION
    private var notiDataProp: NotiProperty? = null
    private var objIndex: Int = -1
    private var viewModel: AppHaloConfigViewModel? = null
    private var adapter: MappingExpandableListAdapter? = null

    private var initialSetFinished: Boolean = false

    private var mappingChangedListener: GroupViewInteractionListener? = null


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    fun setProperties(visVar:NuNotiVisVariable, notiProp: NotiProperty?, index:Int, appConfigViewModel: AppHaloConfigViewModel? = null, expandableAdapter: MappingExpandableListAdapter){
        notiVisVar = visVar
        notiDataProp = notiProp
        objIndex = index
        viewModel = appConfigViewModel
        adapter = expandableAdapter
        findViewById<TextView>(R.id.selected_vis_var_text_view).text = notiVisVar.name
        findViewById<Spinner>(R.id.selected_noti_prop_spinner).setSelection(
                when(notiProp){
                    null -> 0
                    NotiProperty.IMPORTANCE -> notiProp.ordinal + 1
                    NotiProperty.LIFE_STAGE -> notiProp.ordinal + 1
                    NotiProperty.CONTENT -> notiProp.ordinal + 1
                }
        )
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.IndependentMappingParentLayout, defStyle, 0)

        a.recycle()
        View.inflate(context, R.layout.item_parent_expandablecontrolpanel, this)

        findViewById<Spinner>(R.id.selected_noti_prop_spinner).let{spinner ->
            spinner.isFocusable = false
            spinner.isFocusableInTouchMode = false

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
            mappingChangedListener = null
        }
    }

    fun setMappingChangedListener(listener: GroupViewInteractionListener){
        mappingChangedListener = listener
    }

    private fun invalidateObjAndViewModel(){
        viewModel?.appHaloConfigLiveData?.value?.let{ appConfig ->
            val newMapping: Map<NuNotiVisVariable, NotiProperty?> = appConfig.independentVisualMappings[objIndex].mapValues{
                if(it.key == notiVisVar) notiDataProp else it.value
            }
            appConfig.independentVisualMappings[objIndex] = newMapping
            viewModel?.appHaloConfigLiveData?.value = appConfig
            mappingChangedListener?.onMappingUpdate(notiVisVar, notiDataProp)
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
