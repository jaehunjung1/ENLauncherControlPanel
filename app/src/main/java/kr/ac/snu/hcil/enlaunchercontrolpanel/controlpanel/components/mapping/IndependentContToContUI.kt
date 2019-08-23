package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.jaygoo.widget.VerticalRangeSeekBar
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable

import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.utilities.Utilities


class IndependentContToContUI(context: Context) : ConstraintLayout(context) {
    lateinit var set: ConstraintSet
    lateinit var leftSeekBar: VerticalRangeSeekBar
    lateinit var rightSeekBar: VerticalRangeSeekBar
    private var contMappingView: ContMappingView? = null

    internal var isLeftInverted: Boolean = false
    internal var isRightInverted: Boolean = false

    private var viewModel: AppHaloConfigViewModel? = null
    private var notiProperty: NotiProperty? = null
    private var notiVisVar: NotiVisVariable? = null

    private var notiPropRange: Pair<Double, Double> = Pair(0.0, 1.0)
    private var notiVisVarRange: Pair<Double, Double> = Pair(0.0, 1.0)

    fun setViewModel(configViewModel: AppHaloConfigViewModel?){
        viewModel = configViewModel
    }

    fun setMapping(visVar: NotiVisVariable, notiProp: NotiProperty){
        if(notiProperty != notiProp || notiVisVar != visVar){
            notiProperty = notiProp
            notiVisVar = visVar

            viewModel?.appHaloConfigLiveData?.value?.let{ config ->
                when(notiProperty){
                    NotiProperty.IMPORTANCE -> {
                        notiPropRange = config.independentDataParameters[0].selectedImportanceRange
                        if(notiPropRange.first <= notiPropRange.second){
                            isLeftInverted = false
                            leftSeekBar.setProgress(100 * notiPropRange.first.toFloat(), 100 * notiPropRange.second.toFloat())
                        }
                        else{
                            isLeftInverted = true
                            leftSeekBar.setProgress(100 * notiPropRange.second.toFloat(), 100 * notiPropRange.first.toFloat())
                        }
                    }
                    else -> {
                        // do nothing
                    }
                }
                when(notiVisVar){
                    NotiVisVariable.POSITION -> {
                        notiVisVarRange = config.independentVisualParameters[0].selectedPosRange
                    }
                    NotiVisVariable.SIZE -> {
                        notiVisVarRange = config.independentVisualParameters[0].selectedSizeRange
                    }
                    else ->{
                        // do nothing
                    }
                }

                if(notiVisVarRange.first <= notiVisVarRange.second){
                    isRightInverted = false
                    rightSeekBar.setProgress(100 * notiVisVarRange.first.toFloat(), 100 * notiVisVarRange.second.toFloat())
                }
                else{
                    isRightInverted = true
                    rightSeekBar.setProgress(100 * notiVisVarRange.second.toFloat(), 100 * notiVisVarRange.first.toFloat())
                }

                contMappingView?.invalidateSize(thumbPos)
            }
        }
    }

    private val thumbPos: FloatArray
        get() {
            val thumbHeight = Utilities.dpToPx(context, 15).toFloat()
            val padding = Utilities.dpToPx(context, 10).toFloat()
            val height = leftSeekBar.height - 2 * padding

            val leftFirstVal = if (isLeftInverted) leftSeekBar.rightSeekBar.progress else leftSeekBar.leftSeekBar.progress
            val leftSecondVal = if (isLeftInverted) leftSeekBar.leftSeekBar.progress else leftSeekBar.rightSeekBar.progress
            val rightFirstVal = if (isRightInverted) rightSeekBar.rightSeekBar.progress else rightSeekBar.leftSeekBar.progress
            val rightSecondVal = if (isRightInverted) rightSeekBar.leftSeekBar.progress else rightSeekBar.rightSeekBar.progress


            val maxProgress = leftSeekBar.maxProgress

            val leftStart = padding + thumbHeight / 2 + height * leftFirstVal / maxProgress
            val leftEnd = padding + thumbHeight / 2 + height * leftSecondVal / maxProgress
            val rightStart = padding + thumbHeight / 2 + height * rightFirstVal / maxProgress
            val rightEnd = padding + thumbHeight / 2 + height * rightSecondVal / maxProgress

            return floatArrayOf(leftStart, rightStart, rightEnd, leftEnd)
        }

