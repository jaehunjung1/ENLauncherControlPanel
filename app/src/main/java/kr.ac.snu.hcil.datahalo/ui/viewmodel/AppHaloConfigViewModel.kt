package kr.ac.snu.hcil.datahalo.ui.viewmodel

import android.arch.lifecycle.*
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig

class AppHaloConfigViewModel: ViewModel() {
    companion object{
        const val TAG = "APP_HALO_CONFIG_VIEWMODEL"
        const val SAMPLE_PACKAGE_NAME = "kr.ac.snu.hcil.datahalo.sample"
    }

    val appHaloConfigLiveData: MutableLiveData<AppHaloConfig> = MutableLiveData()

    init{
        appHaloConfigLiveData.value = AppHaloConfig(SAMPLE_PACKAGE_NAME)
    }

    /*
    fun getAppHaloConfig() = appHaloConfigLiveData.value
    fun setAppHaloConfig(config: AppHaloConfig){
        appHaloConfigLiveData.value = config
    }

    fun retrieveAppHalo(context: Context):AppNotificationHalo?{
        return getAppHaloConfig()?.let{
            DataHaloManager.createAppHalo(context, SAMPLE_PACKAGE_NAME, it)
        }
    }
    */
}