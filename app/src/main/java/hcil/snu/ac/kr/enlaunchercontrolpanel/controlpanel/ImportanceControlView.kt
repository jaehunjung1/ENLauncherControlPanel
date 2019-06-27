package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.*
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponent
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponentAdapter
import io.apptik.widget.MultiSlider
import kotlinx.android.synthetic.main.layout_importance_control_view.view.*
import kotlinx.android.synthetic.main.layout_importance_saturation_control.view.*
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancementPattern
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.NotificationEnhacementParams
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * TODO: document your custom view class.
 */
class ImportanceControlView : LinearLayout {

    enum class SaturationTimeUnit{
        MINUTES,
        HOURS,
        OBSERVATION_WINDOW_SIZE
    }

    private var _exampleString: String? = null // TODO: use a default from R.string...
    private var _exampleColor: Int = Color.RED // TODO: use a default from R.color...
    private var _exampleDimension: Float = 0f // TODO: use a default from R.dimen...

    private var _initalImportance: Double = 0.5

    private var _patternBeforeInteraction: EnhancementPattern = EnhancementPattern.EQ
    private var _saturationNumberBeforeInteraction: Double = 0.0
    private var _saturationUnitBeforeInteraction: SaturationTimeUnit = SaturationTimeUnit.MINUTES

    private var _patternAfterInteraction: EnhancementPattern = EnhancementPattern.EQ
    private var _saturationNumberAfterInteraction: Double = 0.0
    private var _saturationUnitAfterInteraction: SaturationTimeUnit = SaturationTimeUnit.MINUTES

    private var viewModel: AppHaloConfigViewModel? = null
    private var invalidateFlag = true

    private var importanceSaturationExamples = listOf(
            HaloVisComponent("example1", R.drawable.kakaotalk_logo)
    )


    //Properties Set Programmatically

    private var textPaint: TextPaint? = null
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f


    var blackImportanceThreshold: Double = 0.0
        set(value){
            field = value
            invalidateTextPaintAndMeasurements()
        }
    var whiteImportanceThreshold: Double = 0.0
        set(value){
            field = value
            invalidateTextPaintAndMeasurements()
        }

    var blackLifeTimeThreshold: Long = 1000L * 60 * 60 * 3
        set(value){
            field = value
            invalidateTextPaintAndMeasurements()
        }

    var whiteLifeTimeThreshold: Long = 1000L * 60 * 60 * 1
        set(value){
            field = value
            invalidateTextPaintAndMeasurements()
        }


    var exampleString: String?
        get() = _exampleString
        set(value) {
            _exampleString = value
            invalidateTextPaintAndMeasurements()
        }

    var exampleColor: Int
        get() = _exampleColor
        set(value) {
            _exampleColor = value
            invalidateTextPaintAndMeasurements()
        }


    var exampleDimension: Float
        get() = _exampleDimension
        set(value) {
            _exampleDimension = value
            invalidateTextPaintAndMeasurements()
        }

    var exampleDrawable: Drawable? = null

    var initialImportance: Double
        get() = _initalImportance
        set(value){
            _initalImportance = value
            invalidateSaturationInformation()
        }

    var patternBeforeInteraction: EnhancementPattern
        get() = _patternBeforeInteraction
        set(value){
            _patternBeforeInteraction = value
            invalidateSaturationInformation()
        }


    var patternAfterInteraction: EnhancementPattern
        get() = _patternAfterInteraction
        set(value){
            _patternAfterInteraction = value
            invalidateSaturationInformation()
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


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        inflate(context, R.layout.layout_importance_control_view, this)

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.ImportanceControlView, defStyle, 0)

        _exampleString = a.getString(
                R.styleable.ImportanceControlView_exampleString)
        _exampleColor = a.getColor(
                R.styleable.ImportanceControlView_exampleColor,
                exampleColor)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        _exampleDimension = a.getDimension(
                R.styleable.ImportanceControlView_exampleDimension,
                exampleDimension)


        if (a.hasValue(R.styleable.ImportanceControlView_exampleDrawable)) {
            exampleDrawable = a.getDrawable(
                    R.styleable.ImportanceControlView_exampleDrawable)
            exampleDrawable?.callback = this
        }

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        val importanceSampleRecyclerView = findViewById<RecyclerView>(R.id.importanceExamples)
        val haloLayoutAdapter = HaloVisComponentAdapter(context!!, importanceSaturationExamples)
        importanceSampleRecyclerView.adapter = haloLayoutAdapter
        importanceSampleRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        initialImportanceSlider.apply{
            setOnThumbValueChangeListener { _, _, _, value ->
                initialImportance = (value.toDouble()) * 0.1
            }
        }

        val firstSaturationControl = findViewById<LinearLayout>(R.id.firstSaturation)
        val firstSaturationImportancePattern = firstSaturationControl.findViewById<Spinner>(R.id.importancePattern)
        val firstSaturationTime = firstSaturationControl.findViewById<EditText>(R.id.saturationTime)
        val firstSaturationUnit = firstSaturationControl.findViewById<Spinner>(R.id.saturationUnit)