    /* returns [left start, left end, right start, right end] */
    private val config: FloatArray
        get() {
            val result = FloatArray(4)
            if (!isLeftInverted) {
                result[0] = leftSeekBar.leftSeekBar.progress
                result[1] = leftSeekBar.rightSeekBar.progress
            } else {
                result[0] = leftSeekBar.rightSeekBar.progress
                result[1] = leftSeekBar.leftSeekBar.progress
            }

            if (!isRightInverted) {
                result[2] = rightSeekBar.leftSeekBar.progress
                result[3] = rightSeekBar.rightSeekBar.progress
            } else {
                result[2] = rightSeekBar.rightSeekBar.progress
                result[3] = rightSeekBar.leftSeekBar.progress
            }

            return result
        }

    init {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.cont_to_cont_ui, this)
        leftSeekBar = findViewById(R.id.sb_left_start)
        rightSeekBar = findViewById(R.id.sb_right_start)
        leftSeekBar.setProgress(20f, 80f)
        rightSeekBar.setProgress(20f, 80f)

        leftSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                if (leftValue == rightValue)
                    isLeftInverted = !isLeftInverted
                contMappingView?.invalidateSize(thumbPos)

                viewModel?.appHaloConfigLiveData?.value?.let{ currentConfig ->
                    when(notiProperty){
                        NotiProperty.IMPORTANCE -> {
                            val rangeLeft = leftSeekBar.leftSeekBar.progress / 100.0
                            val rangeRight = leftSeekBar.rightSeekBar.progress / 100.0

                            if(isLeftInverted){
                                currentConfig.independentDataParameters[0].selectedImportanceRange = Pair(rangeRight, rangeLeft)

                            } else{
                                currentConfig.independentDataParameters[0].selectedImportanceRange = Pair(rangeLeft, rangeRight)
                            }
                        }
                        else -> {
                            //do nothing
                        }
                    }
                    viewModel?.appHaloConfigLiveData?.value = currentConfig
                }
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
        })

        rightSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                if (leftValue == rightValue)
                    isRightInverted = !isRightInverted
                contMappingView?.invalidateSize(thumbPos)

                viewModel?.appHaloConfigLiveData?.value?.let{ currentConfig ->
                    when(notiVisVar){
                        NotiVisVariable.POSITION -> {
                            val rangeLeft = rightSeekBar.leftSeekBar.progress / 100.0
                            val rangeRight = rightSeekBar.rightSeekBar.progress / 100.0

                            if(isRightInverted){
                                currentConfig.independentVisualParameters[0].selectedPosRange = Pair(rangeRight, rangeLeft)

                            } else{
                                currentConfig.independentVisualParameters[0].selectedPosRange = Pair(rangeLeft, rangeRight)
                            }
                        }
                        NotiVisVariable.SIZE -> {
                            val rangeLeft = rightSeekBar.leftSeekBar.progress / 100.0
                            val rangeRight = rightSeekBar.rightSeekBar.progress / 100.0

                            if(isRightInverted){
                                currentConfig.independentVisualParameters[0].selectedSizeRange = Pair(rangeRight, rangeLeft)

                            } else{
                                currentConfig.independentVisualParameters[0].selectedSizeRange = Pair(rangeLeft, rangeRight)
                            }
                        }
                        else -> {
                            //do nothing
                        }
                    }
                    viewModel?.appHaloConfigLiveData?.value = currentConfig
                }
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
        })

        isLeftInverted = false
        isRightInverted = false

        leftSeekBar.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val paddingLeft = Utilities.dpToPx(context, 7).toFloat()
                val paddingRight = Utilities.dpToPx(context, 9).toFloat()
                leftSeekBar.viewTreeObserver.removeOnPreDrawListener(this)
                contMappingView = ContMappingView(context, leftSeekBar.left + paddingLeft, rightSeekBar.left + paddingRight).apply{
                    id = View.generateViewId()
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(this, 0)

                    set = ConstraintSet()
                    set.clone(this@IndependentContToContUI)
                    set.connect(id, ConstraintSet.TOP, this@IndependentContToContUI.id, ConstraintSet.TOP)
                    set.connect(id, ConstraintSet.BOTTOM, this@IndependentContToContUI.id, ConstraintSet.BOTTOM)
                    set.connect(id, ConstraintSet.LEFT, this@IndependentContToContUI.id, ConstraintSet.LEFT)
                    set.connect(id, ConstraintSet.RIGHT, this@IndependentContToContUI.id, ConstraintSet.RIGHT)
                    set.applyTo(this@IndependentContToContUI)

                    invalidateSize(thumbPos)
                }
                return true
            }
        })
    }
}

