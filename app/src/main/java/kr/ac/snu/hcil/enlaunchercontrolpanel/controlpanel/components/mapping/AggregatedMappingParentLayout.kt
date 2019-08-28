package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable

import kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisMappingRule

class AggregatedMappingParentLayout: LinearLayout {

    companion object{
        private const val TAG = "AggrMappingLayout"
    }

    interface GroupViewInteractionListener{
        fun onMappingUpdate(visVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, aggrOp: kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType, notiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?)
    }

    private var groupByNotiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty? = null
    private var notiVisVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable = kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable.MOTION
    private var notiDataProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty? = null
    private var aggregationOp: kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType = kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.COUNT
    private var objIndex: Int = -1
    private var viewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel? = null

    private var mappingChangedListener: GroupViewInteractionListener? = null

    private var aggrOpSpinnerInitialSet = false
    private var propSpinnerInitialSet = false

    fun setMappingChangedListener(listener: GroupViewInteractionListener){
        mappingChangedListener = listener
    }

    fun setProperties(groupNotiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?, visVar: kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable, aggrOp: kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType, notiProp: kr.ac.snu.hcil.datahalo.visconfig.NotiProperty?, index:Int, appConfigViewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel? = null){
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
                    kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.IMPORTANCE -> notiProp.ordinal + 1
                    kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.LIFE_STAGE -> notiProp.ordinal + 1
                    kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.CONTENT -> notiProp.ordinal + 1
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
            val newMapping: kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisMappingRule = appConfig.aggregatedVisualMappings[objIndex].let { currentRule ->
                kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisMappingRule(
                        currentRule.groupProperty,
                        currentRule.visMapping.mapValues {
                            if (it.key == notiVisVar) Pair(aggregationOp, notiDataProp) else it.value
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

        val selectedNotiPropertySpinner = findViewById<Spinner>(R.id.selected_noti_prop_spinner)
        val selectedAggrOperationSpinner = findViewById<Spinner>(R.id.selected_aggr_op_spinner)

        selectedNotiPropertySpinner.let { spinner ->
            spinner.isFocusable = false
            spinner.isFocusableInTouchMode = false

            val spinnerValues = kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.values().map { it.name }.toMutableList().let {
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
                    when(propVal){
                        kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.IMPORTANCE -> {
                            (selectedAggrOperationSpinner.adapter as AccessibilityControllableStringAdapter).apply{
                                setElementsEnabled(
                                        kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.MEAN_NUMERIC.name,
                                        kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.MAX_NUMERIC.name,
                                        kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.MIN_NUMERIC.name
                                )
                            }
                        }
                        kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.CONTENT -> {
                            (selectedAggrOperationSpinner.adapter as AccessibilityControllableStringAdapter).apply{
                                setElementsEnabled(
                                        kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.MOST_FREQUENT_NOMINAL.name
                                )
                            }
                        }
                        kr.ac.snu.hcil.datahalo.visconfig.NotiProperty.LIFE_STAGE -> {
                            (selectedAggrOperationSpinner.adapter as AccessibilityControllableStringAdapter).apply{
                                setElementsEnabled(
                                        kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.MOST_FREQUENT_NOMINAL.name
                                )
                            }
                        }
                        else -> {
                            (selectedAggrOperationSpinner.adapter as AccessibilityControllableStringAdapter).apply{
                                setElementsEnabled(
                                        kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.COUNT.name
                                )
                            }
                        }
                    }

                    notiDataProp = propVal
                    if (propSpinnerInitialSet) {
                        invalidateObjAndViewModel()
                    }
                    propSpinnerInitialSet = true
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }
        }

        selectedAggrOperationSpinner.let{spinner ->
            spinner.isFocusable = false
            spinner.isFocusableInTouchMode = false

            val spinnerValues = kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.values().map{ it.name }.toMutableList()

            val aggrOpSpinnerAdapter = getArrayAdapter(spinnerValues)
            aggrOpSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner)

            spinner.adapter = aggrOpSpinnerAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val spinnerVal = parent.getItemAtPosition(position) as String
                    val propVal = kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType.valueOf(spinnerVal)
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

    private fun getArrayAdapter(stringList: List<String>): AccessibilityControllableStringAdapter {
        return AccessibilityControllableStringAdapter(context, stringList)
    }

}