package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.alespero.expandablecardview.ExpandableCardView
import hcil.snu.ac.kr.enlaunchercontrolpanel.IndependentMappingLayout
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponent
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponentAdapter
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.NuNotiVisVariable

class IndepSpecFragment : Fragment() {


    // todo need to integrate viewModel with visualSpecFragment
    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var haloIndependentEffectVisComponents: List<HaloVisComponent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run {
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        haloIndependentEffectVisComponents = VisEffectManager.availableIndependentVisEffects.map {
            HaloVisComponent(it, R.drawable.kakaotalk_logo)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parentLayout = inflater.inflate(R.layout.fragment_setting_indep_spec,
                container, false) as ViewGroup

        val indepVisRecyclerView = parentLayout.findViewById<RecyclerView>(R.id.halo_layout_recyclerview)
        // TODO change this to independent vis adapter / indep vis ArrayList
        val indepVisAdapter = HaloVisComponentAdapter(context!!, haloIndependentEffectVisComponents)
        indepVisRecyclerView.adapter = indepVisAdapter
        indepVisRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val indepSpecLinearLayout = parentLayout.findViewById<LinearLayout>(R.id.indep_spec_linearlayout)


        NuNotiVisVariable.values().forEach { visVar ->
            indepSpecLinearLayout.addView(
                    IndependentMappingLayout(context!!, visVar, 0, appConfigViewModel)
            )
        }

        return parentLayout
    }
}