package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.Spinner
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable
import kr.ac.snu.hcil.enlaunchercontrolpanel.R

class NominalVisVarContentSpinner{
    companion object {
        fun generate(
                context: Context,
                appConfig: AppHaloConfig,
                visVar: NotiVisVariable,
                itemLayoutID: Int = R.layout.item_spinner
        ): Spinner{

            val spinner = Spinner(context)

            when(visVar){
                NotiVisVariable.COLOR ->{}
                NotiVisVariable.SHAPE -> {}
                NotiVisVariable.MOTION -> {}
                NotiVisVariable.POSITION -> {}
                NotiVisVariable.SIZE -> {}
            }

            return spinner
        }
    }
}