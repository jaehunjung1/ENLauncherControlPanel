package kr.ac.snu.hcil.enlaunchercontrolpanel

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments.*
import kr.ac.snu.hcil.enlaunchercontrolpanel.utilities.Utilities
import kotlinx.android.synthetic.main.activity_new_control_panel.*
import kotlinx.android.synthetic.main.activity_new_control_panel.preview_layout

import kr.ac.snu.hcil.datahalo.haloview.AppNotificationHalo
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife
import kr.ac.snu.hcil.datahalo.notificationdata.NotiContent
import kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig

class ControlPanelActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object{
        private const val TAG = "HALO_SETTING_ACTIVITY"
        private val exampleNotifications = listOf(
                kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification(25123, "", System.currentTimeMillis(), kr.ac.snu.hcil.datahalo.notificationdata.NotiContent("한구현", "집에 갑시다.1")).also{
                    it.currEnhancement = 0.3
                    it.lifeCycle = kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED
                },
                kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification(34532, "", System.currentTimeMillis(), kr.ac.snu.hcil.datahalo.notificationdata.NotiContent("정재훈", "집에 갑시다.2")).also{
                    it.currEnhancement = 0.8
                    it.lifeCycle = kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife.INTERACTED_NOT_DECAYING
                },
                kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification(54634, "", System.currentTimeMillis(), kr.ac.snu.hcil.datahalo.notificationdata.NotiContent("안단태", "집에 갑시다.3")).also{
                    it.currEnhancement = 0.2
                    it.lifeCycle = kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife.DECAYING
                },
                kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification(65124, "", System.currentTimeMillis(), kr.ac.snu.hcil.datahalo.notificationdata.NotiContent("한구현", "집에 갑시다.4")).also{
                    it.currEnhancement = 0.5
                    it.lifeCycle = kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife.INTERACTED_NOT_DECAYING
                },
                kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification(66893, "", System.currentTimeMillis(), kr.ac.snu.hcil.datahalo.notificationdata.NotiContent("정재훈", "집에 갑시다.5")).also{
                    it.currEnhancement = 0.8
                    it.lifeCycle = kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife.TRIGGERED_NOT_INTERACTED
                },
                kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotification(70892, "", System.currentTimeMillis(), kr.ac.snu.hcil.datahalo.notificationdata.NotiContent("안단태", "집에 갑시다.6")).also{
                    it.currEnhancement = 0.1
                    it.lifeCycle = kr.ac.snu.hcil.datahalo.notificationdata.EnhancedNotificationLife.DECAYING
                }
        )
    }

    private lateinit var appConfigViewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel
    private lateinit var previewHalo: kr.ac.snu.hcil.datahalo.haloview.AppNotificationHalo

    private val controlPanelFragments: Map<String, Fragment> = mapOf(
            "Predefined Components" to HaloExampleCollectionFragment.newInstance(),
            "Notification Filters" to HaloDataSettingFragment(),
            "Enhancement Patterns" to HaloEnhancementSettingFragment.newInstance(),
            "Keyword Group Setting" to HaloKeywordGroupSettingFragment(),
            "Layout Methods" to HaloLayoutSettingFragment.newInstance(),
            "Independent Effects" to HaloIndependentEffectSettingFragment.newInstance(),
            "Aggregated Effects" to HaloAggregatedEffectSettingFragment.newInstance()
    )
    private var viewPagerAdapter: ScreenSlidPagerAdapter? = null


    private inner class ScreenSlidPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm){
        val fragments: MutableMap<String, Fragment> = mutableMapOf()
        override fun getCount(): Int = fragments.size
        override fun getItem(p0: Int): Fragment = fragments.toList()[p0].second
        override fun getPageTitle(position: Int): CharSequence? = fragments.keys.toList()[position]
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        viewPagerAdapter?.let{adapter ->
            setViewPager(adapter, item.itemId)
        }
        controlpanel_drawer.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            viewPagerAdapter?.getItem(view_pager.currentItem)?.onActivityResult(requestCode, resultCode, data )
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setViewPager(adapter: ScreenSlidPagerAdapter, itemId: Int){
        adapter.apply{
            fragments.clear()
            when(itemId){
                R.id.examples -> {
                    fragments["Predefined Components"] = controlPanelFragments["Predefined Components"]!!
                }
                R.id.data_manipulation -> {
                    fragments["Notification Filters"] = controlPanelFragments["Notification Filters"]!!
                    fragments["Keyword Group Setting"] = controlPanelFragments["Keyword Group Setting"]!!
                    fragments["Enhancement Patterns"] = controlPanelFragments["Enhancement Patterns"]!!
                }
                R.id.visual_setting -> {
                    fragments["Layout Methods"] = controlPanelFragments["Layout Methods"]!!
                    fragments["Independent Effects"] = controlPanelFragments["Independent Effects"]!!
                    fragments["Aggregated Effects"] = controlPanelFragments["Aggregated Effects"]!!
                }
                else -> {}
            }
        }
        view_pager.adapter = adapter
        tabs.setupWithViewPager(view_pager, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_control_panel)

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(
                        DialogOnAnyDeniedMultiplePermissionsListener.Builder
                                .withContext(this)
                                .withTitle("Read and Write External Storage")
                                .withMessage("These Permissions are Needed to Access Gallery and Create a Cropped Image Source")
                                .withButtonText(android.R.string.ok)
                                .withIcon(R.mipmap.ic_launcher).build()
                ).check()

        previewHalo = kr.ac.snu.hcil.datahalo.haloview.AppNotificationHalo(this, null).apply{
            id = View.generateViewId()
        }

        preview_layout.addView(
                previewHalo,
                FrameLayout.LayoutParams(Utilities.dpToPx(this, 200), Utilities.dpToPx(this, 200), Gravity.CENTER)
        )

        preview_layout.addView(
                ImageView(this).apply{
                    id = View.generateViewId()
                    setImageDrawable(getDrawable(R.drawable.kakaotalk_logo))
                },
                FrameLayout.LayoutParams(Utilities.dpToPx(this, 80), Utilities.dpToPx(this, 80), Gravity.CENTER)
        )

        viewPagerAdapter = ScreenSlidPagerAdapter(supportFragmentManager).apply{
            setViewPager(this, R.id.examples)
        }

        appConfigViewModel = ViewModelProviders.of(this).get(kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel::class.java)
        val appConfigObserver = Observer<kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig>{ newConfig ->
            Log.d(TAG, "Model Updated")
            newConfig?.let{
                previewHalo.setVisConfig(it)
                previewHalo.setAppHaloData(kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications(kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel.SAMPLE_PACKAGE_NAME).also{ notifications ->
                    notifications.notificationData = exampleNotifications.toMutableList()
                })
                preview_layout.invalidate()
            }
        }
        appConfigViewModel.appHaloConfigLiveData.observe(this, appConfigObserver)


        setSupportActionBar(tool_bar)
        supportActionBar?.apply{
            title = "App Halo Setting"
            setDisplayHomeAsUpEnabled(true)
        }

        controlpanel_drawer.addDrawerListener(ActionBarDrawerToggle(this, controlpanel_drawer, tool_bar, R.string.drawer_open, R.string.drawer_closed))
        controlpanel_navigation_view.setNavigationItemSelectedListener(this)
    }
}