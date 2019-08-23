package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textview.MaterialTextView
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.utilities.Utilities
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotiAggregationType
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape
import kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType

class AggregatedMappingChildLayout : LinearLayout{
    companion object{
        private const val TAG = "Aggr_Map_child"
        private const val nominalVisvarContentsMinSize = 50
    }

    interface ChildViewInteractionListener{
        fun onShapeMappingContentsUpdated(componentIndex: Int, shapeType: VisShapeType)
    }

    private var groupByNotiProp: NotiProperty? = null
    private var notiVisVar: NotiVisVariable = NotiVisVariable.MOTION
    private var notiAggrOp: NotiAggregationType = NotiAggregationType.COUNT
    private var notiDataProp: NotiProperty? = null
    private var viewModel: AppHaloConfigViewModel? = null
    private var objIndex: Int = -1

    private var mappingContentsChangedListener: ChildViewInteractionListener? = null

    private val visVarContents: MutableList<Any> = mutableListOf()
    private val notiDataPropContents: MutableList<Any> = mutableListOf()

    private lateinit var mappingUI: AggregatedContToContUI

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
            visVar: NotiVisVariable,
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

        this.removeAllViews()

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

    private fun setNominalTableMapping(appConfig: AppHaloConfig){
        val tableLayout = findViewById<TableLayout>(R.id.nominal_mapping_table).apply{
            removeAllViews()
        }

        val aggregatedVisParams = appConfig.aggregatedVisualParameters[0]
        val aggregatedDataParams = appConfig.aggregatedDataParameters[0]
        val orderedKeywordGroups = appConfig.keywordGroupPatterns.getOrderedKeywordGroupImportancePatternsWithRemainder().map{
            it.group to it.keywords.toList()
        }.toList()

        //notiData Contents 가져오고
        notiDataPropContents.clear()
        notiDataPropContents.addAll(
                when (notiDataProp) {
                    null -> emptyList()
                    NotiProperty.IMPORTANCE -> {
                        aggregatedDataParams.selectedImportanceRangeList
                    }
                    NotiProperty.LIFE_STAGE -> {
                        aggregatedDataParams.givenLifeList
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
                            listOf(aggregatedVisParams.selectedMotion)
                        }
                        else{
                            aggregatedVisParams.selectedMotionList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NotiVisVariable.SHAPE -> {
                        if(notiDataPropContents.size == 0){
                            listOf(aggregatedVisParams.selectedShape)
                        }
                        else{
                            aggregatedVisParams.selectedShapeList.subList(0, notiDataPropContents.size)
                        }

                    }
                    NotiVisVariable.COLOR -> {
                        if(notiDataPropContents.size == 0){
                            listOf(aggregatedVisParams.selectedColor)
                        }
                        else{
                            aggregatedVisParams.selectedColorList.subList(0, notiDataPropContents.size)
                        }
                    }
                    NotiVisVariable.SIZE -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(aggregatedVisParams.selectedSizeRange)
                        }
                        else{
                            aggregatedVisParams.getSelectedSizeRangeList(notiDataPropContents.size)

                        }
                    }
                    NotiVisVariable.POSITION -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(aggregatedVisParams.selectedPosRange)
                        }
                        else{
                            aggregatedVisParams.getSelectedPosRangeList(notiDataPropContents.size)
                        }
                    }
                }
        )

        for(i in 0..visVarContents.size){
            tableLayout.addView(TableRow(context), TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT))
        }

        when (notiDataProp) {
            NotiProperty.CONTENT -> {
                val contents = (notiDataPropContents as List<Pair<String, List<String>>>).map{it.first}
                setNominalNotiPropViews(contents, tableLayout, LayoutInflater.from(context))
            }
            NotiProperty.LIFE_STAGE -> {
                setNominalNotiPropViews(aggregatedDataParams.givenLifeList, tableLayout, LayoutInflater.from(context))
            }
            NotiProperty.IMPORTANCE -> {
                val givenImportanceList = MapFunctionUtilities.bin(aggregatedDataParams.givenImportanceRange, aggregatedDataParams.binNums)
                setNominalNotiPropViews(givenImportanceList, tableLayout, LayoutInflater.from(context))
            }
            null -> { }
        }

        if(notiDataPropContents.size == 0){
            val tableRow = tableLayout.getChildAt(0) as TableRow
            tableRow.addView(
                    MaterialTextView(context, null, R.style.TextAppearance_MyTheme_Subtitle2).apply{
                        text = "Not Mapped"
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

            setVisVarFrame(appConfig, index, frame, content)
            tableRow.addView(
                    MaterialTextView(context, null, R.style.TextAppearance_MyTheme_Overline).apply{
                        text = "to"
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
                        marginStart = Utilities.dpToPx(context, 8)
                        marginEnd = Utilities.dpToPx(context, 8)
                        topMargin = Utilities.dpToPx(context, 8)
                        bottomMargin = Utilities.dpToPx(context, 8)
                        gravity = Gravity.CENTER
                        column = 2
                    }
            )
        }
    }

    private fun setNominalMapping(appConfig: AppHaloConfig){
        View.inflate(context, R.layout.item_child_new_nominal_mapping, this)

        findViewById<LinearLayout>(R.id.bin_layout).let{binLayout ->
            binLayout.visibility = View.GONE
        }
        setNominalTableMapping(appConfig)
    }

    private fun setNominalNotiPropViews(givenPropContents: List<Any>, notiPropLayout: TableLayout, inflater: LayoutInflater){
        val givenPropStringContents: List<String> =
                when(notiDataProp){
                    NotiProperty.IMPORTANCE -> {givenPropContents.map{
                        val propContent = it as Pair<Double, Double>
                        "${"%.2f".format(propContent.first)}-${"%.2f".format(propContent.second)}"}
                    }
                    NotiProperty.LIFE_STAGE -> {givenPropContents.map{
                        val propContent = it as EnhancedNotificationLife
                        propContent.name }
                    }
                    NotiProperty.CONTENT -> {
                        givenPropContents as List<String>
                    }
                    else -> {
                        emptyList()
                    }
                }

        givenPropStringContents.forEachIndexed{ index, strContent ->
            val tr = notiPropLayout.getChildAt(index) as TableRow
            tr.addView(
                    FrameLayout(context).also { frame ->
                        val keywordFrame = inflater.inflate(R.layout.layout_new_keyword_mapping, null, false) as LinearLayout

                        keywordFrame.findViewById<TextView>(R.id.group_name_text).text = strContent

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

    private fun setVisVarFrame(appConfig: AppHaloConfig, index: Int, frame: FrameLayout, content: Any ){
        frame.tag = index
        when(notiVisVar){
            NotiVisVariable.COLOR -> {
                val origColor = content as Int
                val imgView = ImageView(context).apply{

                    setImageDrawable(ColorDrawable(origColor))
                    setOnClickListener {
                        ColorPicker(context as Activity,
                                Color.red(origColor), Color.green(origColor), Color.blue(origColor)
                        ).let { cp ->
                            cp.enableAutoClose()
                            cp.setCallback { color ->
                                visVarContents[index] = color
                                setImageDrawable(ColorDrawable(color))
                                Log.d(TAG, "$color Picked. ${visVarContents[index]}")
                                updateAppConfig()
                            }
                            cp.show()
                        }
                    }
                }

                frame.addView(
                        imgView,
                        FrameLayout.LayoutParams(50, 50)
                )
            }
            NotiVisVariable.SHAPE -> {
                NominalVisVarContentSpinner.generate(context, appConfig.aggregatedDataParameters[0].binNums, notiVisVar).let{ spinner ->
                    spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            (parent?.getItemAtPosition(position) as Triple<String, String, VisShapeType>?)?.let{ item ->
                                when(item.third){
                                    VisShapeType.IMAGE -> {
                                        CropImage.activity()
                                                .setGuidelines(CropImageView.Guidelines.ON)
                                                .setActivityTitle("Set Image")
                                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                                .setAspectRatio(1, 1)
                                                .setCropMenuCropButtonTitle("Done")
                                                .setRequestedSize(150, 150)
                                                .start(context as FragmentActivity)
                                        mappingContentsChangedListener?.onShapeMappingContentsUpdated(index, item.third)
                                    }
                                    else -> {
                                        visVarContents[index] = VisObjectShape(item.third, null)
                                        updateAppConfig()
                                    }
                                }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                    (spinner.adapter as NominalVisVarContentSpinner.NominalVisVarContentAdapter).let{adapter ->
                        val visObjectShape = visVarContents[index] as VisObjectShape
                        spinner.setSelection(adapter.findContent(visObjectShape.type)?: 0)
                    }

                    frame.addView(
                            spinner,
                            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    )
                }
            }
            else -> {
                NominalVisVarContentSpinner.generate(context, appConfig.aggregatedDataParameters[0].binNums, notiVisVar).let{ spinner ->
                    spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            (parent?.getItemAtPosition(position) as Triple<String, String, Any>?)?.let{ item ->
                                visVarContents[index] = item.third
                                updateAppConfig()
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                    (spinner.adapter as NominalVisVarContentSpinner.NominalVisVarContentAdapter).let{adapter ->
                        spinner.setSelection(adapter.findContent(visVarContents[index])?: index)
                    }

                    frame.addView(
                            spinner,
                            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    )
                }
            }
        }
    }

    private fun setRangeMapping(appHaloConfig: AppHaloConfig){

        View.inflate(context, R.layout.item_child_new_range_mapping, this)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.range_mapping_layout)
        constraintLayout.addView(AggregatedContToContUI(context).apply {
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
    }

    private fun updateAppConfig(){
        viewModel?.appHaloConfigLiveData?.value?.let{ currentConfig ->
            val listSize = when(notiDataProp){
                NotiProperty.LIFE_STAGE -> EnhancedNotificationLife.values().size
                NotiProperty.IMPORTANCE -> currentConfig.aggregatedDataParameters[objIndex].binNums
                NotiProperty.CONTENT -> currentConfig.keywordGroupPatterns.getOrderedKeywordGroupImportancePatternsWithRemainder().size
                else -> 5
            }

            when(notiVisVar){
                NotiVisVariable.MOTION -> {
                    val currentList = currentConfig.aggregatedVisualParameters[objIndex].selectedMotionList
                    currentConfig.aggregatedVisualParameters[objIndex].selectedMotionList = List(listSize){ index ->
                        if(currentList.size >= visVarContents.size){
                            if(index >= currentList.size){
                                AnimatorSet()
                            }
                            else if(index >= visVarContents.size){
                                currentList[index]
                            }
                            else{
                                (visVarContents as MutableList<AnimatorSet>)[index]
                            }
                        }
                        else{
                            if(index >= currentList.size){
                                AnimatorSet()
                            }
                            else if(index >= visVarContents.size){
                                currentList[index]
                            }
                            else{
                                (visVarContents as MutableList<AnimatorSet>)[index]
                            }
                        }
                    }
                }
                NotiVisVariable.COLOR -> {
                    val currentList = currentConfig.aggregatedVisualParameters[objIndex].selectedColorList
                    currentConfig.aggregatedVisualParameters[objIndex].selectedColorList = List(listSize){ index ->
                        if(currentList.size >= visVarContents.size){
                            if(index >= currentList.size){
                                Color.BLACK
                            }
                            else if(index >= visVarContents.size){
                                currentList[index]
                            }
                            else{
                                (visVarContents as MutableList<Int>)[index]
                            }
                        }
                        else{
                            if(index >= currentList.size){
                                Color.BLACK
                            }
                            else if(index >= visVarContents.size){
                                currentList[index]
                            }
                            else{
                                (visVarContents as MutableList<Int>)[index]
                            }
                        }
                    }
                }
                NotiVisVariable.SHAPE -> {
                    val currentList = currentConfig.aggregatedVisualParameters[objIndex].selectedShapeList
                    currentConfig.aggregatedVisualParameters[objIndex].selectedShapeList = List(listSize){ index ->
                        if(currentList.size >= visVarContents.size){
                            if(index >= currentList.size){
                                VisObjectShape(VisShapeType.RECT, null)
                            }
                            else if(index >= visVarContents.size){
                                currentList[index]
                            }
                            else{
                                (visVarContents as MutableList<VisObjectShape>)[index]
                            }
                        }
                        else{
                            if(index >= currentList.size){
                                VisObjectShape(VisShapeType.RECT, null)
                            }
                            else if(index >= visVarContents.size){
                                currentList[index]
                            }
                            else{
                                (visVarContents as MutableList<VisObjectShape>)[index]
                            }
                        }
                    }
                }
                NotiVisVariable.SIZE -> {
                    val currentList = currentConfig.aggregatedVisualParameters[objIndex].getSelectedSizeRangeList(
                            listSize
                    )

                    currentConfig.aggregatedVisualParameters[objIndex].setSelectedSizeRangeList(
                            List(listSize){index ->
                                if(index >= visVarContents.size)
                                    currentList[index]
                                else
                                    (visVarContents as MutableList<Pair<Double, Double>>)[index]
                            }
                    )
                }
                NotiVisVariable.POSITION -> {
                    val currentList = currentConfig.aggregatedVisualParameters[objIndex].getSelectedPosRangeList(
                            listSize
                    )

                    currentConfig.aggregatedVisualParameters[objIndex].setSelectedPosRangeList(
                            List(listSize){index ->
                                if(index >= visVarContents.size)
                                    currentList[index]
                                else
                                    (visVarContents as MutableList<Pair<Double, Double>>)[index]
                            }
                    )
                }
            }

            viewModel?.appHaloConfigLiveData?.value = currentConfig
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
}