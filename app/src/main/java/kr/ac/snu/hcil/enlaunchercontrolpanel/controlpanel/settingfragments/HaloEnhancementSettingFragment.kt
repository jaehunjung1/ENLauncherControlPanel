package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.lifecycle.ViewModelProviders
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.ImportanceControlView
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.KeywordGroupExpandableListAdapter

class HaloEnhancementSettingFragment: androidx.fragment.app.Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = HaloEnhancementSettingFragment()
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
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