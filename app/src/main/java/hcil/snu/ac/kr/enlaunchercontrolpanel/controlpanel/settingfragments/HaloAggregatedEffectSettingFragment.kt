package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel.settingfragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders

import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel.componentviews.AggregatedMappingExpandableListAdapter
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.ComponentExampleSelectionView
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.HaloVisComponent
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

class HaloAggregatedEffectSettingFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = HaloAggregatedEffectSettingFragment()
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private var componentExamples = VisEffectManager.availableAggregatedVisEffects.map{
        HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.AGGREGATED_VISEFFECT)
    }
    private var componentExampleSelector: ComponentExampleSelectionView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appConfigViewModel = activity?.run {
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        componentExampleSelector?.saveSelection(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_halo_aggregated_effect_setting, container, false).also{
            componentExampleSelector = it.findViewById<ComponentExampleSelectionView>(R.id.exampleSelectionView).apply{
                setViewModel(appConfigViewModel)
                exampleDataList = componentExamples
                loadSelection(savedInstanceState)
            }

            val testExpandableListView = it.findViewById<ExpandableListView>(R.id.expandable_mapping_list)
            testExpandableListView.setAdapter(
                    AggregatedMappingExpandableListAdapter().apply{
                        setViewModel(appConfigViewModel)
                    }
            )

            testExpandableListView.setOnGroupClickListener{parent, view, groupPos, id ->
                Toast.makeText(context, "c click = $groupPos", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

}
