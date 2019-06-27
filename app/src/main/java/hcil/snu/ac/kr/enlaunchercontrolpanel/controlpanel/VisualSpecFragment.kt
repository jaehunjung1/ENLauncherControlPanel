package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponentAdapter
import hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel.HaloVisComponent
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

class VisualSpecFragment : Fragment() {

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var haloLayoutMethodVisComponents: List<HaloVisComponent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        haloLayoutMethodVisComponents = AppHaloLayoutMethods.availiableLayouts.map{
            HaloVisComponent(it, R.drawable.kakaotalk_logo)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parentLayout = inflater.inflate(R.layout.fragment_setting_visual_spec,
                container, false) as ViewGroup

        val haloLayoutRecyclerView = parentLayout.findViewById<RecyclerView>(R.id.halo_layout_recyclerview)
        val haloLayoutAdapter = HaloVisComponentAdapter(context!!, haloLayoutMethodVisComponents)
        haloLayoutRecyclerView.adapter = haloLayoutAdapter
        haloLayoutRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        return parentLayout
    }

}