package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.componentviews.ImportanceControlView
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

class HaloEnhancementSettingFragment: androidx.fragment.app.Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = HaloEnhancementSettingFragment()
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private var controlView: ImportanceControlView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        controlView?.save(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_halo_enhancement_setting, container, false).also{ view ->

            controlView = view.findViewById<ImportanceControlView>(R.id.enhancement_control_view).apply{
                setViewModel(appConfigViewModel)
                load(savedInstanceState)
            }
        }
    }
}