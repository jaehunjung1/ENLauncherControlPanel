package kr.ac.snu.hcil.enlaunchercontrolpanel

import android.animation.AnimatorSet
import android.app.Activity
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
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

import kr.ac.snu.hcil.enlaunchercontrolpanel.utilities.Utilities
import kotlinx.android.synthetic.main.independent_mapping_layout.view.*
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape


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

    // 모든 액션은 얘를 수정시켜야 하고, 그 결과를 얘에 반영시켜야 함
    private val visVarContents: MutableList<Any> = mutableListOf()
    private val notiDataPropContents: MutableList<Any> = mutableListOf()

    companion object{
        private const val TAG = "IndepMappingLayout"
        private fun exceptionKeywordGroupNotExist(groupName: String) = Exception("Cannot Access $groupName When Creating Keyword Frame")
        private fun exceptionInvalidInputToView(viewType: String, content: String) = Exception("Invalid Input in $viewType, $content")
    }

    init {
        View.inflate(getContext(), R.layout.independent_mapping_layout, this)

        visvar_textview.text = visVar.name

        val spinnerValues = NotiProperty.values().map{it.name}.toMutableList().let{
            it.add(0, "none")
            it.toList()
        }
        notiPropSpinnerAdapter = getArrayAdapter(spinnerValues)
        notiPropSpinnerAdapter.setDropDownViewResource(R.layout.item_spinner)
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
                val spinnerVal = adapterView.getItemAtPosition(i) as String
                val propVal = if (spinnerVal == "none") null else NotiProperty.valueOf(spinnerVal)
                notiDataProp = propVal

                if(initialSetFinished){
                    showMappingDialog()
                }
                initialSetFinished = true
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
            // TODO set Mapping Container 일단 가져와야지 다
            viewModel.appHaloConfigLiveData.value?.let{ currentConfig ->
                val newMapping = currentConfig.independentVisualMappings[objIndex].mapValues{ entry ->
                    if(entry.key == notiVisVar)
                        notiDataProp
                    else
                        entry.value
                }
                currentConfig.independentVisualMappings[objIndex] = newMapping

                when(notiVisVar){
                    NuNotiVisVariable.MOTION -> {
                        currentConfig.independentVisualParameters[objIndex].selectedMotionList = (visVarContents as MutableList<AnimatorSet>).toList()
                    }
                    NuNotiVisVariable.COLOR -> {
                        //TODO(값 체크)
                        currentConfig.independentVisualParameters[objIndex].selectedColorList = (visVarContents as MutableList<Int>).toList()
                    }
                    NuNotiVisVariable.SHAPE -> {
                        currentConfig.independentVisualParameters[objIndex].selectedShapeList = (visVarContents as MutableList<VisObjectShape>).toList()
                    }
                    NuNotiVisVariable.SIZE -> {
                        currentConfig.independentVisualParameters[objIndex].selectedSizeRangeList = (visVarContents as MutableList<Pair<Double, Double>>).toList()
                    }
                    NuNotiVisVariable.POSITION -> {
                        currentConfig.independentVisualParameters[objIndex].selectedPosRangeList = (visVarContents as MutableList<Pair<Double, Double>>).toList()
                    }
                }

                when(notiDataProp){
                    NotiProperty.LIFE_STAGE -> {
                        currentConfig.independentDataParameters[objIndex].selectedLifeList = (notiDataPropContents as MutableList<EnhancedNotificationLife>).toList()
                    }
                    NotiProperty.IMPORTANCE -> {
                        currentConfig.independentDataParameters[objIndex].selectedImportanceRangeList = (notiDataPropContents as MutableList<Pair<Double, Double>>).toList()
                    }
                    NotiProperty.CONTENT -> {
                        currentConfig.independentDataParameters[objIndex].keywordGroupMap = (notiDataPropContents as MutableList<Pair<String, MutableList<String>>>).toMap()
                    }
                    else -> {}
                }

                viewModel.appHaloConfigLiveData.value = currentConfig
            }

            mDialog.dismiss()
        }
        dialogCancel.setOnClickListener { mDialog.dismiss() }

    }

    private fun addKeywordToFlowLayout(flowLayout: FlowLayout, keyword: String, inflater: LayoutInflater) {
        (inflater.inflate(R.layout.chip_view_layout, null) as Chip).let{ chipView ->
            chipView.chipText = keyword
            chipView.setOnCloseClickListener {
                chipView.visibility = View.GONE
                (chipView.parent as ViewGroup).removeView(chipView)
                (notiDataPropContents as MutableList<Pair<String, MutableList<String>>>)
                        .find{keywordGroups -> keywordGroups.first == (flowLayout.tag as String) }
                        ?.let{ keywordGroup -> keywordGroup.second.remove(keyword) }
            }
            flowLayout.addView(chipView)
        }
    }

    private fun setKeywordFrameView(
            notiDataPropContents: MutableList<Pair<String, MutableList<String>>>,
            notiPropDialogList: LinearLayout,
            inflater: LayoutInflater
            ){
        notiDataPropContents.forEachIndexed { index, keywordGroup ->
            (notiPropDialogList.getChildAt(index) as FrameLayout).also { frame ->
                frame.removeAllViews()
                frame.layoutParams.width = Utilities.dpToPx(context, 170)
                val keywordFrame = inflater.inflate(R.layout.layout_keyword_mapping, parent as ViewGroup, false) as LinearLayout

                val flowLayout = keywordFrame.findViewById<FlowLayout>(R.id.mapping_keyword_flowLayout).apply{
                    tag = keywordGroup.first
                    keywordGroup.second.forEach{keyword ->  addKeywordToFlowLayout(this, keyword, inflater)}
                }

                keywordFrame.findViewById<ImageButton>(R.id.mapping_keyword_add_button).let{ addButton ->
                    addButton.setOnClickListener {
                        val mDialog = AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar).let { mBuilder ->
                            val editText = EditText(context)
                            mBuilder.setView(editText)
                            mBuilder.setPositiveButton("OK") { dialogInterface, i ->
                                val newKeyword = editText.text.toString()
                                if(newKeyword.isNotEmpty() && newKeyword !in keywordGroup.second){
                                    keywordGroup.second.add(newKeyword)
                                    addKeywordToFlowLayout(flowLayout, newKeyword, inflater)
                                }
                            }
                            mBuilder.setNegativeButton("Cancel") { _, _ -> }
                            mBuilder.create()
                        }
                        mDialog.show()
                    }
                }
                frame.addView(keywordFrame)
            }
        }
    }

    private fun setSpinnerView(selectedPropContentsIndices: List<Int>, givenPropContents: List<Any>, notiPropDialogList: LinearLayout){
        val givenPropStringContents: List<String> =
                when(notiDataProp){
                    NotiProperty.IMPORTANCE -> {givenPropContents.map{
                        val propContent = it as Pair<Double, Double>
                        "${"%.1f".format(propContent.first)}-${"%.1f".format(propContent.second)}"}
                    }
                    NotiProperty.LIFE_STAGE -> {givenPropContents.map{
                        val propContent = it as EnhancedNotificationLife
                        propContent.name }
                    }
                    else -> {
                        emptyList()
                    }
                }

        givenPropContents.toMutableList().add(0, "None")
        givenPropContents.toList()

        val spinnerAdapter = getArrayAdapter(givenPropStringContents)
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner)
        selectedPropContentsIndices.forEachIndexed{ index, indexVal ->
            (notiPropDialogList.getChildAt(index) as FrameLayout).also{ frame ->
                frame.removeAllViews()

                val spinner = Spinner(context)
                spinner.adapter = spinnerAdapter
                spinner.setSelection(indexVal) //indexVal 번째 순서에 있는 givenPropContent
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                        if(i == 0){
                            //TODO(mapping)
                            //notiDataPropContents[index] = selectedData
                        }else{
                            val selectedData = givenPropContents[i - 1]
                            notiDataPropContents[index] = selectedData
                        }

                    }
                    override fun onNothingSelected(adapterView: AdapterView<*>) {}
                }

                frame.addView(spinner)
            }
        }
    }

    private fun setVisVarFrame(index: Int, frame: FrameLayout, content: Any ){
        frame.background = ContextCompat.getDrawable(context, R.drawable.rounded_rectangle)
        frame.addView(TextView(context).apply { text = contentToString(notiVisVar, content) })
        when(notiVisVar){
            NuNotiVisVariable.MOTION -> {
                //TODO(Shape Selection View)
            }
            NuNotiVisVariable.COLOR -> {
                val origColor = content as Int
                frame.setBackgroundColor(origColor)
                frame.setOnClickListener {
                    ColorPicker(context as Activity,
                            Color.red(origColor), Color.green(origColor), Color.blue(origColor)
                    ).let { cp ->
                        cp.enableAutoClose()
                        cp.setCallback { color ->
                            visVarContents[index] = color
                            frame.setBackgroundColor(color)
                            Log.d(TAG, "$color has Picked. ${visVarContents[index]}")
                        }
                        cp.show()
                    }
                }
            }
            NuNotiVisVariable.SHAPE -> {
                //TODO(Shape Selection View)
            }
            NuNotiVisVariable.SIZE -> {}
            NuNotiVisVariable.POSITION -> {}
        }
    }

    private fun contentToString(notiVisVar: NuNotiVisVariable, content: Any): String{
        return when (notiVisVar){
            NuNotiVisVariable.MOTION -> content.toString()
            NuNotiVisVariable.SHAPE -> (content as VisObjectShape).type.name
            NuNotiVisVariable.COLOR -> (content as Int).let{"R:${Color.red(it)}, G:${Color.green(it)}, B:${Color.blue(it)}"}
            NuNotiVisVariable.SIZE -> (content as Pair<Double, Double>).let{"${"%.1f".format(it.first)}-${"%.1f".format(it.second)}"}
            NuNotiVisVariable.POSITION -> (content as Pair<Double, Double>).let{"${"%.1f".format(it.first)}-${"%.1f".format(it.second)}"}
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
            val independentVisParams = configToLookUp.independentVisualParameters[objIndex]
            val independentDataParams = configToLookUp.independentDataParameters[objIndex]

            visVarContents.clear()
            visVarContents.addAll(
                    when (notiVisVar) {
                        NuNotiVisVariable.MOTION -> {
                            independentVisParams.selectedMotionList
                        }
                        NuNotiVisVariable.SHAPE -> {
                            independentVisParams.selectedShapeList
                        }
                        NuNotiVisVariable.COLOR -> {
                            independentVisParams.selectedColorList
                        }
                        NuNotiVisVariable.SIZE -> {
                            MapFunctionUtilities.bin(
                                    independentVisParams.selectedSizeRange,
                                    5)
                        }
                        NuNotiVisVariable.POSITION -> {
                            MapFunctionUtilities.bin(
                                    independentVisParams.selectedPosRange,
                                    5)
                        }
                    }
            )

            visVarContents.forEachIndexed { index, content ->
                val frame = visVarDialogList.getChildAt(index) as FrameLayout
                frame.removeAllViews()
                if (notiDataProp != null){
                    setVisVarFrame(index, frame, content)
                }
                else{
                    if(index == 0)
                        setVisVarFrame(index, frame, content)
                }
            }

            notiDataPropContents.clear()
            notiDataPropContents.addAll(
                    when (notiDataProp) {
                        null -> emptyList()
                        NotiProperty.IMPORTANCE -> {
                            independentDataParams.selectedImportanceRangeList
                        }
                        NotiProperty.LIFE_STAGE -> {
                            independentDataParams.selectedLifeList
                        }
                        NotiProperty.CONTENT -> {
                            independentDataParams.keywordGroupMap.toList()
                        }
                    }
            )

            when (notiDataProp) {
                NotiProperty.CONTENT -> {
                    setKeywordFrameView(notiDataPropContents as MutableList<Pair<String, MutableList<String>>>, notiPropDialogList, inflater)
                }
                NotiProperty.LIFE_STAGE -> {
                    val givenLifeStageIndices = (notiDataPropContents as MutableList<EnhancedNotificationLife>).map{independentDataParams.givenLifeList.indexOf(it)}

                    setSpinnerView(givenLifeStageIndices, independentDataParams.givenLifeList, notiPropDialogList)
                }
                NotiProperty.IMPORTANCE -> {
                    val givenImportanceList = MapFunctionUtilities.bin(independentDataParams.givenImportanceRange, 5)
                    val givenImportanceIndices = (notiDataPropContents as MutableList<Pair<Double, Double>>).map{givenImportanceList.indexOf(it)}

                    setSpinnerView(
                            givenImportanceIndices,
                            givenImportanceList,
                            notiPropDialogList)
                }
                null -> {

                }
            }
        }
    }

    private fun getArrayAdapter(stringList: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(context, R.layout.item_spinner, stringList) {
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