        firstSaturationImportancePattern.apply{
            adapter = ArrayAdapter(context, R.layout.spinner_item, EnhancementPattern.values())
            onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    patternBeforeInteraction = getItemAtPosition(position) as EnhancementPattern
                    when(patternBeforeInteraction){
                        EnhancementPattern.EQ -> {
                            firstSaturationTime.visibility = View.INVISIBLE
                            firstSaturationUnit.visibility = View.INVISIBLE
                        }
                        else -> {
                            if(firstSaturationTime.visibility == View.INVISIBLE){
                                firstSaturationTime.visibility = View.VISIBLE
                                firstSaturationUnit.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        val secondSaturationControl = findViewById<LinearLayout>(R.id.secondSaturation)
        val secondSaturationImportancePattern = secondSaturationControl.findViewById<Spinner>(R.id.importancePattern)
        val secondSaturationTime = secondSaturationControl.findViewById<EditText>(R.id.saturationTime)
        val secondSaturationUnit = secondSaturationControl.findViewById<Spinner>(R.id.saturationUnit)

        secondSaturationImportancePattern.apply{
            adapter = ArrayAdapter(context, R.layout.spinner_item, EnhancementPattern.values())
            onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    patternAfterInteraction = getItemAtPosition(position) as EnhancementPattern
                    when(patternAfterInteraction){
                        EnhancementPattern.EQ -> {
                            secondSaturationTime.visibility = View.INVISIBLE
                            secondSaturationUnit.visibility = View.INVISIBLE
                        }
                        else -> {
                            if(secondSaturationTime.visibility == View.INVISIBLE){
                                secondSaturationTime.visibility = View.VISIBLE
                                secondSaturationUnit.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        firstSaturation.saturationTime.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                _saturationNumberBeforeInteraction = s.toString().toDouble()
                invalidateSaturationInformation()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        secondSaturation.saturationTime.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                _saturationNumberAfterInteraction = s.toString().toDouble()
                invalidateSaturationInformation()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        firstSaturation.saturationUnit.apply{
            adapter = ArrayAdapter(context, R.layout.spinner_item, SaturationTimeUnit.values())
            onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    _saturationUnitBeforeInteraction = getItemAtPosition(position) as SaturationTimeUnit
                    invalidateSaturationInformation()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        secondSaturation.saturationUnit.apply{
            adapter = ArrayAdapter(context, R.layout.spinner_item, SaturationTimeUnit.values())
            onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    _saturationUnitAfterInteraction = getItemAtPosition(position) as SaturationTimeUnit
                    invalidateSaturationInformation()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    fun setViewModel(appConfigViewModel: AppHaloConfigViewModel){
        viewModel = appConfigViewModel
        appConfigViewModel.appHaloConfigLiveData.value?.let{
            invalidateFlag = false
            val currentParam = it.notificationEnhancementParams
            blackLifeTimeThreshold = currentParam.lifespan
            blackImportanceThreshold = currentParam.importanceRange.first

            _initalImportance = currentParam.initialImportance
            initialImportanceSlider.getThumb(0).value = (_initalImportance * 10).roundToInt()

            _patternBeforeInteraction = currentParam.firstPattern
            firstSaturation.importancePattern.setSelection(_patternBeforeInteraction.ordinal)

            _patternAfterInteraction = currentParam.secondPattern
            secondSaturation.importancePattern.setSelection(_patternAfterInteraction.ordinal)

            val firstSaturationTime = currentParam.firstSaturationTime
            firstSaturation.saturationTime.setText(
                    (firstSaturationTime.toDouble() / blackLifeTimeThreshold).toString()
            )
            firstSaturation.saturationUnit.setSelection(SaturationTimeUnit.OBSERVATION_WINDOW_SIZE.ordinal)

            val secondSaturationTime = currentParam.secondSaturationTime
            secondSaturation.saturationTime.setText(
                    (secondSaturationTime.toDouble() / blackLifeTimeThreshold).toString()
            )
            secondSaturation.saturationUnit.setSelection(SaturationTimeUnit.OBSERVATION_WINDOW_SIZE.ordinal)
            invalidateFlag = true
        }
    }

    private fun invalidateSaturationInformation(){
        if(!invalidateFlag)
            return

        val importanceOffset = _initalImportance
        val lifespan = blackLifeTimeThreshold
        val importanceLowerBound = blackImportanceThreshold
        val importanceUpperBound = 1.0

        val firstPattern = _patternBeforeInteraction
        val secondPattern = _patternAfterInteraction

        val firstSaturationTime: Long = when(_saturationUnitBeforeInteraction){
            SaturationTimeUnit.MINUTES -> {
                (_saturationNumberBeforeInteraction * 1000L * 60).roundToLong()
            }
            SaturationTimeUnit.HOURS -> {
                (_saturationNumberBeforeInteraction * 1000L * 60 * 60).roundToLong()
            }
            SaturationTimeUnit.OBSERVATION_WINDOW_SIZE -> {
                (_saturationNumberBeforeInteraction * lifespan).roundToLong()
            }
        }

        val secondSaturationTime: Long = when(_saturationUnitAfterInteraction){
            SaturationTimeUnit.MINUTES -> {
                (_saturationNumberAfterInteraction * 1000L * 60).roundToLong()
            }
            SaturationTimeUnit.HOURS -> {
                (_saturationNumberAfterInteraction * 1000L * 60 * 60).roundToLong()
            }
            SaturationTimeUnit.OBSERVATION_WINDOW_SIZE -> {
                (_saturationNumberAfterInteraction * lifespan).roundToLong()
            }
        }


        viewModel?.appHaloConfigLiveData?.value?.let{appConfig ->
            appConfig.notificationEnhancementParams = NotificationEnhacementParams(
                    importanceOffset,
                    lifespan,
                    Pair(importanceLowerBound, importanceUpperBound),
                    firstPattern,
                    secondPattern,
                    firstSaturationTime,
                    secondSaturationTime
            )
            viewModel!!.appHaloConfigLiveData.value = appConfig
        }
    }

    private fun invalidateTextPaintAndMeasurements() {
        /*
        textPaint?.let {
            it.textSize = exampleDimension
            it.color = exampleColor
            textWidth = it.measureText(exampleString)
            textHeight = it.fontMetrics.bottom
        }
        */
    }

}
