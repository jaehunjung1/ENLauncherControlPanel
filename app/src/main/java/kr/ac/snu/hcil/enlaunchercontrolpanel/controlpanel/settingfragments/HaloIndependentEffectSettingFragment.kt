package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.PathShape
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.theartofdev.edmodo.cropper.CropImage

import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping.IndependentMappingExpandableListAdapter
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection.ComponentExampleSelectionView
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.presetselection.HaloVisComponent

import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.utils.TextDrawable
import kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

class HaloIndependentEffectSettingFragment : Fragment() {

    companion object {
        private const val TAG = "HaloIndeEffSet"
        @JvmStatic
        fun newInstance() = HaloIndependentEffectSettingFragment()
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private var componentExamples = VisEffectManager.availableIndependentVisEffects.map{
        HaloVisComponent(it, R.drawable.kakaotalk_logo, HaloVisComponent.HaloVisComponentType.INDEPENDENT_VISEFFECT)
    }

    private var componentExampleSelector: ComponentExampleSelectionView? = null
    private var expandableListView: ExpandableListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appConfigViewModel = activity?.run {
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

    }

    private var currentShapeChangedIndex: Int? = null
    private var currentShapeChangedUri: Uri? = null
    private var currentShapeChangedType: VisShapeType? = null

    private fun updateShapeChanged(){

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
                currentConfig.independentVisualParameters[0].selectedShapeList =
                        currentConfig.independentVisualParameters[0].selectedShapeList.mapIndexed{ index, visObjShape ->
                            if(index == currentShapeChangedIndex!!){
                                newVisObjShape
                            }
                            else
                                visObjShape
                        }

                appConfigViewModel.appHaloConfigLiveData.value = currentConfig
                currentShapeChangedIndex = null
                currentShapeChangedUri = null

                /*
                expandableListView?.expandableListAdapter?.let{
                    (it as IndependentMappingExpandableListAdapter).notifyDataSetChanged()
                }
                */
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            when(resultCode){
                RESULT_OK -> {
                    currentShapeChangedUri = result.uri
                    updateShapeChanged()
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    Log.e(TAG, "Crop_Image_Activity_Failed")
                    throw result.error
                }
                else -> {}
            }
        }
        else{
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
        return inflater.inflate(R.layout.fragment_halo_independent_effect_setting, container, false).also{

            componentExampleSelector = it.findViewById<ComponentExampleSelectionView>(R.id.exampleSelectionView).apply{
                exampleDataList = componentExamples
                setViewModel(appConfigViewModel)
                loadSelection(savedInstanceState)
            }

            expandableListView = it.findViewById<ExpandableListView>(R.id.expandable_mapping_list).apply{
                setAdapter(
                        IndependentMappingExpandableListAdapter().apply{
                            setViewModel(appConfigViewModel)
                            shapeMappingParameterChangedLister = object: IndependentMappingExpandableListAdapter.MappingParameterChangedListener{
                                override fun onShapeParameterChanged(index: Int, visShapeType: VisShapeType) {
                                    currentShapeChangedIndex = index
                                    currentShapeChangedType = visShapeType
                                    if(visShapeType != VisShapeType.IMAGE)
                                        updateShapeChanged()
                                }
                            }
                        }
                )

                setOnGroupClickListener{ parent, view, groupPos, id ->
                    Toast.makeText(context, "c click = $groupPos", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }
}
