package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout

import com.nex3z.flowlayout.FlowLayout
import com.robertlevonyan.views.chip.Chip

import java.util.ArrayList

import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import io.apptik.widget.MultiSlider
import kotlinx.android.synthetic.main.datafilter_card_view.*
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.WGBFilterVar
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection.ComponentExampleSelectionView
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection.HaloVisComponent
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class HaloDataSettingFragment : androidx.fragment.app.Fragment() {

    // Data Parameters
    val maxTimeWindow: Long = 1000L * 60 * 60 * 24

    val filterEnhancmentMin: Double = 0.0
    val filterEnhancmentMax: Double = 10.0

    var filterObservationWindowMin: Long = 0L
    var filterObservationWindowMax: Long = maxTimeWindow

    val observationTimeUnit: Long = 60 * 60 * 1000L
    val observationTimeScales: List<Double> = listOf(0.0, 0.25, 0.5, 1.0, 2.0, 4.0, 6.0, 12.0, 24.0, 24.0*7)

    var keywordBlackList = ArrayList<String>()
    var keywordWhiteList = ArrayList<String>()

    private lateinit var appConfigViewModel: AppHaloConfigViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parentLayout = inflater.inflate(R.layout.fragment_setting_data_filtering, container, false) as ViewGroup

        val datafilteringCardView = parentLayout.findViewById<LinearLayout>(R.id.data_filtering_view)

        appConfigViewModel.appHaloConfigLiveData.value?.let{ appHaloConfig ->

            datafilteringCardView.findViewById<MultiSlider>(R.id.maxIndependentNumSeekbar).also{ maxIndependentNotificationSeekBar ->
                maxIndependentNotificationSeekBar.getThumb(0).value = appHaloConfig.maxNumOfIndependentNotifications
                maxIndependentNotificationSeekBar.setOnThumbValueChangeListener{_, _, _, value ->
                    appConfigViewModel.appHaloConfigLiveData.value = appHaloConfig.apply{maxNumOfIndependentNotifications = value}
                }
            }

            datafilteringCardView.findViewById<MultiSlider>(R.id.enhancementSeekbar).also{ enhancementSeekbar ->
                enhancementSeekbar.getThumb(0).thumb = resources.getDrawable(R.drawable.seek_bar_black, null)
                enhancementSeekbar.getThumb(1).thumb = resources.getDrawable(R.drawable.seek_bar_white, null)
                enhancementSeekbar.getThumb(0).value = ((appHaloConfig.filterImportanceConfig[WGBFilterVar.BLACK_COND] as Double) * (filterEnhancmentMax - filterEnhancmentMin)).roundToInt()
                enhancementSeekbar.getThumb(1).value = ((appHaloConfig.filterImportanceConfig[WGBFilterVar.WHITE_COND] as Double) * (filterEnhancmentMax - filterEnhancmentMin)).roundToInt()
                enhancementSeekbar.setOnThumbValueChangeListener{ _, _, thumbIndex, value ->
                    appConfigViewModel.appHaloConfigLiveData.value = appHaloConfig.apply{
                        filterImportanceConfig = if (thumbIndex == 0) {
                            mapOf(
                                    WGBFilterVar.ACTIVE to true,
                                    WGBFilterVar.WHITE_COND to enhancementSeekbar.getThumb(1).value.toDouble() / (filterEnhancmentMax - filterEnhancmentMin),
                                    WGBFilterVar.BLACK_COND to (value.toDouble() / (filterEnhancmentMax - filterEnhancmentMin))
                            )
                        } else {
                            mapOf(
                                    WGBFilterVar.ACTIVE to true,
                                    WGBFilterVar.WHITE_COND to (value.toDouble() / (filterEnhancmentMax - filterEnhancmentMin)),
                                    WGBFilterVar.BLACK_COND to enhancementSeekbar.getThumb(0).value.toDouble() / (filterEnhancmentMax - filterEnhancmentMin)
                            )
                        }
                    }
                }
            }

            datafilteringCardView.findViewById<MultiSlider>(R.id.observationWindowSeekbar).also{ observationWindowSeekbar ->
                val filter = appHaloConfig.filterObservationWindowConfig
                observationWindowSeekbar.getThumb(0).thumb = resources.getDrawable(R.drawable.seek_bar_white, null)
                observationWindowSeekbar.getThumb(1).thumb = resources.getDrawable(R.drawable.seek_bar_black, null)
                observationWindowSeekbar.getThumb(0).value = observationTimeScales.indexOf((filter[WGBFilterVar.WHITE_COND] as Long).toDouble() / observationTimeUnit)
                observationWindowSeekbar.getThumb(1).value = observationTimeScales.indexOf((filter[WGBFilterVar.BLACK_COND] as Long).toDouble() / observationTimeUnit)
                observationWindowSeekbar.setOnThumbValueChangeListener{_, _, thumbIndex, value ->
                    appConfigViewModel.appHaloConfigLiveData.value = appHaloConfig.apply{
                        filterObservationWindowConfig = if(thumbIndex == 0){
                            mapOf(
                                    WGBFilterVar.ACTIVE to true,
                                    WGBFilterVar.WHITE_COND to (observationTimeScales[value] * observationTimeUnit).roundToLong(),
                                    WGBFilterVar.BLACK_COND to (observationTimeScales[observationWindowSeekbar.getThumb(1).value] * observationTimeUnit).roundToLong()
                            )
                        } else{
                            mapOf(
                                    WGBFilterVar.ACTIVE to true,
                                    WGBFilterVar.WHITE_COND to (observationTimeScales[observationWindowSeekbar.getThumb(0).value] * observationTimeUnit).roundToLong(),
                                    WGBFilterVar.BLACK_COND to (observationTimeScales[value] * observationTimeUnit).roundToLong()
                            )
                        }
                    }
                }
            }

            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            val whiteFlowLayout = datafilteringCardView.findViewById<FlowLayout>(R.id.whiteFlowLayout)
            val whiteKeywordTextInput = datafilteringCardView.findViewById<TextInputLayout>(R.id.white_keyword_text_input)
            val whiteKeywordEditText = datafilteringCardView.findViewById<TextInputEditText>(R.id.white_keyword_editText)

            (appHaloConfig.filterKeywordConfig[WGBFilterVar.WHITE_COND] as Set<String>).forEach{whiteKeyword ->
                addKeyword(whiteFlowLayout, whiteKeyword, true)
            }

            whiteKeywordEditText.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    imm.hideSoftInputFromWindow(whiteKeywordEditText.windowToken, 0)
                    addKeyword(whiteFlowLayout, whiteKeywordEditText.text!!.toString(), true)
                    whiteKeywordEditText.setText("")
                    whiteKeywordTextInput.clearFocus()

                    appConfigViewModel.appHaloConfigLiveData.value?.let{
                        it.filterKeywordConfig = mapOf(
                                WGBFilterVar.ACTIVE to true,
                                WGBFilterVar.WHITE_COND to keywordWhiteList.toSet(),
                                WGBFilterVar.BLACK_COND to keywordBlackList.toSet()
                        )
                        appConfigViewModel.appHaloConfigLiveData.value = it
                    }
                }
                false
            }

            val blackFlowLayout = datafilteringCardView.findViewById<FlowLayout>(R.id.blackFlowLayout)
            val blackKeywordTextInput = datafilteringCardView.findViewById<TextInputLayout>(R.id.black_keyword_text_input)
            val blackKeywordEditText = datafilteringCardView.findViewById<TextInputEditText>(R.id.black_keyword_editText)

            (appHaloConfig.filterKeywordConfig[WGBFilterVar.BLACK_COND] as Set<String>).forEach{blackKeyword ->
                addKeyword(blackFlowLayout, blackKeyword, false)
            }

            blackKeywordEditText.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    imm.hideSoftInputFromWindow(blackKeywordEditText.windowToken, 0)
                    addKeyword(blackFlowLayout, blackKeywordEditText.text!!.toString(), false)
                    blackKeywordEditText.setText("")
                    blackKeywordTextInput.clearFocus()

                    appConfigViewModel.appHaloConfigLiveData.value?.let{
                        it.filterKeywordConfig = mapOf(
                                WGBFilterVar.ACTIVE to true,
                                WGBFilterVar.WHITE_COND to keywordWhiteList.toSet(),
                                WGBFilterVar.BLACK_COND to keywordBlackList.toSet()
                        )
                        appConfigViewModel.appHaloConfigLiveData.value = it
                    }
                }
                false
            }
        }

        return parentLayout
    }


    private fun addKeyword(flowLayout: FlowLayout, keyword: String, isWhite: Boolean) {
        if (keyword.isEmpty()) return
        val keywordList: ArrayList<String>
        val backGroundColor: Int
        if (isWhite) {
            keywordList = keywordWhiteList
            backGroundColor = ContextCompat.getColor(context!!, R.color.holo_blue_light)
        } else {
            keywordList = keywordBlackList
            backGroundColor = ContextCompat.getColor(context!!, R.color.holo_red_light)
        }

        if (keywordList.size == 0) {
            flowLayout.visibility = View.VISIBLE
        }
        keywordList.add(keyword)


        val chipView = layoutInflater.inflate(R.layout.chip_view_layout, null) as Chip
        chipView.chipText = keyword
        chipView.changeBackgroundColor(backGroundColor)
        flowLayout.addView(chipView)
        chipView.setOnCloseClickListener {
            chipView.visibility = View.GONE
            keywordList.remove(chipView.chipText)
            if (keywordList.size == 0) {
                (chipView.parent as ViewGroup).visibility = View.GONE
            }
            (chipView.parent as ViewGroup).removeView(chipView)
            appConfigViewModel.appHaloConfigLiveData.value?.let{
                it.filterKeywordConfig = mapOf(
                        WGBFilterVar.ACTIVE to true,
                        WGBFilterVar.WHITE_COND to keywordWhiteList.toSet(),
                        WGBFilterVar.BLACK_COND to keywordBlackList.toSet()
                )
                appConfigViewModel.appHaloConfigLiveData.value = it
            }
        }
    }
}
