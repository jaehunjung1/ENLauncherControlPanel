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
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
import kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType

class IndependentMappingChildLayout : LinearLayout {
    companion object{
        private const val TAG = "Inde_Map_Child"
        private const val nominalVisvarContentsMinSize = 50
    }

    interface ChildViewInteractionListener{
        fun onShapeMappingContentsUpdated(componentIndex: Int, shapeType: VisShapeType)
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

    fun setMappingContentsChangedListener(listener: ChildViewInteractionListener){
        mappingContentsChangedListener = listener
    }

    private fun updateAppConfig(){
        viewModel?.appHaloConfigLiveData?.value?.let{ currentConfig ->

            val listSize = when(notiDataProp){
                NotiProperty.LIFE_STAGE -> EnhancedNotificationLife.values().size
                NotiProperty.IMPORTANCE -> currentConfig.independentDataParameters[objIndex].binNums
                NotiProperty.CONTENT -> currentConfig.keywordGroupPatterns.getOrderedKeywordGroups().size + 1
                else -> 5
            }

            when(notiVisVar){
                NotiVisVariable.MOTION -> {
                    val currentList = currentConfig.independentVisualParameters[objIndex].selectedMotionList

                    currentConfig.independentVisualParameters[objIndex].selectedMotionList = List(listSize){index ->
                        if(index >= visVarContents.size)
                            currentList[index]
                        else
                            (visVarContents as MutableList<AnimatorSet>)[index]
                    }
                }
                NotiVisVariable.COLOR -> {
                    val currentList = currentConfig.independentVisualParameters[objIndex].selectedColorList

                    currentConfig.independentVisualParameters[objIndex].selectedColorList = List(listSize){index ->
                        if(index >= visVarContents.size)
                            currentList[index]
                        else
                            (visVarContents as MutableList<Int>)[index]
                    }
                }
                NotiVisVariable.SHAPE -> {
                    val currentList = currentConfig.independentVisualParameters[objIndex].selectedShapeList

                    currentConfig.independentVisualParameters[objIndex].selectedShapeList = List(listSize){index ->
                        if(index >= visVarContents.size)
                            currentList[index]
                        else
                            (visVarContents as MutableList<VisObjectShape>)[index]
                    }
                }
                NotiVisVariable.SIZE -> {
                    val currentList = currentConfig.independentVisualParameters[objIndex].getSelectedSizeRangeList(
                            listSize
                    )

                    currentConfig.independentVisualParameters[objIndex].setSelectedSizeRangeList(
                            List(listSize){index ->
                                if(index >= visVarContents.size)
                                    currentList[index]
                                else
                                    (visVarContents as MutableList<Pair<Double, Double>>)[index]
                            }
                    )
                }
                NotiVisVariable.POSITION -> {
                    val currentList = currentConfig.independentVisualParameters[objIndex].getSelectedPosRangeList(
                            listSize
                    )

                    currentConfig.independentVisualParameters[objIndex].setSelectedPosRangeList(
                            List(listSize){index ->
                                if(index >= visVarContents.size)
                                    currentList[index]
                                else
                                    (visVarContents as MutableList<Pair<Double, Double>>)[index]
                            }
                    )
                }
            }

            when(notiDataProp){
                NotiProperty.LIFE_STAGE -> {
                    //currentConfig.independentDataParameters[objIndex].givenLifeList = (notiDataPropContents as MutableList<EnhancedNotificationLife>).toList()
                }
                NotiProperty.IMPORTANCE -> {
                    //currentConfig.independentDataParameters[objIndex].selectedImportanceRangeList = (notiDataPropContents as MutableList<Pair<Double, Double>>).toList()
                    currentConfig.independentDataParameters[objIndex].binNums = findViewById<NumberPicker>(R.id.bin_number_picker).value
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

    private fun setNominalTableMapping(appConfig: AppHaloConfig){
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
                        independentDataParams.givenLifeList
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
                            independentVisParams.getSelectedSizeRangeList(notiDataPropContents.size)

                        }
                    }
                    NotiVisVariable.POSITION -> {
                        if(notiDataPropContents.size == 0)
                        {
                            listOf(independentVisParams.selectedPosRange)
                        }
                        else{
                            independentVisParams.getSelectedPosRangeList(notiDataPropContents.size)
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
                //val givenLifeStageIndices = (notiDataPropContents as MutableList<EnhancedNotificationLife>).map{independentDataParams.givenLifeList.indexOf(it)}
                //setNewSpinnerView(givenLifeStageIndices, independentDataParams.givenLifeList, tableLayout)
                setNominalNotiPropViews(independentDataParams.givenLifeList, tableLayout, LayoutInflater.from(context))
            }
            NotiProperty.IMPORTANCE -> {
                val givenImportanceList = MapFunctionUtilities.bin(independentDataParams.givenImportanceRange, independentDataParams.binNums)
                //val givenImportanceIndices = (notiDataPropContents as MutableList<Pair<Double, Double>>).map{givenImportanceList.indexOf(it)}
                //setNewSpinnerView(givenImportanceIndices, givenImportanceList, tableLayout)
                setNominalNotiPropViews(givenImportanceList, tableLayout, LayoutInflater.from(context))
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

            setVisVarFrame(appConfig, index, frame, content)
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

    private fun setNominalMapping(appConfig: AppHaloConfig){
        View.inflate(context, R.layout.item_child_new_nominal_mapping, this)

        findViewById<LinearLayout>(R.id.bin_layout).let{binLayout ->
            binLayout.findViewById<NumberPicker>(R.id.bin_number_picker).apply{
                when(notiDataProp){
                    NotiProperty.IMPORTANCE -> {
                        minValue = 2
                        maxValue = 5
                        value = appConfig.independentDataParameters[objIndex].binNums
                        setOnValueChangedListener { _, oldVal, newVal ->
                            if(oldVal != newVal){
                                //table layout을 다시 그려줘야 될 것 같음
                                updateAppConfig()
                                viewModel?.appHaloConfigLiveData?.value?.let{
                                    setNominalTableMapping(it)
                                }
                            }
                        }
                    }
                    else ->{binLayout.visibility = View.GONE}
                }
            }
        }
        setNominalTableMapping(appConfig)
    }

    private fun setNominalNotiPropViews(givenPropContents: List<Any>, notiPropLayout: TableLayout, inflater: LayoutInflater){
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
                NominalVisVarContentSpinner.generateForIndependentMapping(context, appConfig, notiVisVar).let{ spinner ->
                    spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            (parent?.getItemAtPosition(position) as Triple<String, String, VisShapeType>?)?.let{ item->
                                when(item.third){
                                    VisShapeType.IMAGE -> {
                                        CropImage.activity()
                                                .setGuidelines(CropImageView.Guidelines.ON)
                                                .setActivityTitle("Set Image Drawable")
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

                    (spinner.adapter as NominalVisVarContentSpinner.NominalVisVarContentAdapter).let {adapter ->
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
                NominalVisVarContentSpinner.generateForIndependentMapping(context, appConfig, notiVisVar).let{ spinner ->
                    spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            (parent?.getItemAtPosition(position) as Triple<String, String, Any>?)?.let{ item ->
                                visVarContents[index] = item.third
                                updateAppConfig()
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                    (spinner.adapter as NominalVisVarContentSpinner.NominalVisVarContentAdapter).let {adapter ->
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
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.IndependentMappingChildLayout, defStyle, 0)
        a.recycle()
        mappingContentsChangedListener = null
    }
}
