package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kr.ac.snu.hcil.datahalo.notificationdata.EnhancementPattern
import kr.ac.snu.hcil.datahalo.visconfig.KeywordGroupImportance
import kr.ac.snu.hcil.datahalo.visconfig.NotificationEnhacementParams
import kr.ac.snu.hcil.enlaunchercontrolpanel.R

class KeywordGroupChildView: LinearLayout {
    companion object{
        private const val TAG = "KeywordGroup_Child"
    }

    private var keywordGroupImportance: KeywordGroupImportance? = null


    interface KeywordGroupChildInteractionListener{
        fun onEnhancementParamUpdated(pattern: NotificationEnhacementParams)
    }

    constructor(context: Context): super(context){
        init(null, 0)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    fun setProperties(keywordGroupImportance: KeywordGroupImportance){
        this.keywordGroupImportance = keywordGroupImportance
        val enhancementParam = keywordGroupImportance.enhancementParam
        //TODO: update UI

    }

    private fun init(attrs: AttributeSet?, defStyle: Int){
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.KeywordGroupChildView, defStyle, 0)
        //if attributes are required
        a.recycle()

        View.inflate(context, R.layout.item_parent_keywordgroup_expandable_controlpanel, this)

        //Initial UI setting
        
    }
}