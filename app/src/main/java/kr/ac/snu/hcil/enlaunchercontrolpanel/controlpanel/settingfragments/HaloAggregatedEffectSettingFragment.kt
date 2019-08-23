package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.theartofdev.edmodo.cropper.CropImage

import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping.AggregatedMappingExpandableListAdapter
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection.ComponentExampleSelectionView
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection.HaloVisComponent
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape
import kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType

class HaloAggregatedEffectSettingFragment : Fragment() {

    companion object {
        private const val TAG = "HaloAggrEffSet"
        @JvmStatic
        fun newInstance() = HaloAggregatedEffectSettingFragment()
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private var componentExamples = VisEffectManager.availableAggregatedVisEffects.map{
        HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.AGGREGATED_VISEFFECT)
    }

    private var componentExampleSelector: ComponentExampleSelectionView? = null
    private var expandableListView: ExpandableListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appConfigViewModel = activity?.run {
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val appConfigObserver = Observer<AppHaloConfig> { newConfig ->
            componentExampleSelector?.setViewModel(appConfigViewModel)
        }
        appConfigViewModel.appHaloConfigLiveData.observe(this, appConfigObserver)
    }

    private var currentShapeChangedIndex: Int? = null
    private var currentShapeChangedUri: Uri? = null
    private var currentShapeChangedType: VisShapeType? = null


    private fun updateShapedChanged(){
        currentShapeChangedType?.let{ type ->
            when(type){
                VisShapeType.OVAL -> {
                    VisObjectShape(type, null)
                }
                VisShapeType.RECT -> {
                    VisObjectShape(type, null)
                }
                VisShapeType.PATH -> {
                    VisObjectShape(type, null)
                }
                VisShapeType.TEXT -> {
                    VisObjectShape(type, null)
                }
                VisShapeType.IMAGE -> {
                    if(currentShapeChangedIndex != null && currentShapeChangedUri != null){
                        VisObjectShape(
                                VisShapeType.IMAGE,
                                BitmapDrawable(resources, MediaStore.Images.Media.getBitmap(activity!!.contentResolver, currentShapeChangedUri!!))
                        )
                    } else null
                }
            }
        }?.let{ newVisObjShape ->
            appConfigViewModel.appHaloConfigLiveData.value?.let{ currentConfig ->
                currentConfig.aggregatedVisualParameters[0].selectedShapeList =
                        currentConfig.aggregatedVisualParameters[0].selectedShapeList.mapIndexed{ index, visObjShape ->
                            if(index == currentShapeChangedIndex!!){
                                newVisObjShape
                            }
                            else
                                visObjShape
                        }

                appConfigViewModel.appHaloConfigLiveData.value = currentConfig
                currentShapeChangedIndex = null
                currentShapeChangedUri = null

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            when(resultCode){
                RESULT_OK -> {
                    currentShapeChangedUri = result.uri
                    updateShapedChanged()
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    Log.e(TAG, "Crop_Image_Activity_Failed")
                    throw result.error
                }
                else -> {}
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
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
                exampleDataList = componentExamples
                setViewModel(appConfigViewModel)
                loadSelection(savedInstanceState)
            }

            val groupPropertySpinner = it.findViewById<Spinner>(R.id.group_property_spinner)
            val binNumberPicker = it.findViewById<NumberPicker>(R.id.bin_number_picker)

            groupPropertySpinner.let{ spinner ->
                spinner.adapter = ArrayAdapter(
                        context!!,
                        R.layout.item_spinner,
                        NotiProperty.values().map{prop -> prop.name}.toMutableList().apply{
                            add(0, "None")
                        }.toList()
                )

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val notiProp: NotiProperty? = if(position == 0){ null } else{ NotiProperty.values()[position - 1]}

                        when(notiProp){
                            NotiProperty.IMPORTANCE ->{
                                binNumberPicker.visibility = View.VISIBLE
                            }
                            else -> {
                                binNumberPicker.visibility = View.INVISIBLE
                            }
                        }

                        appConfigViewModel.appHaloConfigLiveData.value?.let{ config ->
                            config.aggregatedVisualMappings[0].groupProperty = notiProp
                            appConfigViewModel.appHaloConfigLiveData.value = config
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) { }
                }
            }

            binNumberPicker.let{numPicker ->
                numPicker.minValue = 2
                numPicker.maxValue = 5
                numPicker.value = appConfigViewModel.appHaloConfigLiveData.value?.let{ config ->
                    config.aggregatedDataParameters[0].binNums
                } ?: numPicker.minValue

                numPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    if(oldVal != newVal){
                        appConfigViewModel.appHaloConfigLiveData.value?.let{ config ->
                            config.aggregatedDataParameters[0].binNums = newVal
                            appConfigViewModel.appHaloConfigLiveData.value = config
                        }
                    }
                }

            }

            expandableListView = it.findViewById<ExpandableListView>(R.id.expandable_mapping_list).apply{
                setAdapter(
                        AggregatedMappingExpandableListAdapter().apply{
                            setViewModel(appConfigViewModel)
                            shapeMappingParameterChangedListener = object: AggregatedMappingExpandableListAdapter.MappingParameterChangedListener{
                                override fun onShapeParameterChanged(index: Int, visShapeType: VisShapeType) {
                                    currentShapeChangedIndex = index
                                    currentShapeChangedType = visShapeType
                                    if(visShapeType != VisShapeType.IMAGE)
                                        updateShapedChanged()
                                }
                            }
                        }
                )

                setOnGroupClickListener{parent, view, groupPos, id ->
                    Toast.makeText(context, "c click = $groupPos", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }

}
