package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection.ComponentExampleSelectionView
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection.HaloVisComponent
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

                exampleDataList = VisDataManager.exampleImportanceSaturationPatterns.keys.toList().map{ component ->
                    HaloVisComponent(component, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.IMPORTANCE_SATURATION )
                }
                setViewModel(appConfigViewModel)
                loadSelection(savedInstanceState)
            }
            layoutMethodSelector = it.findViewById<ComponentExampleSelectionView>(R.id.layout_examples).apply{

                exampleDataList = AppHaloLayoutMethods.availiableLayouts.map{ component ->
                    HaloVisComponent(component, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.VISEFFECT_LAYOUT )
                }
                setViewModel(appConfigViewModel)
                loadSelection(savedInstanceState)
            }
            independentEffectSelector = it.findViewById<ComponentExampleSelectionView>(R.id.indpendent_effect_examples).apply{

                exampleDataList = VisEffectManager.availableIndependentVisEffects.map{ component ->
                    HaloVisComponent(component, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT )
                }
                setViewModel(appConfigViewModel)
                loadSelection(savedInstanceState)
            }
            aggregatedEffectSelector = it.findViewById<ComponentExampleSelectionView>(R.id.aggregated_effect_examples).apply{

                exampleDataList = VisEffectManager.availableAggregatedVisEffects.map{ component ->
                    HaloVisComponent(component, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.AGGREGATED_VISEFFECT)
                }
                setViewModel(appConfigViewModel)
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