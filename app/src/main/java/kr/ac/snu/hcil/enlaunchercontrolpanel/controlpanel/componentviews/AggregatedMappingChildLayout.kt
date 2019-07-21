package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.componentviews

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
import androidx.core.content.ContextCompat
import com.nex3z.flowlayout.FlowLayout
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import com.robertlevonyan.views.chip.Chip
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.utilities.Utilities
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

class AggregatedMappingChildLayout : LinearLayout{
    companion object{
        private const val TAG = "Aggr_Map_child"
    }

    interface ChildViewInteractionListener{
        fun onMappingContentsUpdated()
    }

    private var groupByNotiProp: NotiProperty? = null
    private var notiVisVar: NuNotiVisVariable = NuNotiVisVariable.MOTION
    private var notiAggrOp: NotiAggregationType = NotiAggregationType.COUNT
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
            groupByProp: NotiProperty?,
            visVar: NuNotiVisVariable,
            aggrOp: NotiAggregationType,
            notiProp: NotiProperty?,
            visObjIndex: Int,
            appConfigViewModel: AppHaloConfigViewModel? = null
    ){
        groupByNotiProp = groupByProp
        notiVisVar = visVar
        notiAggrOp = aggrOp
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

    private fun setNominalMapping(appConfig: AppHaloConfig){
        View.inflate(context, R.layout.item_child_new_nominal_mapping, this)

        val tableLayout = findViewById<TableLayout>(R.id.nominal_mapping_table).apply{
            removeAllViews()
        }

        val aggregatedVisParams = appConfig.aggregatedVisualParameters[0]
        val aggregatedDataParams = appConfig.aggregatedDataParameters[0]

        //notiData Contents 가져오고
        notiDataPropContents.clear()
        notiDataPropContents.addAll(
                when (notiDataProp) {
                    null -> emptyList()
                    NotiProperty.IMPORTANCE -> {
                        aggregatedDataParams.selectedImportanceRangeList
                    }
                    NotiProperty.LIFE_STAGE -> {
                        aggregatedDataParams.selectedLifeList
                    }
                    NotiProperty.CONTENT -> {
                        aggregatedDataParams.keywordGroupMap.toList()
                    }
                }
        )

        //visVar Contents 가져오고
        visVarContents.clear()
        visVarContents.addAll(
                when (notiVisVar) {
                    NuNotiVisVariable.MOTION -> {
                        if(notiDataPropContents.size == 0){
                            listOf(aggregatedVisParams.selectedMotion)
                        }
                        else{
                            aggregatedVisParams.selectedMotionList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NuNotiVisVariable.SHAPE -> {
                        if(notiDataPropContents.size == 0){
                            listOf(aggregatedVisParams.selectedShape)
                        }
                        else{
                            aggregatedVisParams.selectedShapeList.subList(0, notiDataPropContents.size)
                        }

                    }
                    NuNotiVisVariable.COLOR -> {
                        if(notiDataPropContents.size == 0){
                            listOf(aggregatedVisParams.selectedColor)
                        }
                        else{
                            aggregatedVisParams.selectedColorList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NuNotiVisVariable.SIZE -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(aggregatedVisParams.selectedSizeRange)
                        }
                        else{
                            MapFunctionUtilities.bin(
                                    aggregatedVisParams.selectedSizeRange,
                                    notiDataPropContents.size)

                        }
                    }
                    NuNotiVisVariable.POSITION -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(aggregatedVisParams.selectedPosRange)
                        }
                        else{
                            MapFunctionUtilities.bin(
                                    aggregatedVisParams.selectedPosRange,
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
                val givenLifeStageIndices = (notiDataPropContents as MutableList<EnhancedNotificationLife>).map{aggregatedDataParams.givenLifeList.indexOf(it)}

                setNewSpinnerView(givenLifeStageIndices, aggregatedDataParams.givenLifeList, tableLayout)
            }
            NotiProperty.IMPORTANCE -> {
                val givenImportanceList = MapFunctionUtilities.bin(aggregatedDataParams.givenImportanceRange, 5)
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

    private fun setRangeMapping(appHaloConfig: AppHaloConfig){

    }

    private fun updateAppConfig(){
        viewModel?.appHaloConfigLiveData?.value?.let{ currentConfig ->
            when(notiVisVar){
                NuNotiVisVariable.MOTION -> {
                    currentConfig.aggregatedVisualParameters[objIndex].selectedMotionList = (visVarContents as MutableList<AnimatorSet>).toList()
                }
                NuNotiVisVariable.COLOR -> {
                    currentConfig.aggregatedVisualParameters[objIndex].selectedColorList = (visVarContents as MutableList<Int>).toList()
                }
                NuNotiVisVariable.SHAPE -> {
                    currentConfig.aggregatedVisualParameters[objIndex].selectedShapeList = (visVarContents as MutableList<VisObjectShape>).toList()
                }
                NuNotiVisVariable.SIZE -> {
                    currentConfig.aggregatedVisualParameters[objIndex].selectedSizeRangeList = (visVarContents as MutableList<Pair<Double, Double>>).toList()
                }
                NuNotiVisVariable.POSITION -> {
                    currentConfig.aggregatedVisualParameters[objIndex].selectedPosRangeList = (visVarContents as MutableList<Pair<Double, Double>>).toList()
                }
            }

            when(notiDataProp){
                NotiProperty.LIFE_STAGE -> {
                    currentConfig.aggregatedDataParameters[objIndex].selectedLifeList = (notiDataPropContents as MutableList<EnhancedNotificationLife>).toList()
                }
                NotiProperty.IMPORTANCE -> {
                    currentConfig.aggregatedDataParameters[objIndex].selectedImportanceRangeList = (notiDataPropContents as MutableList<Pair<Double, Double>>).toList()
                }
                NotiProperty.CONTENT -> {
                    currentConfig.aggregatedDataParameters[objIndex].keywordGroupMap = (notiDataPropContents as MutableList<Pair<String, MutableList<String>>>).toMap()
                }
                else -> {}
            }

            viewModel?.appHaloConfigLiveData?.value = currentConfig
            mappingContentsChangedListener?.onMappingContentsUpdated()
        }
    }

    fun setMappingContentsChangedListener(listener: ChildViewInteractionListener){
        mappingContentsChangedListener = listener
    }

    private fun init(attrs: AttributeSet?, defStyle: Int){
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.AggregatedMappingChildLayout, defStyle, 0)
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