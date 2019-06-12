package hcil.snu.ac.kr.enlaunchercontrolpanel

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView

import com.nex3z.flowlayout.FlowLayout
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import com.robertlevonyan.views.chip.Chip

import java.util.ArrayList
import java.util.Locale

import hcil.snu.ac.kr.enlaunchercontrolpanel.utilities.Utilities
import kotlinx.android.synthetic.main.independent_mapping_layout.view.*
import kotlinx.android.synthetic.main.layout_nominal_mapping.view.*
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable


/*
 * Mapping Information Container
 * 1) Each mapping layout configures exactly one mapping info container instance
 * - VisVar: Nominal ( motion, shape, color ) / Continuous ( position, size )
 * - NotiProp: Nominal ( interaction stage, keyword ) / Continuous ( importance )
 *
 * 2) i) VisVar: Nominal, NotiProp: Nominal: visVar is fixed, and noti prop has 5 frames
 *   with respective spinner
 *   ii) VisVar: Nominal, NotiProp: Continuous: visVar is same as above, and noti prop has 5 frames
 *   with respective spinner (each spinner has numerical interval as item )
 *   iii) VisVar: Continuous, NotiProp: Nominal: visVar is fixed as list of numerical intervals,
 *   and noti prop has 5 frames with respective spinner
 *   iv) VisVar: Continuous, NotiProp: Continuous: the only nasty case!!!
 *   (net-shaped graph, with start & end value respectively )
 *
 */

