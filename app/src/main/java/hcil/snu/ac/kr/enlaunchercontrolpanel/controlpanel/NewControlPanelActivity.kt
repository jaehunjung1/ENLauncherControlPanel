package hcil.snu.ac.kr.enlaunchercontrolpanel.controlpanel

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.utilities.Utilities
import kotlinx.android.synthetic.main.activity_new_control_panel.*
import kotlinx.android.synthetic.main.activity_new_control_panel.preview_layout
import kotlinx.android.synthetic.main.activity_new_control_panel.view.*
import kr.ac.snu.hcil.datahalo.haloview.AppNotificationHalo
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.notificationdata.NotiContent
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig

class NewControlPanelActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "HALO_SETTING_ACTIVITY"
        private val exampleNotifications = listOf(
                EnhancedNotification(25123, "", System.currentTimeMillis(), 1000L * 60 * 60).also{
                    it.currEnhancement = 0.3
                    it.lifeCycle = EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED
                    it.notiContent = NotiContent("한구현", "집에 갑시다.")
                },
                EnhancedNotification(34532, "", System.currentTimeMillis(), 1000L * 60 * 60).also{
                    it.currEnhancement = 0.8
                    it.lifeCycle = EnhancedNotificationLife.INTERACTED_NOT_DECAYING
                    it.notiContent = NotiContent("정재훈", "집에 갑시다.")
                },
                EnhancedNotification(54634, "", System.currentTimeMillis(), 1000L * 60 * 60).also{
                    it.currEnhancement = 0.2
                    it.lifeCycle = EnhancedNotificationLife.DECAYING
                    it.notiContent = NotiContent("안단태", "집에 갑시다.")
                }
        )
    }

    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var previewHalo: AppNotificationHalo

    private inner class ScreenSlidPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm){
        val fragments: MutableMap<String, Fragment> = mutableMapOf()
        override fun getCount(): Int = fragments.size
        override fun getItem(p0: Int): Fragment = fragments.toList()[p0].second
        override fun getPageTitle(position: Int): CharSequence? = fragments.keys.toList()[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_control_panel)

        previewHalo = AppNotificationHalo(this, null).apply{
            id = View.generateViewId()
        }

        preview_layout.addView(previewHalo, FrameLayout.LayoutParams(Utilities.dpToPx(this, 200), Utilities.dpToPx(this, 200), Gravity.CENTER))
        preview_layout.addView(
                ImageView(this).apply{
                    id = View.generateViewId()
                    setImageDrawable(getDrawable(R.drawable.kakaotalk_logo))
                },
                FrameLayout.LayoutParams(Utilities.dpToPx(this, 80), Utilities.dpToPx(this, 80), Gravity.CENTER)
        )

        val viewPagerAdapter = ScreenSlidPagerAdapter(supportFragmentManager).apply{
            fragments["Data"] = HaloDataSettingFragment()
            fragments["Enhancement"] = HaloEnhancementSettingFragment.newInstance()
            fragments["Layout"] = HaloLayoutFragment.newInstance()
            fragments["Indpendent"] = HaloIndependentEffectSettingFragment.newInstance()
            fragments["Aggregated"] = HaloIndependentEffectSettingFragment.newInstance()
        }

        view_pager.adapter = viewPagerAdapter
        tabs.setupWithViewPager(view_pager)

        appConfigViewModel = ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        val appConfigObserver = Observer<AppHaloConfig>{ newConfig ->
            Log.d(TAG, "Model Updated")
            newConfig?.let{
                previewHalo.setVisConfig(it)
                previewHalo.setAppHaloData(EnhancedAppNotifications(AppHaloConfigViewModel.SAMPLE_PACKAGE_NAME).also{ notifications ->
                    notifications.notificationData = exampleNotifications.toMutableList()
                })
                preview_layout.invalidate()
            }
        }
        appConfigViewModel.appHaloConfigLiveData.observe(this, appConfigObserver)
    }
}