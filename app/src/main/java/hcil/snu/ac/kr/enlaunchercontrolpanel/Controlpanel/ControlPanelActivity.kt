package hcil.snu.ac.kr.enlaunchercontrolpanel.Controlpanel

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

import java.util.ArrayList

import hcil.snu.ac.kr.enlaunchercontrolpanel.AuraPreview
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.VisualParamContainer
import hcil.snu.ac.kr.enlaunchercontrolpanel.R
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.PreviewParamModel
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.StaticMode
import kr.ac.snu.hcil.datahalo.haloview.AppNotificationHalo
import kr.ac.snu.hcil.datahalo.manager.DataHaloManager
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancedAppNotifications
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig

class ControlPanelActivity : AppCompatActivity() {
    private val previewPackageNmae = "kr.ac.snu.hcil.datahalo.preview"
    private lateinit var appConfigViewModel: AppHaloConfigViewModel
    private lateinit var previewHalo: AppNotificationHalo


    lateinit var auraPreview: AuraPreview

    var enavShape: Int = 0
    var enavColor: String? = null

    private var paramModel: PreviewParamModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controlpanel)

        previewHalo = DataHaloManager.createAppHalo(this, previewPackageNmae)
        appConfigViewModel = ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        val appConfigObserver = Observer<AppHaloConfig>{ newConfig ->
            newConfig?.let{
                previewHalo.setVisConfig(it)
                previewHalo.setAppHaloData(EnhancedAppNotifications(previewPackageNmae))
            }
        }
        appConfigViewModel.appHaloConfigLiveData.observe(this, appConfigObserver)


        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment, Setting1Fragment(), "FRAGMENT_SETTING1")
                .commit()

        auraPreview = findViewById(R.id.aura_preview)

        /* *
        * Initial EAAV Attaching
        * // TODO EAAV도 AuraPreview에서 define 할 수 있도록 변경
        * */
        val testImageView = ImageView(this@ControlPanelActivity)
        testImageView.id = View.generateViewId()
        testImageView.setImageResource(R.drawable.kakaotalk_logo)

        auraPreview.eaav = testImageView


        /* *
        * Initial Data List Attaching (Notification Data Attaching)
        * currently, data list is simple integer list
        * */
        val enavDataList = ArrayList<Int>()
        for (i in 0 until enavNum) {
            enavDataList.add(i)
        }

        /* *
         * Initial Visual Param List Attaching (Notification Data Attaching)
         * currently, visual param list is simple integer list
         * 각 원소는 ENAV 각각의 visual parameters
         * */
        val enavVisualParamList = ArrayList<Int>()
        for (i in 0 until enavNum) {
            enavVisualParamList.add(i)
        }

        val visualParamContainer = VisualParamContainer(
                StaticMode.SNAKE, enavNum, 0,
                "phaedra", enavVisualParamList
        )
        auraPreview.setENAVList(enavDataList, visualParamContainer)


        /* *
        * PreviewParamModel Initializing
        * */
        enavShape = 0
        enavColor = "phaedra"
        paramModel = ViewModelProviders.of(this).get(PreviewParamModel::class.java)
        paramModel!!.init(StaticMode.SNAKE, enavNum, 0,
                "phaedra")

        paramModel!!.staticModeLiveData.observe(this, Observer { staticMode -> auraPreview.changeStaticMode(staticMode) })
        paramModel!!.kNumLiveData.observe(this, Observer { k -> auraPreview.changeKNum(k!!) })
        paramModel!!.enavShapeLiveData.observe(this, Observer { shape ->
            enavShape = shape!!
            auraPreview.changeENAVShapeAndColor(enavShape, enavColor)
        })
        paramModel!!.enavColorLiveData.observe(this, Observer { color ->
            enavColor = color
            auraPreview.changeENAVShapeAndColor(enavShape, enavColor)
        })
    }

    companion object {

        internal val enavNum = 6 // number of ENAVs in preview
    }
}
