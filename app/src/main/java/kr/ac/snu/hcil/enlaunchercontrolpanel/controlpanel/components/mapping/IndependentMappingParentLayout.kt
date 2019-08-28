package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable

class IndependentMappingParentLayout : LinearLayout {
    companion object{
        private const val TAG = "IndepMappingLayout"
    }

    interface GroupViewInteractionListener{
        fun onMappingUpdate(visVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, notiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?)
    }

    private var notiVisVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable = kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable.MOTION
    private var notiDataProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty? = null
    private var objIndex: Int = -1
    private var viewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel? = null

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

    fun setProperties(visVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, notiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?, index:Int, appConfigViewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel? = null){
        notiVisVar = visVar
        notiDataProp = notiProp
        objIndex = index
        viewModel = appConfigViewModel
        findViewById<TextView>(R.id.selected_vis_var_text_view).text = notiVisVar.name
        findViewById<Spinner>(R.id.selected_noti_prop_spinner).setSelection(
                when(notiProp){
                    null -> 0
                    kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.IMPORTANCE -> notiProp.ordinal + 1
                    kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.LIFE_STAGE -> notiProp.ordinal + 1
                    kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.CONTENT -> notiProp.ordinal + 1
                }
        )
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.IndependentMappingParentLayout, defStyle, 0)

        a.recycle()
        View.inflate(context, R.layout.item_parent_indep_expandable_controlpanel, this)

        findViewById<Spinner>(R.id.selected_noti_prop_spinner).let{spinner ->
            spinner.isFocusable = false
            spinner.isFocusableInTouchMode = false

            val spinnerValues = kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.values().map{it.name}.toMutableList().let{
                it.add(0, "none")
                it.toList()
            }

            val notiPropSpinnerAdapter = getArrayAdapter(spinnerValues)
            notiPropSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner)

            spinner.adapter = notiPropSpinnerAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    val spinnerVal = adapterView.getItemAtPosition(i) as String
                    val propVal = if (spinnerVal == "none") null else kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.valueOf(spinnerVal)
                    notiDataProp = propVal
                    if(initialSetFinished){
                        invalidateObjAndViewModel()
                    }
                    initialSetFinished = true
                }
                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }
        }
    }

    fun setMappingChangedListener(listener: GroupViewInteractionListener){
        mappingChangedListener = listener
    }

    private fun invalidateObjAndViewModel(){
        viewModel?.appHaloConfigLiveData?.value?.let{ appConfig ->
            val newMapping: Map<kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?> = appConfig.independentVisualMappings[objIndex].mapValues{
                if(it.key == notiVisVar) notiDataProp else it.value
            }
            appConfig.independentVisualMappings[objIndex] = newMapping
            viewModel?.appHaloConfigLiveData?.value = appConfig
            mappingChangedListener?.onMappingUpdate(notiVisVar, notiDataProp)
        }
    }

    private fun getArrayAdapter(stringList: List<String>): AccessibilityControllableStringAdapter{
        return AccessibilityControllableStringAdapter(context, stringList)
    }


}
