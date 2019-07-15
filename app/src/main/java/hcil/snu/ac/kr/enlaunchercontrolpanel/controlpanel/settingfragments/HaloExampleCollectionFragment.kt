package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel.settingfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.ComponentExampleSelectionView
import hcil.snu.ac.kr.enlaunchercontrolpanel.examplecomponentselection.HaloVisComponent
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel

class HaloExampleCollectionFragment: Fragment() {

    companion object{
        @JvmStatic
        fun newInstance() = HaloExampleCollectionFragment()
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel

    private var importancePatternSelector: ComponentExampleSelectionView? = null
    private var layoutMethodSelector: ComponentExampleSelectionView? = null
    private var independentEffectSelector: ComponentExampleSelectionView? = null
    private var aggregatedEffectSelector: ComponentExampleSelectionView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_halo_example_collections, container, false).also{

            importancePatternSelector = it.findViewById<ComponentExampleSelectionView>(R.id.importance_pattern_examples).apply{
                setViewModel(appConfigViewModel)
                exampleDataList = VisDataManager.exampleImportanceSaturationPatterns.keys.toList().map{ component ->
                    HaloVisComponent(component, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.IMPORTANCE_SATURATION )
                }
                loadSelection(savedInstanceState)
            }
            layoutMethodSelector = it.findViewById<ComponentExampleSelectionView>(R.id.layout_examples).apply{
                setViewModel(appConfigViewModel)
                exampleDataList = AppHaloLayoutMethods.availiableLayouts.map{ component ->
                    HaloVisComponent(component, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.VISEFFECT_LAYOUT )
                }
                loadSelection(savedInstanceState)
            }
            independentEffectSelector = it.findViewById<ComponentExampleSelectionView>(R.id.indpendent_effect_examples).apply{
                setViewModel(appConfigViewModel)
                exampleDataList = VisEffectManager.availableIndependentVisEffects.map{ component ->
                    HaloVisComponent(component, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT )
                }
                loadSelection(savedInstanceState)
            }
            aggregatedEffectSelector = it.findViewById<ComponentExampleSelectionView>(R.id.aggregated_effect_examples).apply{
                setViewModel(appConfigViewModel)
                exampleDataList = VisEffectManager.availableAggregatedVisEffects.map{ component ->
                    HaloVisComponent(component, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.AGGREGATED_VISEFFECT)
                }
                loadSelection(savedInstanceState)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        importancePatternSelector?.saveSelection(outState)
        layoutMethodSelector?.saveSelection(outState)
        independentEffectSelector?.saveSelection(outState)
        aggregatedEffectSelector?.saveSelection(outState)
    }
}