package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.nex3z.flowlayout.FlowLayout
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import com.robertlevonyan.views.chip.Chip
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.utilities.ContToContUI
import hcil.snu.ac.kr.enlaunchercontrolpanel.utilities.Utilities
import kotlinx.android.synthetic.main.aggregated_mapping_layout.view.*
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

class IndependentMappingChildLayout : LinearLayout {
    companion object{
        private const val TAG = "Inde_Map_Child"
    }

    interface ChildViewInteractionListener{
        fun onMappingContentsUpdated()
    }

    private var notiVisVar: NuNotiVisVariable = NuNotiVisVariable.MOTION
    private var notiDataProp: NotiProperty? = null
    private var viewModel: AppHaloConfigViewModel? = null
    private var objIndex: Int = -1

    private var mappingContentsChangedListener: ChildViewInteractionListener? = null

    private val visVarContents: MutableList<Any> = mutableListOf()
    private val notiDataPropContents: MutableList<Any> = mutableListOf()


    constructor(context: Context) : super(context) {
        init(null, 0)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    fun setProperties(
            visVar: NuNotiVisVariable,
            notiProp: NotiProperty?,
            visObjIndex: Int,
            appConfigViewModel: AppHaloConfigViewModel? = null
    ){
        notiVisVar = visVar
        notiDataProp = notiProp
        objIndex = visObjIndex
        viewModel = appConfigViewModel


        appConfigViewModel?.appHaloConfigLiveData?.value?.let{ appHaloConfig ->
            if((notiVisVar == NuNotiVisVariable.SIZE || notiVisVar == NuNotiVisVariable.POSITION) &&
                    (notiDataProp == NotiProperty.IMPORTANCE)
            ) {
                setRangeMapping(appHaloConfig)
            } else {
                //setDiscreteMapping(appHaloConfig)
                setNominalMapping(appHaloConfig)
            }
        }
    }

    fun setMappingContentsChangedListener(listener: ChildViewInteractionListener){
        mappingContentsChangedListener = listener
    }

    private fun updateAppConfig(){
        viewModel?.appHaloConfigLiveData?.value?.let{ currentConfig ->

            when(notiVisVar){
                NuNotiVisVariable.MOTION -> {
                    currentConfig.independentVisualParameters[objIndex].selectedMotionList = (visVarContents as MutableList<AnimatorSet>).toList()
                }
                NuNotiVisVariable.COLOR -> {
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

            viewModel?.appHaloConfigLiveData?.value = currentConfig
            mappingContentsChangedListener?.onMappingContentsUpdated()
        }
    }

    private fun setNominalMapping(appConfig: AppHaloConfig){
        View.inflate(context, R.layout.item_child_new_nominal_mapping, this)

        val tableLayout = findViewById<TableLayout>(R.id.nominal_mapping_table).apply{
            removeAllViews()
        }

        val independentVisParams = appConfig.independentVisualParameters[0]
        val independentDataParams = appConfig.independentDataParameters[0]

        //notiData Contents 가져오고
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

        //visVar Contents 가져오고
        visVarContents.clear()
        visVarContents.addAll(
                when (notiVisVar) {
                    NuNotiVisVariable.MOTION -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedMotion)
                        }
                        else{
                            independentVisParams.selectedMotionList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NuNotiVisVariable.SHAPE -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedShape)
                        }
                        else{
                            independentVisParams.selectedShapeList.subList(0, notiDataPropContents.size)
                        }

                    }
                    NuNotiVisVariable.COLOR -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedColor)
                        }
                        else{
                            independentVisParams.selectedColorList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NuNotiVisVariable.SIZE -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(independentVisParams.selectedSizeRange)
                        }
                        else{
                            MapFunctionUtilities.bin(
                                    independentVisParams.selectedSizeRange,
                                    notiDataPropContents.size)

                        }
                    }
                    NuNotiVisVariable.POSITION -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(independentVisParams.selectedPosRange)
                        }
                        else{
                            MapFunctionUtilities.bin(
                                    independentVisParams.selectedPosRange,
                                    notiDataPropContents.size)
                        }
                    }
                }
        )

        for(i in 0..visVarContents.size){
            tableLayout.addView(TableRow(context), TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT))
        }

        when (notiDataProp) {
            NotiProperty.CONTENT -> {
                setNewKeywordFrameView(notiDataPropContents as MutableList<Pair<String, MutableList<String>>>, tableLayout, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            }
            NotiProperty.LIFE_STAGE -> {
                val givenLifeStageIndices = (notiDataPropContents as MutableList<EnhancedNotificationLife>).map{independentDataParams.givenLifeList.indexOf(it)}

                setNewSpinnerView(givenLifeStageIndices, independentDataParams.givenLifeList, tableLayout)
            }
            NotiProperty.IMPORTANCE -> {
                val givenImportanceList = MapFunctionUtilities.bin(independentDataParams.givenImportanceRange, 5)
                val givenImportanceIndices = (notiDataPropContents as MutableList<Pair<Double, Double>>).map{givenImportanceList.indexOf(it)}

                setNewSpinnerView(givenImportanceIndices, givenImportanceList, tableLayout)
            }
            null -> { }
        }

        if(notiDataPropContents.size == 0){
            val tableRow = tableLayout.getChildAt(0) as TableRow
            tableRow.addView(
                    TextView(context).apply{
                        text = "Not Mapped"
                        textSize = 16f
                        gravity = Gravity.CENTER
                    },
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT ).apply{
                        column = 0
                        gravity = Gravity.CENTER
                    }
            )
        }

        visVarContents.forEachIndexed { index, content ->
            val tableRow = tableLayout.getChildAt(index) as TableRow

            val frame = FrameLayout(context)
            setVisVarFrame(index, frame, content)

            tableRow.addView(
                    TextView(context).apply{
                        text = "to"
                        textSize = 13f
                        gravity = Gravity.CENTER
                    },
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply{
                        gravity = Gravity.CENTER
                        column = 1
                    }
            )


            tableRow.addView(
                    frame,
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply{
                        marginStart = Utilities.dpToPx(context, 10)
                        marginEnd = Utilities.dpToPx(context, 10)
                        topMargin = Utilities.dpToPx(context, 10)
                        bottomMargin = Utilities.dpToPx(context, 10)
                        gravity = Gravity.CENTER
                        column = 2
                    }
            )
        }

    }

    private fun setDiscreteMapping(appConfig: AppHaloConfig){
        View.inflate(context, R.layout.item_child_nominal_expandablecontrolpanel, this)

        val visVarLayout = findViewById<LinearLayout>(R.id.vis_var_content_list).apply{
            removeAllViews()
        }
        val notiPropLayout = findViewById<LinearLayout>(R.id.noti_prop_content_list).apply{
            removeAllViews()
        }

        val independentVisParams = appConfig.independentVisualParameters[0]
        val independentDataParams = appConfig.independentDataParameters[0]

        //notiData Contents 가져오고
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

        //visVar Contents 가져오고
        visVarContents.clear()
        visVarContents.addAll(
                when (notiVisVar) {
                    NuNotiVisVariable.MOTION -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedMotion)
                        }
                        else{
                            independentVisParams.selectedMotionList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NuNotiVisVariable.SHAPE -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedShape)
                        }
                        else{
                            independentVisParams.selectedShapeList.subList(0, notiDataPropContents.size)
                        }

                    }
                    NuNotiVisVariable.COLOR -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedColor)
                        }
                        else{
                            independentVisParams.selectedColorList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NuNotiVisVariable.SIZE -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(independentVisParams.selectedSizeRange)
                        }
                        else{
                            MapFunctionUtilities.bin(
                                    independentVisParams.selectedSizeRange,
                                    notiDataPropContents.size)

                        }
                    }
                    NuNotiVisVariable.POSITION -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(independentVisParams.selectedPosRange)
                        }
                        else{
                            MapFunctionUtilities.bin(
                                    independentVisParams.selectedPosRange,
                                    notiDataPropContents.size)
                        }
                    }
                }
        )

        visVarContents.forEachIndexed { index, content ->
            val frame = FrameLayout(context)
            setVisVarFrame(index, frame, content)
            visVarLayout.addView(
                    frame,
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply{
                        marginStart = Utilities.dpToPx(context, 10)
                        marginEnd = Utilities.dpToPx(context, 10)
                    }
            )
        }

        when (notiDataProp) {
            NotiProperty.CONTENT -> {
                setKeywordFrameView(notiDataPropContents as MutableList<Pair<String, MutableList<String>>>, notiPropLayout, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            }
            NotiProperty.LIFE_STAGE -> {
                val givenLifeStageIndices = (notiDataPropContents as MutableList<EnhancedNotificationLife>).map{independentDataParams.givenLifeList.indexOf(it)}

                setSpinnerView(givenLifeStageIndices, independentDataParams.givenLifeList, notiPropLayout)
            }
            NotiProperty.IMPORTANCE -> {
                val givenImportanceList = MapFunctionUtilities.bin(independentDataParams.givenImportanceRange, 5)
                val givenImportanceIndices = (notiDataPropContents as MutableList<Pair<Double, Double>>).map{givenImportanceList.indexOf(it)}

                setSpinnerView(
                        givenImportanceIndices,
                        givenImportanceList,
                        notiPropLayout)
            }
            null -> { }
        }
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
                updateAppConfig()
            }
            flowLayout.addView(chipView)
        }
    }

    private fun setNewKeywordFrameView(
            notiDataPropContents: MutableList<Pair<String, MutableList<String>>>,
            notiPropLayout: TableLayout,
            inflater: LayoutInflater
    ){
        notiDataPropContents.forEachIndexed { index, keywordGroup ->
            val tr = notiPropLayout.getChildAt(index) as TableRow
            tr.addView(
                    FrameLayout(context).also { frame ->
                        val keywordFrame = inflater.inflate(R.layout.layout_keyword_mapping, null, false) as LinearLayout

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
                        frame.addView(
                                keywordFrame,
                                FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply{
                                    gravity = Gravity.CENTER
                                }
                        )
                    },

                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply{
                        gravity = Gravity.CENTER
                        column = 0
                    }
            )
        }
    }

    private fun setKeywordFrameView(
            notiDataPropContents: MutableList<Pair<String, MutableList<String>>>,
            notiPropLayout: LinearLayout,
            inflater: LayoutInflater
    ){
        notiDataPropContents.forEachIndexed { index, keywordGroup ->
            notiPropLayout.addView(
                    FrameLayout(context).also { frame ->
                        //frame.removeAllViews()
                        //frame.layoutParams.width = Utilities.dpToPx(context, 170)
                        val keywordFrame = inflater.inflate(R.layout.layout_keyword_mapping, null, false) as LinearLayout

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
                    },
                    LayoutParams(Utilities.dpToPx(context, 170), LayoutParams.WRAP_CONTENT).apply{
                        marginStart = Utilities.dpToPx(context, 10)
                        marginEnd = Utilities.dpToPx(context, 10)
                    }
            )
        }
    }

    private fun setNewSpinnerView(selectedPropContentsIndices: List<Int>, givenPropContents: List<Any>, notiPropLayout: TableLayout){
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
        val spinnerAdapter = getArrayAdapter(givenPropStringContents.toMutableList().apply{
            add(0, "None")
        }.toList())
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner)
        selectedPropContentsIndices.forEachIndexed{ index, indexVal ->
            val tr = notiPropLayout.getChildAt(index) as TableRow
            tr.addView(
                    FrameLayout(context).also{ frame ->
                        val spinner = Spinner(context)
                        spinner.adapter = spinnerAdapter
                        spinner.setSelection(indexVal + 1) //indexVal 번째 순서에 있는 givenPropContent
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                                if(i == 0){
                                    //TODO(mapping)
                                    //notiDataPropContents[index] = selectedData
                                } else{
                                    val selectedData = givenPropContents[i - 1]
                                    notiDataPropContents[index] = selectedData
                                }
                                updateAppConfig()
                            }
                            override fun onNothingSelected(adapterView: AdapterView<*>) {}
                        }
                        frame.addView(spinner)
                    },
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply{
                        gravity = Gravity.CENTER
                        column = 0
                    }
            )
        }
    }

    private fun setSpinnerView(selectedPropContentsIndices: List<Int>, givenPropContents: List<Any>, notiPropLayout: LinearLayout){
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



        val spinnerAdapter = getArrayAdapter(givenPropStringContents.toMutableList().apply{
            add(0, "None")
        }.toList())
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner)
        selectedPropContentsIndices.forEachIndexed{ index, indexVal ->
            notiPropLayout.addView(
                    FrameLayout(context).also{ frame ->
                        val spinner = Spinner(context)
                        spinner.adapter = spinnerAdapter
                        spinner.setSelection(indexVal + 1) //indexVal 번째 순서에 있는 givenPropContent
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                                if(i == 0){
                                    //TODO(mapping)
                                    //notiDataPropContents[index] = selectedData
                                } else{
                                    val selectedData = givenPropContents[i - 1]
                                    notiDataPropContents[index] = selectedData
                                }
                                updateAppConfig()
                            }
                            override fun onNothingSelected(adapterView: AdapterView<*>) {}
                        }
                        frame.addView(spinner)
                    },
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply{
                        //marginStart = Utilities.dpToPx(context, 10)
                        //marginEnd = Utilities.dpToPx(context, 10)
                        column = 1
                    }
            )
        }
    }

    private fun setVisVarFrame(index: Int, frame: FrameLayout, content: Any ){
        frame.background = ContextCompat.getDrawable(context, R.drawable.rounded_rectangle)
        frame.addView(
                TextView(context).apply {
                    text = contentToString(notiVisVar, content)
                    gravity = Gravity.CENTER
                }
        )
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
                            Log.d(TAG, "$color Picked. ${visVarContents[index]}")
                            updateAppConfig()
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

    private fun setRangeMapping(appConfig: AppHaloConfig){
        lateinit var mappingUI: ContToContUI

        View.inflate(context, R.layout.item_child_new_range_mapping, this)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.range_mapping_layout)
        constraintLayout.addView(ContToContUI(context).apply {
            mappingUI = this
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        })
        constraintLayout.getChildAt(0).apply {
            val set = ConstraintSet()
            set.clone(constraintLayout)
            set.connect(this.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP)
            set.connect(this.id, ConstraintSet.RIGHT, constraintLayout.id, ConstraintSet.RIGHT)
            set.connect(this.id, ConstraintSet.LEFT, constraintLayout.id, ConstraintSet.LEFT)
            set.connect(this.id, ConstraintSet.BOTTOM, constraintLayout.id, ConstraintSet.BOTTOM)
            set.applyTo(constraintLayout)
        }








    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.IndependentMappingChildLayout, defStyle, 0)
        a.recycle()
        mappingContentsChangedListener = null
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
