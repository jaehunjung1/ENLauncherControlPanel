package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.ComponentExampleSelectionView
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.HaloVisComponent
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

class HaloLayoutFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = HaloLayoutFragment()
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var visComponents: List<HaloVisComponent>
    private var componentExamples = AppHaloLayoutMethods.availiableLayouts.map{
        HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT)
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
        return inflater.inflate(R.layout.fragment_halo_layout, container, false).also{
            componentExampleSelector = it.findViewById<ComponentExampleSelectionView>(R.id.exampleSelectionView).apply{
                exampleDataList = componentExamples
                loadSelection(savedInstanceState)
            }
        }
    }


}
