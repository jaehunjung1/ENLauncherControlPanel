package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable

import kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisMappingRule

class AggregatedMappingParentLayout: LinearLayout {

    companion object{
        private const val TAG = "AggrMappingLayout"
    }

    interface GroupViewInteractionListener{
        fun onMappingUpdate(visVar: NotiVisVariable, aggrOp: NotiAggregationType, notiProp: NotiProperty?)
    }

    private var groupByNotiProp: NotiProperty? = null
    private var notiVisVar: NotiVisVariable = NotiVisVariable.MOTION
    private var notiDataProp: NotiProperty? = null
    private var aggregationOp: NotiAggregationType = NotiAggregationType.COUNT
    private var objIndex: Int = -1
    private var viewModel: AppHaloConfigViewModel? = null

    private var mappingChangedListener: GroupViewInteractionListener? = null

    private var aggrOpSpinnerInitialSet = false
    private var propSpinnerInitialSet = false

    fun setMappingChangedListener(listener: GroupViewInteractionListener){
        mappingChangedListener = listener
    }

    fun setProperties(groupNotiProp: NotiProperty?, visVar: NotiVisVariable, aggrOp: NotiAggregationType, notiProp: NotiProperty?, index:Int, appConfigViewModel: AppHaloConfigViewModel? = null){
        groupByNotiProp = groupNotiProp
        notiVisVar = visVar
        notiDataProp = notiProp
        aggregationOp = aggrOp
        objIndex = index
        viewModel = appConfigViewModel

        findViewById<TextView>(R.id.selected_vis_var_text_view).text = notiVisVar.name

        findViewById<Spinner>(R.id.selected_aggr_op_spinner).setSelection(aggrOp.ordinal)

        findViewById<Spinner>(R.id.selected_noti_prop_spinner).setSelection(
                when(notiProp){
                    null -> 0
                    NotiProperty.IMPORTANCE -> notiProp.ordinal + 1
                    NotiProperty.LIFE_STAGE -> notiProp.ordinal + 1
                    NotiProperty.CONTENT -> notiProp.ordinal + 1
                }
        )
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }


    private fun invalidateObjAndViewModel(){
        viewModel?.appHaloConfigLiveData?.value?.let{ appConfig ->
            val newMapping: AggregatedVisMappingRule = appConfig.aggregatedVisualMappings[objIndex].let { currentRule ->
                AggregatedVisMappingRule(
                        currentRule.groupProperty,
                        currentRule.visMapping.mapValues{
                            if(it.key == notiVisVar) Pair(aggregationOp, notiDataProp) else it.value
                        }
                )
            }
            appConfig.aggregatedVisualMappings[objIndex] = newMapping
            viewModel?.appHaloConfigLiveData?.value = appConfig
            mappingChangedListener?.onMappingUpdate(notiVisVar, aggregationOp, notiDataProp)
        }
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.AggregatedMappingParentLayout, defStyle, 0)
        a.recycle()
        View.inflate(context, R.layout.item_parent_aggre_expandable_controlpanel, this)

        findViewById<Spinner>(R.id.selected_noti_prop_spinner).let { spinner ->
            spinner.isFocusable = false
            spinner.isFocusableInTouchMode = false

            val spinnerValues = NotiProperty.values().map { it.name }.toMutableList().let {
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
                    if (propSpinnerInitialSet) {
                        invalidateObjAndViewModel()
                    }
                    propSpinnerInitialSet = true
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }
        }

        findViewById<Spinner>(R.id.selected_aggr_op_spinner).let{spinner ->
            spinner.isFocusable = false
            spinner.isFocusableInTouchMode = false

            val spinnerValues = NotiAggregationType.values().map{ it.name }.toMutableList()

            val aggrOpSpinnerAdapter = getArrayAdapter(spinnerValues)
            aggrOpSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner)

            spinner.adapter = aggrOpSpinnerAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val spinnerVal = parent.getItemAtPosition(position) as String
                    val propVal = NotiAggregationType.valueOf(spinnerVal)
                    aggregationOp = propVal
                    if(aggrOpSpinnerInitialSet){
                        invalidateObjAndViewModel()
                    }
                    aggrOpSpinnerInitialSet = true
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
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