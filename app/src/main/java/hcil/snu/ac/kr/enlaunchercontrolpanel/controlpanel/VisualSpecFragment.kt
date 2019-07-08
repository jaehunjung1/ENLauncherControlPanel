package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.lifecycle.Observer
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.HaloVisComponent
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import android.widget.Toast

class VisualSpecFragment : androidx.fragment.app.Fragment() {

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var haloLayoutMethodVisComponents: List<HaloVisComponent>
    private lateinit var haloIndependentEffectVisComponents: List<HaloVisComponent>
    private lateinit var haloAggregatedEffectVisComponents:List<HaloVisComponent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        haloLayoutMethodVisComponents = AppHaloLayoutMethods.availiableLayouts.map{
            HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.VISEFFECT_LAYOUT)
        }
        haloIndependentEffectVisComponents = VisEffectManager.availableIndependentVisEffects.map{
            HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT)
        }
        haloAggregatedEffectVisComponents = VisEffectManager.availableAggregatedVisEffects.map{
            HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.AGGREGATED_VISEFFECT)
        }

        appConfigViewModel = activity?.run {
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        appConfigViewModel.appHaloConfigLiveData.observe(this, Observer<AppHaloConfig>{ config ->

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parentLayout = inflater.inflate(R.layout.fragment_setting_visual_spec,
                container, false) as ViewGroup

        /*
        val haloLayoutCardView = parentLayout.findViewById<ExpandableCardView>(R.id.halo_layout_card_view)
        val haloLayoutRecyclerView = haloLayoutCardView.findViewById<RecyclerView>(R.id.halo_layout_recyclerview)
        val haloLayoutAdapter = HaloVisComponentAdapter(context!!, haloLayoutMethodVisComponents)
        haloLayoutRecyclerView.adapter = haloLayoutAdapter
        haloLayoutRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        val indepVisCardView = parentLayout.findViewById<ExpandableCardView>(R.id.independent_vis_cardview)
        val indepVisRecyclerView = indepVisCardView.findViewById<RecyclerView>(R.id.halo_layout_recyclerview)
        // TODO change this to independent vis adapter / indep vis ArrayList
        val indepVisAdapter = HaloVisComponentAdapter(context!!, haloIndependentEffectVisComponents)
        indepVisRecyclerView.adapter = indepVisAdapter
        indepVisRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val indepVisInnerView = indepVisCardView.findViewById<LinearLayout>(R.id.innerView)


        NuNotiVisVariable.values().forEach{ visVar ->
            indepVisInnerView.addView(
                    IndependentMappingLayout(context!!, visVar, 0, appConfigViewModel)
            )
        }

        val aggregatedVisCardView = parentLayout.findViewById<ExpandableCardView>(R.id.aggregated_vis_cardview)
        val aggregatedVisRecyclerview = aggregatedVisCardView.findViewById<RecyclerView>(R.id.halo_layout_recyclerview)
        // TODO change this to aggregated vis adatper / aggregated vis arraylist
        val aggregatedVisAdapter = HaloVisComponentAdapter(context!!, haloAggregatedEffectVisComponents)
        aggregatedVisRecyclerview.adapter = aggregatedVisAdapter
        aggregatedVisRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val aggregateVisInnerView = aggregatedVisCardView.findViewById<LinearLayout>(R.id.innerView)
        for (i in MappingContainer.visVarStringList.indices) {
            aggregateVisInnerView.addView(AggregatedMappingLayout(context, MappingContainer.visVarStringList[i]))
        }

        */
        val testExpandableListView = parentLayout.findViewById<ExpandableListView>(R.id.text_expandable_list_view)
        testExpandableListView.setAdapter(
                MappingExpandableListAdapter().apply{
                    setViewModel(appConfigViewModel)
                }
        )

        testExpandableListView.setOnGroupClickListener{parent, view, groupPos, id ->
            Toast.makeText(context, "c click = $groupPos", Toast.LENGTH_SHORT).show()
            false
        }
        /*
        testExpandableListView.setOnChildClickListener{parent, view, groupPos, id ->

        }
        testExpandableListView.setOnGroupCollapseListener {

        }
        testExpandableListView.setOnGroupExpandListener {

        }
        */

        return parentLayout
    }

}// Required empty public constructor