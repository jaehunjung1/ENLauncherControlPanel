package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_halo_enhancement_setting.*
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.ImportanceControlView
import kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.KeywordGroupExpandableListAdapter

class HaloEnhancementSettingFragment: androidx.fragment.app.Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = HaloEnhancementSettingFragment()
    }

    private lateinit var appConfigViewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")


        val appConfigObserver = Observer<kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig> { newConfig ->
            (expandable_importance_pattern_mapping.expandableListAdapter as KeywordGroupExpandableListAdapter).setAppConfig(newConfig)
        }
        appConfigViewModel.appHaloConfigLiveData.observe(this, appConfigObserver)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_halo_enhancement_setting, container, false).also{ view ->
            view.findViewById<ExpandableListView>(R.id.expandable_importance_pattern_mapping).apply{
                setAdapter(
                        KeywordGroupExpandableListAdapter(appConfigViewModel)
                )
            }
        }
    }
}