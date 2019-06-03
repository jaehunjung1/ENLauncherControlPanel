package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup

import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import kotlinx.android.synthetic.main.activity_controlpanel.*
import kr.ac.snu.hcil.datahalo.haloview.AppNotificationHalo
import kr.ac.snu.hcil.datahalo.manager.DataHaloManager
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig

class ControlPanelActivity : FragmentActivity() {

    companion object{
        private const val NUM_PAGES = 2
        private const val previewPackageName = "kr.ac.snu.hcil.datahalo.preview"
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var previewHalo: AppNotificationHalo

    private inner class ScreenSlidPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm){
        private val fragments: List<Fragment> = listOf(
                DataFilteringFragment(),
                VisualSpecFragment()
        )
        override fun getCount(): Int = NUM_PAGES
        override fun getItem(p0: Int): Fragment = fragments[p0]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controlpanel)

        settingPager.adapter = ScreenSlidPagerAdapter(supportFragmentManager)
        previewHalo = DataHaloManager.createAppHalo(this, previewPackageName)
        preview_frameLayout.addView(previewHalo, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        appConfigViewModel = ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        val appConfigObserver = Observer<AppHaloConfig>{ newConfig ->
            newConfig?.let{
                previewHalo.setVisConfig(it)
                previewHalo.setAppHaloData(EnhancedAppNotifications(previewPackageName).also{notifications ->
                    notifications.notificationData = mutableListOf()
                })
            }
        }
        appConfigViewModel.appHaloConfigLiveData.observe(this, appConfigObserver)

    }

    override fun onBackPressed() {
        if(settingPager.currentItem == 0){ super.onBackPressed()}
        else{ settingPager.currentItem = settingPager.currentItem - 1}
    }
}
