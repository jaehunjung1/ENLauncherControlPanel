package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.ComponentExampleSelectionView
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.HaloVisComponent
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.HaloVisComponentAdapter

import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig

class HaloIndependentEffectSettingFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = HaloIndependentEffectSettingFragment()
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var visComponents: List<HaloVisComponent>
    private var componentExamples = VisEffectManager.availableIndependentVisEffects.map{
        HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT)
    }
    private var componentExampleSelector: ComponentExampleSelectionView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        visComponents = VisEffectManager.availableIndependentVisEffects.map{
            HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT)
        }

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
        return inflater.inflate(R.layout.fragment_halo_independent_effect_setting, container, false).also{

            componentExampleSelector = it.findViewById<ComponentExampleSelectionView>(R.id.exampleSelectionView).apply{
                exampleDataList = componentExamples
                loadSelection(savedInstanceState)
            }

            val testExpandableListView = it.findViewById<ExpandableListView>(R.id.expandable_mapping_list)
            testExpandableListView.setAdapter(
                    MappingExpandableListAdapter().apply{
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
