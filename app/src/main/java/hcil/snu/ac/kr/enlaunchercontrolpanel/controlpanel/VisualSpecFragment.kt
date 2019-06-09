package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.alespero.expandablecardview.ExpandableCardView

import hcil.snu.ac.kr.enlaunchercontrolpanel.AggregatedMappingLayout
import hcil.snu.ac.kr.enlaunchercontrolpanel.IndependentMappingLayout
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponentAdapter
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponent
import hcil.snu.ac.kr.enlaunchercontrolpanel.viewmodel.MappingContainer
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

class VisualSpecFragment : Fragment() {

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var haloLayoutMethodVisComponents: List<HaloVisComponent>
    private lateinit var haloIndependentEffectVisComponents: List<HaloVisComponent>
    private lateinit var haloAggregatedEffectVisComponents:List<HaloVisComponent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        haloLayoutMethodVisComponents = AppHaloLayoutMethods.availiableLayouts.map{
            HaloVisComponent(it, R.drawable.kakaotalk_logo)
        }
        haloIndependentEffectVisComponents = VisEffectManager.availableIndependentVisEffects.map{
            HaloVisComponent(it, R.drawable.kakaotalk_logo)
        }
        haloAggregatedEffectVisComponents = VisEffectManager.availableAggregatedVisEffects.map{
            HaloVisComponent(it, R.drawable.kakaotalk_logo)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parentLayout = inflater.inflate(R.layout.fragment_setting_visual_spec,
                container, false) as ViewGroup

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
        for (i in MappingContainer.visVarStringList.indices) {
            indepVisInnerView.addView(IndependentMappingLayout(context, MappingContainer.visVarStringList[i]))
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

        return parentLayout
    }

}// Required empty public constructor