class IndependentMappingLayout(
        context: Context,
        visVar: NuNotiVisVariable,
        private val objIndex: Int,
        private val viewModel: AppHaloConfigViewModel
) : LinearLayout(context) {

    private val notiVisVar: NuNotiVisVariable = visVar
    private var notiDataProp: NotiProperty? = null
    private var notiPropSpinnerAdapter: ArrayAdapter<String>
    private var initialSetFinished: Boolean = false

    init {
        View.inflate(getContext(), R.layout.independent_mapping_layout, this)

        visvar_textview.text = visVar.name

        val spinnerValues = NotiProperty.values().map{it.name}.toMutableList().let{
            it.add(0, "none")
            it.toList()
        }
        notiPropSpinnerAdapter = getArrayAdapter(spinnerValues)
        notiPropSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        notiProp_spinner.adapter = notiPropSpinnerAdapter

        viewModel.appHaloConfigLiveData.value?.let{
            val mappedProp = it.independentVisualMappings[objIndex][visVar]
            notiDataProp = mappedProp
            notiProp_spinner.setSelection(
                    when(mappedProp){
                        null -> 0
                        NotiProperty.IMPORTANCE -> mappedProp.ordinal
                        NotiProperty.LIFE_STAGE -> mappedProp.ordinal
                        NotiProperty.CONTENT -> mappedProp.ordinal
                    }
            )
        }

        // set item click listener for notiPropSpinner
        notiProp_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                //i = 0을 none으로 사용하고 있는건가
                if( i != 0 ){
                    val spinnerVal = adapterView.getItemAtPosition(i) as String
                    val propVal = if (spinnerVal == "none") null else NotiProperty.valueOf(spinnerVal)
                    notiDataProp = propVal

                    if(initialSetFinished){
                        showMappingDialog()
                    }
                    initialSetFinished = true
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        setOnClickListener {
            if (notiProp_spinner.selectedItemPosition != 0)
                showMappingDialog()
        }
    }

    private fun showMappingDialog() {
        val mBuilder = AlertDialog.Builder(
                context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar
        )
        val dialogLayout = (context as Activity).layoutInflater
                .inflate(R.layout.dialog_mapping, parent as ViewGroup, false) as LinearLayout
        mBuilder.setView(dialogLayout)
        val mDialog = mBuilder.create()
        mDialog.show()


        // if both continuous, need to create the continuous graph
        if((notiVisVar == NuNotiVisVariable.SIZE || notiVisVar == NuNotiVisVariable.POSITION) &&
                (notiDataProp == NotiProperty.IMPORTANCE)
        ) {


        } else {
            setDiscreteMappingContent(dialogLayout)
        }

        val frameLayout = dialogLayout.findViewById<FrameLayout>(R.id.dialog_frame_layout)


        val dialogDone = dialogLayout.findViewById<View>(R.id.dialog_done)
        val dialogCancel = dialogLayout.findViewById<View>(R.id.dialog_cancel)
        dialogDone.setOnClickListener {
            // TODO set Mapping Container

            mDialog.dismiss()
        }
        dialogCancel.setOnClickListener { mDialog.dismiss() }

    }

    private fun addKeywordFrameView(
            notiDataProp: NotiProperty?,
            notiDataPropContents: List<String>,
            notiPropDialogList: LinearLayout,
            inflater: LayoutInflater,
            configToLookUp: AppHaloConfig
            ){
        notiDataPropContents.forEachIndexed { index, content ->
            (notiPropDialogList.getChildAt(index) as FrameLayout).also { frame ->
                frame.layoutParams.width = Utilities.dpToPx(context, 170)
                val keywordFrame = inflater.inflate(R.layout.layout_keyword_mapping, parent as ViewGroup, false) as LinearLayout
                val flowLayout = keywordFrame.findViewById<FlowLayout>(R.id.mapping_keyword_flowLayout)

                configToLookUp.independentDataParameters[objIndex].contentGroupMap[content]?.forEach { keyword ->
                    addKeywordToFlowLayout(flowLayout, keyword, inflater)
                }

                val addButton = keywordFrame.findViewById<ImageButton>(R.id.mapping_keyword_add_button)
                addButton.setOnClickListener {
                    val mBuilder = AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar)
                    val editText = EditText(context)
                    mBuilder.setView(editText)
                    mBuilder.setPositiveButton("OK") { dialogInterface, i -> addKeywordToFlowLayout(flowLayout, editText.text.toString(), inflater) }
                    mBuilder.setNegativeButton("Cancel") { dialogInterface, i -> }

                    val mDialog = mBuilder.create()
                    mDialog.show()
                }
                frame.addView(keywordFrame)
                //Todo(update된 내용을 옮겨야 됨)
            }
        }
    }

    private fun addSpinnerView(notiDataProp: NotiProperty?, notiDataPropContents: List<String>,  notiPropDialogList: LinearLayout){
        val spinnerAdapter = getArrayAdapter(notiDataPropContents)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        notiDataPropContents.forEachIndexed{ index, _ ->
            (notiPropDialogList.getChildAt(index) as FrameLayout).also{ frame ->
                val spinner = Spinner(context)
                spinner.adapter = spinnerAdapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                        val text = adapterView.getItemAtPosition(i) as String
                        //Todo(update된 내용을 옮겨야 됨)
                    }
                    override fun onNothingSelected(adapterView: AdapterView<*>) {}
                }
                frame.addView(spinner)
            }
        }
    }

    private fun setDiscreteMappingContent(dialogLayout: ViewGroup) {
        val inflater = (context as Activity).layoutInflater

        (dialogLayout.findViewById<View>(R.id.dialog_frame_layout) as FrameLayout).addView(
                inflater.inflate(R.layout.layout_nominal_mapping, parent as ViewGroup, false)
        )

        dialogLayout.findViewById<TextView>(R.id.vis_var_text_view).apply{
            text = notiVisVar.name
        }
        dialogLayout.findViewById<TextView>(R.id.noti_property_text_view).apply{
            text = notiDataProp?.name ?: ""
        }

        val visVarDialogList = dialogLayout.findViewById<LinearLayout>(R.id.vis_var_dialog_list)
        val notiPropDialogList = dialogLayout.findViewById<LinearLayout>(R.id.noti_prop_dialog_list)

        // Load Settings From appHaloConfig
        viewModel.appHaloConfigLiveData.value?.let { configToLookUp ->
            val visVarContents: List<Any> =
                    when (notiVisVar) {
                        NuNotiVisVariable.MOTION -> {
                            configToLookUp.independentVisualParameters[objIndex].motionList
                        }
                        NuNotiVisVariable.SHAPE -> {
                            configToLookUp.independentVisualParameters[objIndex].shapeList
                        }
                        NuNotiVisVariable.COLOR -> {
                            configToLookUp.independentVisualParameters[objIndex].colorList
                        }
                        NuNotiVisVariable.SIZE -> {
                            MapFunctionUtilities.bin(
                                    configToLookUp.independentVisualParameters[objIndex].sizeRange,
                                    5)
                        }
                        NuNotiVisVariable.POSITION -> {
                            MapFunctionUtilities.bin(
                                    configToLookUp.independentVisualParameters[objIndex].posRange,
                                    5)
                        }
                    }

            if(notiDataProp == null){
                val content = visVarContents[0]
                (visVarDialogList.getChildAt(0) as FrameLayout).also { frame ->
                    frame.background = ContextCompat.getDrawable(context, R.drawable.rounded_rectangle)
                    frame.addView(TextView(context).apply { text = content.toString() })
                    if (notiVisVar == NuNotiVisVariable.COLOR) {
                        val origColor = content as Int
                        frame.setBackgroundColor(origColor)
                        frame.setOnClickListener {
                            ColorPicker(context as Activity,
                                    Color.red(origColor), Color.green(origColor), Color.blue(origColor)
                            ).let { cp ->
                                cp.show()
                                cp.enableAutoClose()
                                cp.setCallback { color -> frame.setBackgroundColor(color) }
                            }
                        }
                    }
                }
            }
            else{
                visVarContents.forEachIndexed { index, content ->
                    (visVarDialogList.getChildAt(index) as FrameLayout).also { frame ->
                        frame.background = ContextCompat.getDrawable(context, R.drawable.rounded_rectangle)
                        frame.addView(TextView(context).apply { text = content.toString() })
                        if (notiVisVar == NuNotiVisVariable.COLOR) {
                            val origColor = content as Int
                            frame.setBackgroundColor(origColor)
                            frame.setOnClickListener {
                                ColorPicker(context as Activity,
                                        Color.red(origColor), Color.green(origColor), Color.blue(origColor)
                                ).let { cp ->
                                    cp.show()
                                    cp.enableAutoClose()
                                    cp.setCallback { color -> frame.setBackgroundColor(color) }
                                }
                            }
                        }
                    }
                }
            }

            val notiDataPropContents: List<String> =
                    when (notiDataProp) {
                        null -> emptyList()
                        NotiProperty.IMPORTANCE -> {
                            MapFunctionUtilities.bin(
                                    configToLookUp.independentDataParameters[objIndex].importanceRange,
                                    5).map { "${"%.1f".format(it.first)}-${"%.1f".format(it.second)}" }
                        }
                        NotiProperty.LIFE_STAGE -> {
                            configToLookUp.independentDataParameters[objIndex].lifeList.map { it.name }
                        }
                        NotiProperty.CONTENT -> {
                            configToLookUp.independentDataParameters[objIndex].contentGroupMap.keys.toList()
                        }
                        else -> emptyList()
                    }

            when (notiDataProp) {
                NotiProperty.CONTENT -> {
                    addKeywordFrameView(notiDataProp, notiDataPropContents, notiPropDialogList, inflater, configToLookUp)
                }
                NotiProperty.LIFE_STAGE -> {
                    addSpinnerView(notiDataProp, notiDataPropContents, notiPropDialogList)
                }
                NotiProperty.IMPORTANCE -> {
                    addSpinnerView(notiDataProp, notiDataPropContents, notiPropDialogList)
                }
                null -> {

                }
                else -> {

                }
            }
        }
    }

    private fun addKeywordToFlowLayout(flowLayout: FlowLayout, keyword: String, inflater: LayoutInflater) {
        val chipView = inflater.inflate(R.layout.chip_view_layout, null) as Chip
        chipView.chipText = keyword
        flowLayout.addView(chipView)
        chipView.setOnCloseClickListener {
            chipView.visibility = View.GONE
            (chipView.parent as ViewGroup).removeView(chipView)
        }
    }

    private fun getArrayAdapter(stringList: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(context, R.layout.spinner_item, stringList) {
            override fun isEnabled(position: Int): Boolean {
                return true
                /*
                return if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    false
                } else {
                    true
                }
                */
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