class AggregatedContToContUI(context: Context) : ConstraintLayout(context) {
    lateinit var set: ConstraintSet
    lateinit var leftSeekBar: VerticalRangeSeekBar
    lateinit var rightSeekBar: VerticalRangeSeekBar
    private var contMappingView: ContMappingView? = null

    internal var isLeftInverted: Boolean = false
    internal var isRightInverted: Boolean = false

    private var viewModel: AppHaloConfigViewModel? = null
    private var notiProperty: NotiProperty? = null
    private var notiVisVar: NotiVisVariable? = null

    private var notiPropRange: Pair<Double, Double> = Pair(0.0, 1.0)
    private var notiVisVarRange: Pair<Double, Double> = Pair(0.0, 1.0)

    fun setViewModel(configViewModel: AppHaloConfigViewModel?){
        viewModel = configViewModel
    }

    fun setMapping(visVar: NotiVisVariable, notiProp: NotiProperty){
        if(notiProperty != notiProp || notiVisVar != visVar){
            notiProperty = notiProp
            notiVisVar = visVar

            viewModel?.appHaloConfigLiveData?.value?.let{ config ->
                when(notiProperty){
                    NotiProperty.IMPORTANCE -> {
                        notiPropRange = config.aggregatedDataParameters[0].selectedImportanceRange
                        if(notiPropRange.first <= notiPropRange.second){
                            isLeftInverted = false
                            leftSeekBar.setProgress(100 * notiPropRange.first.toFloat(), 100 * notiPropRange.second.toFloat())
                        }
                        else{
                            isLeftInverted = true
                            leftSeekBar.setProgress(100 * notiPropRange.second.toFloat(), 100 * notiPropRange.first.toFloat())
                        }
                    }
                    else -> {
                        // do nothing
                    }
                }
                when(notiVisVar){
                    NotiVisVariable.POSITION -> {
                        notiVisVarRange = config.aggregatedVisualParameters[0].selectedPosRange
                    }
                    NotiVisVariable.SIZE -> {
                        notiVisVarRange = config.aggregatedVisualParameters[0].selectedSizeRange
                    }
                    else ->{
                        // do nothing
                    }
                }

                if(notiVisVarRange.first <= notiVisVarRange.second){
                    isRightInverted = false
                    rightSeekBar.setProgress(100 * notiVisVarRange.first.toFloat(), 100 * notiVisVarRange.second.toFloat())
                }
                else{
                    isRightInverted = true
                    rightSeekBar.setProgress(100 * notiVisVarRange.second.toFloat(), 100 * notiVisVarRange.first.toFloat())
                }

                contMappingView?.invalidateSize(thumbPos)
            }
        }
    }

    private val thumbPos: FloatArray
        get() {
            val thumbHeight = Utilities.dpToPx(context, 15).toFloat()
            val padding = Utilities.dpToPx(context, 10).toFloat()
            val height = leftSeekBar.height - 2 * padding

            val leftFirstVal = if (isLeftInverted) leftSeekBar.rightSeekBar.progress else leftSeekBar.leftSeekBar.progress
            val leftSecondVal = if (isLeftInverted) leftSeekBar.leftSeekBar.progress else leftSeekBar.rightSeekBar.progress
            val rightFirstVal = if (isRightInverted) rightSeekBar.rightSeekBar.progress else rightSeekBar.leftSeekBar.progress
            val rightSecondVal = if (isRightInverted) rightSeekBar.leftSeekBar.progress else rightSeekBar.rightSeekBar.progress


            val maxProgress = leftSeekBar.maxProgress

            val leftStart = padding + thumbHeight / 2 + height * leftFirstVal / maxProgress
            val leftEnd = padding + thumbHeight / 2 + height * leftSecondVal / maxProgress
            val rightStart = padding + thumbHeight / 2 + height * rightFirstVal / maxProgress
            val rightEnd = padding + thumbHeight / 2 + height * rightSecondVal / maxProgress

            return floatArrayOf(leftStart, rightStart, rightEnd, leftEnd)
        }

