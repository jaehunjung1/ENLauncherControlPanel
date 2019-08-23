package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.*
import io.apptik.widget.MultiSlider
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancementPattern
import kr.ac.snu.hcil.datahalo.visconfig.KeywordGroupImportance
import kr.ac.snu.hcil.datahalo.visconfig.NotificationEnhancementParams
import kr.ac.snu.hcil.datahalo.visconfig.WGBFilterVar
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.ImportanceControlView
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class KeywordGroupChildView: LinearLayout {
    companion object{
        private const val TAG = "KeywordGroup_Child"
    }

    private var keywordGroupImportance: KeywordGroupImportance? = null

    private var _initalImportance: Double = 0.5

    private var _patternBeforeInteraction: EnhancementPattern = EnhancementPattern.EQ
    private var _saturationTimeBeforeInteraction: Long = 0L
    private var _saturationUnitBeforeInteraction: ImportanceControlView.SaturationTimeUnit = ImportanceControlView.SaturationTimeUnit.MINUTES

    private var _patternAfterInteraction: EnhancementPattern = EnhancementPattern.EQ
    private var _saturationTimeAfterInteraction: Long = 0L
    private var _saturationUnitAfterInteraction: ImportanceControlView.SaturationTimeUnit = ImportanceControlView.SaturationTimeUnit.MINUTES

    private var timeOut: Long = -1L
    private var invalidateFlag = true

    interface KeywordGroupChildInteractionListener{
        fun onEnhancementParamUpdated(pattern: NotificationEnhancementParams)
    }

    var keywordGroupChildInteractionListener: KeywordGroupChildInteractionListener? = null

    constructor(context: Context): super(context){
        init(null, 0)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

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

    var saturationTimeBeforeInteraction: Long
        get() = _saturationTimeBeforeInteraction
        set(value){
            _saturationTimeBeforeInteraction = value
            invalidateSaturationInformation()
        }

    var saturationTimeAfterInteraction: Long
        get() = _saturationTimeAfterInteraction
        set(value){
            _saturationTimeAfterInteraction = value
            invalidateSaturationInformation()
        }

    var saturationUnitBeforeInteraction: ImportanceControlView.SaturationTimeUnit
        get() = _saturationUnitBeforeInteraction
        set(value){
            _saturationUnitBeforeInteraction = value
            invalidateSaturationInformation()
        }

    var saturationUnitAfterInteraction: ImportanceControlView.SaturationTimeUnit
        get() = _saturationUnitAfterInteraction
        set(value){
            _saturationUnitAfterInteraction = value
            invalidateSaturationInformation()
        }

    private fun calculateSaturationNumber(timeInMiliSec: Long, unit: ImportanceControlView.SaturationTimeUnit): Double {
        return when(unit){
            ImportanceControlView.SaturationTimeUnit.MINUTES -> {
                timeInMiliSec / (1000 * 60).toDouble()
            }
            ImportanceControlView.SaturationTimeUnit.HOURS -> {
                timeInMiliSec / (1000 * 60 * 60).toDouble()
            }
            ImportanceControlView.SaturationTimeUnit.OBSERVATION_WINDOW_SIZE -> {
                timeInMiliSec / timeOut.toDouble()
            }
        }
    }

    private fun calculateSaturationTime(number: Double, unit: ImportanceControlView.SaturationTimeUnit): Long {
        return when(unit){
            ImportanceControlView.SaturationTimeUnit.MINUTES -> {
                (number * 1000 * 60).roundToLong()
            }
            ImportanceControlView.SaturationTimeUnit.HOURS -> {
                (number * 1000 * 60 * 60).roundToLong()
            }
            ImportanceControlView.SaturationTimeUnit.OBSERVATION_WINDOW_SIZE -> {
                (number * timeOut).roundToLong()
            }
        }
    }

    fun setProperties(
            observationWindowFilter: Map<WGBFilterVar, Any>,
            keywordGroupImportance: KeywordGroupImportance
    ){

        //Initial Data Setting
        timeOut = observationWindowFilter[WGBFilterVar.BLACK_COND] as Long
        this.keywordGroupImportance = keywordGroupImportance
        keywordGroupImportance.enhancementParam.let{ param ->
            _initalImportance = param.initialImportance
            _patternBeforeInteraction = param.firstPattern
            _patternAfterInteraction = param.secondPattern
            _saturationTimeBeforeInteraction = param.firstSaturationTime
            _saturationUnitBeforeInteraction = ImportanceControlView.SaturationTimeUnit.OBSERVATION_WINDOW_SIZE
            _saturationTimeAfterInteraction = param.secondSaturationTime
            _saturationUnitAfterInteraction = ImportanceControlView.SaturationTimeUnit.OBSERVATION_WINDOW_SIZE
        }

        invalidateFlag = false
        //Update UI

        val importance = (initialImportance * 10).roundToInt()
        findViewById<TextView>(R.id.initialImportance).text = importance.toString()
        findViewById<MultiSlider>(R.id.initialImportanceSlider).let{ slider ->
            slider.getThumb(0).value = importance
        }

        findViewById<LinearLayout>(R.id.firstSaturation).let{
            it.findViewById<Spinner>(R.id.importancePattern).let{ spinner ->
                spinner.setSelection(patternBeforeInteraction.ordinal)
            }

            it.findViewById<EditText>(R.id.saturationTime).let{ editText ->
                editText.setText(
                        calculateSaturationNumber(
                                saturationTimeBeforeInteraction,
                                saturationUnitBeforeInteraction
                        ).toString()
                )
            }
            it.findViewById<Spinner>(R.id.saturationUnit).let{ spinner ->
                spinner.setSelection(saturationUnitBeforeInteraction.ordinal)
            }

        }

        findViewById<LinearLayout>(R.id.secondSaturation).let{
            it.findViewById<Spinner>(R.id.importancePattern).let{ spinner ->
                spinner.setSelection(patternAfterInteraction.ordinal)
            }

            it.findViewById<EditText>(R.id.saturationTime).let{ editText ->
                editText.setText(
                        calculateSaturationNumber(
                                saturationTimeAfterInteraction,
                                saturationUnitAfterInteraction
                        ).toString()
                )
            }
            it.findViewById<Spinner>(R.id.saturationUnit).let{ spinner ->
                spinner.setSelection(saturationUnitAfterInteraction.ordinal)
            }
        }

        invalidateFlag = true
    }

    private fun invalidateSaturationInformation(){
        if (!invalidateFlag) return

        keywordGroupImportance?.let{ keywordGroupImportance ->
            findViewById<TextView>(R.id.initialImportance).text = (initialImportance * 10).roundToInt().toString()
            keywordGroupChildInteractionListener?.onEnhancementParamUpdated(
                    keywordGroupImportance.enhancementParam.apply{
                        initialImportance = this@KeywordGroupChildView.initialImportance
                        firstPattern = this@KeywordGroupChildView.patternBeforeInteraction
                        secondPattern = this@KeywordGroupChildView.patternAfterInteraction
                        firstSaturationTime = this@KeywordGroupChildView._saturationTimeBeforeInteraction
                        secondSaturationTime = this@KeywordGroupChildView.saturationTimeAfterInteraction
                    }
            )
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int){
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.KeywordGroupChildView, defStyle, 0)
        //if attributes are required
        a.recycle()

        View.inflate(context, R.layout.item_child_keywordgroup_expandable_controlpanel, this)


        findViewById<MultiSlider>(R.id.initialImportanceSlider).let{ slider ->
            slider.setOnThumbValueChangeListener { _, _, _, value ->
                initialImportance = (value.toDouble()) * 0.1
            }
        }

        findViewById<LinearLayout>(R.id.firstSaturation).let{ firstSaturationControl ->
            val firstSaturationImportancePattern = firstSaturationControl.findViewById<Spinner>(R.id.importancePattern)
            val firstSaturationTime = firstSaturationControl.findViewById<EditText>(R.id.saturationTime)
            val firstSaturationUnit = firstSaturationControl.findViewById<Spinner>(R.id.saturationUnit)

            firstSaturationImportancePattern.apply{
                adapter = ArrayAdapter(context, R.layout.item_spinner, EnhancementPattern.values())
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

            firstSaturationTime.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val saturationNum = s.toString().toDouble()
                    saturationTimeBeforeInteraction = calculateSaturationTime(saturationNum, saturationUnitBeforeInteraction)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            firstSaturationUnit.apply{
                adapter = ArrayAdapter(context, R.layout.item_spinner, ImportanceControlView.SaturationTimeUnit.values())
                onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        _saturationUnitBeforeInteraction = getItemAtPosition(position) as ImportanceControlView.SaturationTimeUnit
                        firstSaturationTime.setText(
                                calculateSaturationNumber(
                                        saturationTimeBeforeInteraction,
                                        saturationUnitBeforeInteraction
                                ).toString()
                        )
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }

        findViewById<LinearLayout>(R.id.secondSaturation).let{ secondSaturationControl ->
            val secondSaturationImportancePattern = secondSaturationControl.findViewById<Spinner>(R.id.importancePattern)
            val secondSaturationTime = secondSaturationControl.findViewById<EditText>(R.id.saturationTime)
            val secondSaturationUnit = secondSaturationControl.findViewById<Spinner>(R.id.saturationUnit)

            secondSaturationImportancePattern.apply{
                adapter = ArrayAdapter(context, R.layout.item_spinner, EnhancementPattern.values())
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

            secondSaturationTime.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val saturationNum = s.toString().toDouble()
                    saturationTimeAfterInteraction = calculateSaturationTime(saturationNum, saturationUnitAfterInteraction)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            secondSaturationUnit.apply{
                adapter = ArrayAdapter(context, R.layout.item_spinner, ImportanceControlView.SaturationTimeUnit.values())
                onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        _saturationUnitAfterInteraction = getItemAtPosition(position) as ImportanceControlView.SaturationTimeUnit
                        secondSaturationTime.setText(
                                calculateSaturationNumber(
                                        saturationTimeAfterInteraction,
                                        saturationUnitAfterInteraction
                                ).toString()
                        )
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }
}