package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView

import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import kotlinx.android.synthetic.main.activity_controlpanel.*
import kr.ac.snu.hcil.datahalo.haloview.AppNotificationHalo
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.notificationdata.NotiContent
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig

class ControlPanelActivity : androidx.fragment.app.FragmentActivity() {

    companion object{
        private const val TAG = "HALO_SETTING_ACTIVITY"
        private const val NUM_PAGES = 2
        private val exampleNotifications = listOf(
                EnhancedNotification(25123, "", System.currentTimeMillis(), 1000L * 60 * 60).also{
                    it.currEnhancement = 0.3
                    it.lifeCycle = EnhancedNotificationLife.STATE_2_TRIGGERED_NOT_INTERACTED
                    it.notiContent = NotiContent("한구현", "집에 갑시다.")
                },
                EnhancedNotification(34532, "", System.currentTimeMillis(), 1000L * 60 * 60).also{
                    it.currEnhancement = 0.8
                    it.lifeCycle = EnhancedNotificationLife.STATE_4_INTERACTED_NOT_DECAYED
                    it.notiContent = NotiContent("정재훈", "집에 갑시다.")
                },
                EnhancedNotification(54634, "", System.currentTimeMillis(), 1000L * 60 * 60).also{
                    it.currEnhancement = 0.2
                    it.lifeCycle = EnhancedNotificationLife.STATE_5_DECAYING
                    it.notiContent = NotiContent("안단태", "집에 갑시다.")
                }
        )
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var previewHalo: AppNotificationHalo

    private inner class ScreenSlidPagerAdapter(fm: androidx.fragment.app.FragmentManager): androidx.fragment.app.FragmentStatePagerAdapter(fm){
        private val fragments: List<androidx.fragment.app.Fragment> = listOf(
                DataFilteringFragment(),
                VisualSpecFragment()
        )
        override fun getCount(): Int = NUM_PAGES
        override fun getItem(p0: Int): androidx.fragment.app.Fragment = fragments[p0]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controlpanel)

        settingPager.adapter = ScreenSlidPagerAdapter(supportFragmentManager)

        previewHalo = AppNotificationHalo(context = this, attributeSet = null).also{
            /*
            it.setVisConfig(AppHaloConfig(previewPackageName))
            it.setAppHaloData(EnhancedAppNotifications(previewPackageName).also{notifications ->
                notifications.notificationData = exampleNotifications.toMutableList()
            })
            */
        }

        preview_frameLayout.addView(previewHalo, FrameLayout.LayoutParams(700, 700, Gravity.CENTER))

        preview_frameLayout.addView(
                ImageView(this).also{ it.setImageDrawable(getDrawable(R.drawable.kakaotalk_logo))},
                FrameLayout.LayoutParams(250, 250, Gravity.CENTER)
        )

        appConfigViewModel = ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        val appConfigObserver = Observer<AppHaloConfig>{ newConfig ->
            Log.d(TAG, "Model Updated")
            newConfig?.let{
                previewHalo.setVisConfig(it)
                previewHalo.setAppHaloData(EnhancedAppNotifications(AppHaloConfigViewModel.SAMPLE_PACKAGE_NAME).also{notifications ->
                    notifications.notificationData = exampleNotifications.toMutableList()
                })
                preview_frameLayout.invalidate()
            }
        }
        appConfigViewModel.appHaloConfigLiveData.observe(this, appConfigObserver)

    }

    override fun onBackPressed() {
        if(settingPager.currentItem == 0){ super.onBackPressed()}
        else{ settingPager.currentItem = settingPager.currentItem - 1}
    }
}
