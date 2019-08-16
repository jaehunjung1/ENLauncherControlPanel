package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kr.ac.snu.hcil.enlaunchercontrolpanel.R

class AccessibilityControllableStringAdapter(context: Context, stringList: List<String>)
    : ArrayAdapter<String>(context, R.layout.item_spinner, stringList ){

    private val contentList: List<String> = stringList
    private val enableList: MutableList<Boolean> = List(stringList.size){true}.toMutableList()

    fun setElementEnable(el: String, enabled: Boolean){
        if(el in contentList)
            enableList[contentList.indexOf(el)] = enabled
    }

    fun setElementsEnabled(vararg el: String){
        contentList.forEachIndexed{index, content -> enableList[index] = (content in el) }
        notifyDataSetChanged()
    }

    override fun isEnabled(position: Int): Boolean {
        return enableList[position]
    }

    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val tv = view as TextView
        if (isEnabled(position)) {
            tv.setTextColor(context.getColor(R.color.theme))
        } else {
            tv.setTextColor(Color.GRAY)
        }
        return view
    }
}