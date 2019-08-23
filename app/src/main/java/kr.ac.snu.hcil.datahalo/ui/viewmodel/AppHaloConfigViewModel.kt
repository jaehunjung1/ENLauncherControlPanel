package kr.ac.snu.hcil.datahalo.ui.viewmodel

import androidx.lifecycle.*
import kr.ac.snu.hcil.datahalo.manager.AppConfigManager
import kr.ac.snu.hcil.datahalo.visconfig.*

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