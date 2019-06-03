package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

import com.nex3z.flowlayout.FlowLayout
import com.robertlevonyan.views.chip.Chip

import java.util.ArrayList

import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import io.apptik.widget.MultiSlider
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.WGBFilterVar


class DataFilteringFragment : Fragment() {

    // Data Parameters
    val maxTimeWindow: Long = 1000L * 60 * 60 * 24

    var filterEnhancmentMin: Double = 0.0
    var filterEnhancmentMax: Double = 9.0
    var filterObservationWindowMin: Long = 0L
    var filterObservationWindowMax: Long = maxTimeWindow
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

        // Enhancement Setting UI
        val enhancementSeekbar = parentLayout.findViewById<MultiSlider>(R.id.enhancementSeekbar)
        enhancementSeekbar.getThumb(0).value = 2
        enhancementSeekbar.getThumb(1).value = 7
        enhancementSeekbar.setOnThumbValueChangeListener { _, _, thumbIndex, value ->
            if (thumbIndex == 0) {
                filterEnhancmentMin = (value.toDouble() / 9.0)
            } else {
                filterEnhancmentMax = (value.toDouble() / 9.0)
            }

            appConfigViewModel.appHaloConfigLiveData.value?.let{
                it.filterImportanceConfig = mapOf(
                        WGBFilterVar.ACTIVE to true,
                        WGBFilterVar.WHITE_COND to filterEnhancmentMax,
                        WGBFilterVar.BLACK_COND to filterEnhancmentMin
                )
                appConfigViewModel.appHaloConfigLiveData.value = it
            }
        }

        // Observation Setting UI
        val observationWindowSeekbar = parentLayout.findViewById<MultiSlider>(R.id.observationWindowSeekbar)
        observationWindowSeekbar.getThumb(0).value = 2
        observationWindowSeekbar.getThumb(1).value = 7
        observationWindowSeekbar.setOnThumbValueChangeListener { _, _, thumbIndex, value ->

            if (thumbIndex == 0) {
                filterObservationWindowMin = Math.round(value.toDouble() * maxTimeWindow / 9.0)
            } else {
                filterObservationWindowMax = Math.round(value.toDouble() * maxTimeWindow / 9.0)
            }

            appConfigViewModel.appHaloConfigLiveData.value?.let{
                it.filterObservationWindowConfig = mapOf(
                        WGBFilterVar.ACTIVE to true,
                        WGBFilterVar.WHITE_COND to filterObservationWindowMin,
                        WGBFilterVar.BLACK_COND to filterObservationWindowMax
                )
                appConfigViewModel.appHaloConfigLiveData.value = it
            }

        }

        // Keyword Setting UI
        val imm = activity!!
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val whiteFlowLayout = parentLayout.findViewById<FlowLayout>(R.id.whiteFlowLayout)
        val whiteKeywordTextInput = parentLayout.findViewById<TextInputLayout>(R.id.white_keyword_text_input)
        val whiteKeywordEditText = parentLayout.findViewById<TextInputEditText>(R.id.white_keyword_editText)
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

        val blackFlowLayout = parentLayout.findViewById<FlowLayout>(R.id.blackFlowLayout)
        val blackKeywordTextInput = parentLayout.findViewById<TextInputLayout>(R.id.black_keyword_text_input)
        val blackKeywordEditText = parentLayout.findViewById<TextInputEditText>(R.id.black_keyword_editText)
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
        }
    }
}
