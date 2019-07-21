package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.jaygoo.widget.VerticalRangeSeekBar
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable

import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.utilities.Utilities


class ContToContUI(context: Context) : ConstraintLayout(context) {
    lateinit var set: ConstraintSet
    lateinit var leftSeekBar: VerticalRangeSeekBar
    lateinit var rightSeekBar: VerticalRangeSeekBar
    lateinit var contMappingView: ContMappingView

    internal var isLeftInverted: Boolean = false
    internal var isRightInverted: Boolean = false

    private var viewModel: AppHaloConfigViewModel? = null
    private var notiProperty: NotiProperty? = null
    private var notiVisVar: NuNotiVisVariable? = null

    private var notiPropRange: Pair<Double, Double> = Pair(-1.0, -1.0)
    private var notiVisVarRange: Pair<Double, Double> = Pair(-1.0, -1.0)

    //TODO(mapping function Range와 Range 연결)


    fun setViewModel(configViewModel: AppHaloConfigViewModel){
        viewModel = configViewModel
    }

    fun setMapping(visVar: NuNotiVisVariable, notiProp: NotiProperty){
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
                    NuNotiVisVariable.POSITION -> {
                        notiVisVarRange = config.independentVisualParameters[0].selectedPosRange
                    }
                    NuNotiVisVariable.SIZE -> {
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

                contMappingView.invalidateSize(thumbPos)
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
    val config: FloatArray
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
                contMappingView.invalidateSize(thumbPos)
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {}
        })

        rightSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                if (leftValue == rightValue)
                    isRightInverted = !isRightInverted
                contMappingView.invalidateSize(thumbPos)
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
                contMappingView = ContMappingView(context,
                        leftSeekBar.left + paddingLeft, rightSeekBar.left + paddingRight)
                Log.i("duh", leftSeekBar.x.toString())
                contMappingView.id = View.generateViewId()
                contMappingView.layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                addView(contMappingView, 0)

                set = ConstraintSet()
                set.clone(this@ContToContUI)
                set.connect(contMappingView.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                set.connect(contMappingView.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)
                set.connect(contMappingView.id, ConstraintSet.LEFT, id, ConstraintSet.LEFT)
                set.connect(contMappingView.id, ConstraintSet.RIGHT, id, ConstraintSet.RIGHT)
                set.applyTo(this@ContToContUI)

                contMappingView.invalidateSize(thumbPos)

                return true
            }
        })
    }
}
