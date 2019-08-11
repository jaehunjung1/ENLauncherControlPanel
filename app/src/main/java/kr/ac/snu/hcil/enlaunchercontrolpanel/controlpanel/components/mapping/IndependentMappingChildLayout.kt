package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

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
import androidx.fragment.app.FragmentActivity
import com.nex3z.flowlayout.FlowLayout
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import com.robertlevonyan.views.chip.Chip
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.utilities.Utilities
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

class IndependentMappingChildLayout : LinearLayout {
    companion object{
        private const val TAG = "Inde_Map_Child"
    }

    interface ChildViewInteractionListener{
        fun onShapeMappingContentsUpdated(componentIndex: Int)
    }

    private var notiVisVar: NotiVisVariable = NotiVisVariable.MOTION
    private var notiDataProp: NotiProperty? = null
    private var viewModel: AppHaloConfigViewModel? = null
    private var objIndex: Int = -1

    private var mappingContentsChangedListener: ChildViewInteractionListener? = null

    private val visVarContents: MutableList<Any> = mutableListOf()
    private val notiDataPropContents: MutableList<Any> = mutableListOf()

    private lateinit var mappingUI: ContToContUI

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
            visVar: NotiVisVariable,
            notiProp: NotiProperty?,
            visObjIndex: Int,
            appConfigViewModel: AppHaloConfigViewModel? = null
    ){
        notiVisVar = visVar
        notiDataProp = notiProp
        objIndex = visObjIndex
        viewModel = appConfigViewModel


        appConfigViewModel?.appHaloConfigLiveData?.value?.let{ appHaloConfig ->
            if((notiVisVar == NotiVisVariable.SIZE || notiVisVar == NotiVisVariable.POSITION) &&
                    (notiDataProp == NotiProperty.IMPORTANCE)
            ) {
                setRangeMapping(appHaloConfig)
            } else {
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
                NotiVisVariable.MOTION -> {
                    currentConfig.independentVisualParameters[objIndex].selectedMotionList = (visVarContents as MutableList<AnimatorSet>).toList()
                }
                NotiVisVariable.COLOR -> {
                    currentConfig.independentVisualParameters[objIndex].selectedColorList = (visVarContents as MutableList<Int>).toList()
                }
                NotiVisVariable.SHAPE -> {
                    currentConfig.independentVisualParameters[objIndex].selectedShapeList = (visVarContents as MutableList<VisObjectShape>).toList()
                }
                NotiVisVariable.SIZE -> {
                    currentConfig.independentVisualParameters[objIndex].selectedSizeRangeList = (visVarContents as MutableList<Pair<Double, Double>>).toList()
                }
                NotiVisVariable.POSITION -> {
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
                    //currentConfig.independentDataParameters[objIndex].keywordGroupMap = (notiDataPropContents as MutableList<Pair<String, MutableList<String>>>).toMap()
                    //이게 필요한가? 아닐듯
                }
                else -> {}
            }

            viewModel?.appHaloConfigLiveData?.value = currentConfig
        }
    }

    private fun setNominalMapping(appConfig: AppHaloConfig){
        View.inflate(context, R.layout.item_child_new_nominal_mapping, this)

        val tableLayout = findViewById<TableLayout>(R.id.nominal_mapping_table).apply{
            removeAllViews()
        }

        val independentVisParams = appConfig.independentVisualParameters[0]
        val independentDataParams = appConfig.independentDataParameters[0]
        val orderedKeywordGroups = appConfig.keywordGroupPatterns.getOrderedKeywordGroupImportancePatternsWithRemainder().map{
            it.group to it.keywords.toList()
        }.toList()

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
                        orderedKeywordGroups
                    }
                }
        )

        //visVar Contents 가져오고
        visVarContents.clear()
        visVarContents.addAll(
                when (notiVisVar) {
                    NotiVisVariable.MOTION -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedMotion)
                        }
                        else{
                            independentVisParams.selectedMotionList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NotiVisVariable.SHAPE -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedShape)
                        }
                        else{
                            independentVisParams.selectedShapeList.subList(0, notiDataPropContents.size)
                        }

                    }
                    NotiVisVariable.COLOR -> {
                        if(notiDataPropContents.size == 0){
                            listOf(independentVisParams.selectedColor)
                        }
                        else{
                            independentVisParams.selectedColorList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NotiVisVariable.SIZE -> {
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
                    NotiVisVariable.POSITION -> {
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
                setNewKeywordFrameView(notiDataPropContents as List<Pair<String, List<String>>>, tableLayout, context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
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

    private fun addKeywordToFlowLayout(flowLayout: FlowLayout, keyword: String, inflater: LayoutInflater) {
        (inflater.inflate(R.layout.chip_view_layout, null) as Chip).let{ chipView ->
            chipView.chipText = keyword
            chipView.isClosable = false
            flowLayout.addView(chipView)
        }
    }

    private fun setNewKeywordFrameView(
            notiDataPropContents: List<Pair<String, List<String>>>,
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
        frame.tag = index
        frame.addView(
                TextView(context).apply {
                    text = contentToString(notiVisVar, content)
                    gravity = Gravity.CENTER
                }
        )
        when(notiVisVar){
            NotiVisVariable.MOTION -> {

            }
            NotiVisVariable.COLOR -> {
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
            NotiVisVariable.SHAPE -> {
                val shape = content as VisObjectShape
                frame.addView(
                        ImageView(context).apply{
                            setImageDrawable(shape.drawable)
                            setOnClickListener{
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setActivityTitle("Set Image")
                                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                                        .setAspectRatio(1, 1)
                                        .setCropMenuCropButtonTitle("Done")
                                        .setRequestedSize(150, 150)
                                        .start(context as FragmentActivity)
                                mappingContentsChangedListener?.onShapeMappingContentsUpdated(index)
                            }
                        },
                        FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                )
                /*
                frame.setOnClickListener{
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("My Crop")
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setCropMenuCropButtonTitle("Done")
                            .setRequestedSize(50, 50)
                            .start(context as FragmentActivity)
                    mappingContentsChangedListener?.onShapeMappingContentsUpdated(index)
                }
                */
            }
            NotiVisVariable.SIZE -> {}
            NotiVisVariable.POSITION -> {}
        }
    }

    private fun contentToString(notiVisVar: NotiVisVariable, content: Any): String{
        return when (notiVisVar){
            NotiVisVariable.MOTION -> content.toString()
            NotiVisVariable.SHAPE -> (content as VisObjectShape).type.name
            NotiVisVariable.COLOR -> (content as Int).let{"R:${Color.red(it)}, G:${Color.green(it)}, B:${Color.blue(it)}"}
            NotiVisVariable.SIZE -> (content as Pair<Double, Double>).let{"${"%.1f".format(it.first)}-${"%.1f".format(it.second)}"}
            NotiVisVariable.POSITION -> (content as Pair<Double, Double>).let{"${"%.1f".format(it.first)}-${"%.1f".format(it.second)}"}
        }
    }

    private fun setRangeMapping(appConfig: AppHaloConfig){


        View.inflate(context, R.layout.item_child_new_range_mapping, this)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.range_mapping_layout)
        constraintLayout.addView(ContToContUI(context).apply {
            mappingUI = this
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setViewModel(viewModel)
            setMapping(notiVisVar, notiDataProp!!)
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

        /*
        * TODO set appconfig with range mapping
        * mappingUI.config returns [left Start, left End, right Start, right End]
        */
        //Log.i("Initial Mapping", Arrays.toString(mappingUI.config))

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
