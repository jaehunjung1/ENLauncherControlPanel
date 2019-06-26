package kr.ac.snu.hcil.datahalo.manager

import android.content.Context
import kr.ac.snu.hcil.datahalo.haloview.AppNotificationHalo
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications

class DataHaloManager {
    companion object{

        //Application별로 어떻게 전환할 지에 대한 설명서
        private fun exceptionRedundantAddition(packageName:String) = "Halo Already Exists For: $packageName"
        private fun exceptionNotExist(packageName: String) = "Halo Does Not Exist For: $packageName"

        private val appNotificationHalos: MutableMap<String, AppNotificationHalo> = mutableMapOf()

        fun createAppHalo(context: Context,
                          config:AppHaloConfig)
                : AppNotificationHalo{
            if(config.packageName in appNotificationHalos.keys)
                exceptionRedundantAddition(config.packageName)

            val newAppHalo = AppNotificationHalo(context, null).also{
                it.setVisConfig(config)
            }
            appNotificationHalos[config.packageName] = newAppHalo

            return newAppHalo
        }

        fun createAppHalo(
                context: Context,
                config: AppHaloConfig,
                enhancedData: EnhancedAppNotifications
        ): AppNotificationHalo
        {
            return createAppHalo(context, config).also{
                it.setVisConfig(config)
                it.setAppHaloData(enhancedData)
            }
        }

        fun updateAppHalo(
                packageName: String,
                enhancedData: EnhancedAppNotifications
        ){
            if(packageName !in appNotificationHalos.keys)
                exceptionNotExist(packageName)

            val currAppHalo: AppNotificationHalo = appNotificationHalos[packageName]!!
            currAppHalo.setAppHaloData(enhancedData)
        }
    }
}