    /* returns [left start, left end, right start, right end] */
    private val config: FloatArray
        get() {
            val result = FloatArray(4)
            if (!isLeftInverted) {
                result[0] = leftSeekBar.leftSeekBar.progress
                result[1] = leftSeekBar.rightSeekBar.progress
            } else {
                result[0] = leftSeekBar.rightSeekBar.progress
                result[1] = leftSeekBar.leftSeekBar.progress
            }

            if (!isRightInverted) {
                result[2] = rightSeekBar.leftSeekBar.progress
                result[3] = rightSeekBar.rightSeekBar.progress
            } else {
                result[2] = rightSeekBar.rightSeekBar.progress
                result[3] = rightSeekBar.leftSeekBar.progress
            }

            return result
        }

    init {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.cont_to_cont_ui, this)
        leftSeekBar = findViewById(R.id.sb_left_start)
        rightSeekBar = findViewById(R.id.sb_right_start)
        leftSeekBar.setProgress(20f, 80f)
        rightSeekBar.setProgress(20f, 80f)

        leftSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                if (leftValue == rightValue)
                    isLeftInverted = !isLeftInverted
                contMappingView?.invalidateSize(thumbPos)

                viewModel?.appHaloConfigLiveData?.value?.let{ currentConfig ->
                    when(notiProperty){
                        NotiProperty.IMPORTANCE -> {
                            val rangeLeft = leftSeekBar.leftSeekBar.progress / 100.0
                            val rangeRight = leftSeekBar.rightSeekBar.progress / 100.0

                            if(isLeftInverted){
                                currentConfig.aggregatedDataParameters[0].selectedImportanceRange = Pair(rangeRight, rangeLeft)

                            } else{
                                currentConfig.aggregatedDataParameters[0].selectedImportanceRange = Pair(rangeLeft, rangeRight)
                            }
                        }
                        else -> {
                            //do nothing
                        }
                    }
                    viewModel?.appHaloConfigLiveData?.value = currentConfig
                }
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
        })

        rightSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                if (leftValue == rightValue)
                    isRightInverted = !isRightInverted
                contMappingView?.invalidateSize(thumbPos)

                viewModel?.appHaloConfigLiveData?.value?.let{ currentConfig ->
                    when(notiVisVar){
                        NotiVisVariable.POSITION -> {
                            val rangeLeft = rightSeekBar.leftSeekBar.progress / 100.0
                            val rangeRight = rightSeekBar.rightSeekBar.progress / 100.0

                            if(isRightInverted){
                                currentConfig.aggregatedVisualParameters[0].selectedPosRange = Pair(rangeRight, rangeLeft)

                            } else{
                                currentConfig.aggregatedVisualParameters[0].selectedPosRange = Pair(rangeLeft, rangeRight)
                            }
                        }
                        NotiVisVariable.SIZE -> {
                            val rangeLeft = rightSeekBar.leftSeekBar.progress / 100.0
                            val rangeRight = rightSeekBar.rightSeekBar.progress / 100.0

                            if(isRightInverted){
                                currentConfig.aggregatedVisualParameters[0].selectedSizeRange = Pair(rangeRight, rangeLeft)

                            } else{
                                currentConfig.aggregatedVisualParameters[0].selectedSizeRange = Pair(rangeLeft, rangeRight)
                            }
                        }
                        else -> {
                            //do nothing
                        }
                    }
                    viewModel?.appHaloConfigLiveData?.value = currentConfig
                }
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
        })

        isLeftInverted = false
        isRightInverted = false

        leftSeekBar.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val paddingLeft = Utilities.dpToPx(context, 7).toFloat()
                val paddingRight = Utilities.dpToPx(context, 9).toFloat()
                leftSeekBar.viewTreeObserver.removeOnPreDrawListener(this)
                contMappingView = ContMappingView(context, leftSeekBar.left + paddingLeft, rightSeekBar.left + paddingRight).apply{
                    id = View.generateViewId()
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    addView(this, 0)

                    set = ConstraintSet()
                    set.clone(this@AggregatedContToContUI)
                    set.connect(id, ConstraintSet.TOP, this@AggregatedContToContUI.id, ConstraintSet.TOP)
                    set.connect(id, ConstraintSet.BOTTOM, this@AggregatedContToContUI.id, ConstraintSet.BOTTOM)
                    set.connect(id, ConstraintSet.LEFT, this@AggregatedContToContUI.id, ConstraintSet.LEFT)
                    set.connect(id, ConstraintSet.RIGHT, this@AggregatedContToContUI.id, ConstraintSet.RIGHT)
                    set.applyTo(this@AggregatedContToContUI)

                    invalidateSize(thumbPos)
                }
                return true
            }
        })
    }
}
