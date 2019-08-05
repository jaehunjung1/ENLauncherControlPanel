package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kr.ac.snu.hcil.datahalo.manager.VisDataManager
import kr.ac.snu.hcil.datahalo.visconfig.KeywordGroupImportance
import kr.ac.snu.hcil.enlaunchercontrolpanel.R

class KeywordGroupParentView: LinearLayout {
    companion object{
        private const val TAG = "KeywordGroup_Parent"
    }

    interface KeywordGroupParentInteractionListener{
        fun onMappingUpdate(patternName: String)
    }

    var notificationEnhacementParamChangedListener: KeywordGroupParentInteractionListener? = null
    private var keywordGroup: KeywordGroupImportance? = null

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
        keywordGroup = keywordGroupImportance
        //set UIs
        findViewById<TextView>(R.id.keyword_group_name).text = keywordGroupImportance.group
        findViewById<Spinner>(R.id.enhancement_param_spinner).let{ spinner ->
            when(keywordGroupImportance.type){
                VisDataManager.CUSTOM_PATTERN -> {
                    spinner.setSelection(
                            VisDataManager.availableImportanceSaturationPatterns().size
                    )
                }
                else -> {
                    spinner.setSelection(
                            VisDataManager.availableImportanceSaturationPatterns().indexOf(keywordGroupImportance.type)
                    )
                }
            }
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int){

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.KeywordGroupParentView, defStyle, 0)
        //if attributes are required
        a.recycle()

        View.inflate(context, R.layout.item_parent_keywordgroup_expandable_controlpanel, this)
        findViewById<Spinner>(R.id.enhancement_param_spinner).let{ spinner ->
            val spinnerValues = VisDataManager.exampleImportanceSaturationPatterns.toList().toMutableList().also{
                it.add(Pair("CUSTOM", "User Custom Patterns"))
            }.toList()

            val adapter = getArrayAdapter(spinnerValues)
            adapter.setDropDownViewResource(R.layout.item_with_explanation_spinner)

            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedPattern = (parent.getItemAtPosition(position) as Pair<String, String>).first
                    if(selectedPattern != "CUSTOM")
                        notificationEnhacementParamChangedListener?.onMappingUpdate(selectedPattern)
                    else
                        notificationEnhacementParamChangedListener?.onMappingUpdate(VisDataManager.CUSTOM_PATTERN)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

    }
    private fun getArrayAdapter(contentList: List<Pair<String, String>>): ArrayAdapter<Pair<String,String>> {
        return object : ArrayAdapter<Pair<String, String>>(context, R.layout.item_with_explanation_spinner, contentList) {
            override fun isEnabled(position: Int): Boolean {
                return true
            }

            override fun getDropDownView(position: Int, convertView: View?,
                                         parent: ViewGroup): View {
                return (super.getDropDownView(position, convertView, parent) as LinearLayout).also{ itemLayout ->
                    val (title, content) = getItem(position) as Pair<String, String>
                    itemLayout.findViewById<TextView>(R.id.item_title).text = title
                    itemLayout.findViewById<TextView>(R.id.item_content).text = content
                }
            }
        }
    }

}