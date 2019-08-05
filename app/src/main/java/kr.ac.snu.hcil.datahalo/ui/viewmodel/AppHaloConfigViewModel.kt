package kr.ac.snu.hcil.datahalo.ui.viewmodel

import android.animation.AnimatorSet
import androidx.lifecycle.*
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.view.View
import android.view.animation.LinearInterpolator
import kr.ac.snu.hcil.datahalo.manager.AppConfigManager
import kr.ac.snu.hcil.datahalo.manager.AppHaloLayoutMethods
import kr.ac.snu.hcil.datahalo.manager.VisEffectManager
import kr.ac.snu.hcil.datahalo.visconfig.*
import kr.ac.snu.hcil.datahalo.visualEffects.AggregatedVisMappingRule
import kr.ac.snu.hcil.datahalo.visualEffects.NewVisShape
import kr.ac.snu.hcil.datahalo.visualEffects.VisObjectShape

class AppHaloConfigViewModel: ViewModel() {
    companion object{
        const val TAG = "APP_HALO_CONFIG_VIEWMODEL"
        const val SAMPLE_PACKAGE_NAME = "kr.ac.snu.hcil.datahalo.sample"
    }

    val appHaloConfigLiveData: MutableLiveData<AppHaloConfig> = MutableLiveData()

    init{
        appHaloConfigLiveData.value = AppConfigManager.retrieveDefaultAppConfig(SAMPLE_PACKAGE_NAME)
    }
}