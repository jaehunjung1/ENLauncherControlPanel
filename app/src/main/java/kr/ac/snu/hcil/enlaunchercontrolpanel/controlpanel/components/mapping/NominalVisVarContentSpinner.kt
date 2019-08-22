package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.mapping

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import kr.ac.snu.hcil.datahalo.utils.MapFunctionUtilities
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.datahalo.visconfig.NotiProperty
import kr.ac.snu.hcil.datahalo.visconfig.NotiVisVariable
import kr.ac.snu.hcil.datahalo.visualEffects.VisShapeType
import kr.ac.snu.hcil.enlaunchercontrolpanel.R

class NominalVisVarContentSpinner{
    companion object {
        fun generateForIndependentMapping(
                context: Context,
                appConfig: AppHaloConfig,
                visVar: NotiVisVariable
        ): Spinner{
            val spinner = Spinner(context, Spinner.MODE_DIALOG)
            spinner.isFocusable = false
            spinner.isFocusableInTouchMode = false
            when(visVar){
                NotiVisVariable.COLOR ->{
                    //지금대로 크게 문제 없음
                }
                NotiVisVariable.SHAPE -> {
                    val adapter = NominalVisVarContentAdapter(context, VisShapeType.values().map{Triple(it.name, it.explanation, it)})
                    spinner.adapter = adapter
                }
                NotiVisVariable.MOTION -> {
                    //TODO(Motion Dictionaries)
                    val adapter = NominalVisVarContentAdapter(context, emptyList())
                    spinner.adapter = adapter
                }
                NotiVisVariable.POSITION -> {
                    val contents = MapFunctionUtilities.bin(Pair(0.0, 1.0), appConfig.independentDataParameters[0].binNums)
                    val keys = contents.map{"${"%.2f".format(it.first)}-${"%.2f".format(it.second)}"}
                    val explanations = contents.mapIndexed{index, pair -> " ${index + 1} of ${contents.size} binned position ranges"}
                    val adapter = NominalVisVarContentAdapter(context, List(contents.size){index -> Triple(keys[index], explanations[index], contents[index])})
                    spinner.adapter = adapter
                }
                NotiVisVariable.SIZE -> {
                    val contents = MapFunctionUtilities.bin(Pair(0.0, 1.0), appConfig.independentDataParameters[0].binNums)
                    val keys = contents.map{"${"%.2f".format(it.first)}-${"%.2f".format(it.second)}"}
                    val explanations = contents.mapIndexed{index, pair -> " ${index + 1} of ${contents.size} binned size ranges"}
                    val adapter = NominalVisVarContentAdapter(context, List(contents.size){index -> Triple(keys[index], explanations[index], contents[index])})
                    spinner.adapter = adapter
                }
            }
            return spinner
        }
    }

    class NominalVisVarContentAdapter(context: Context, contents: List<Triple<String, String, Any>>)
        : ArrayAdapter<Triple<String, String, Any>>(context,
            R.layout.item_spinner,
            contents){

        fun findContent(content: Any): Int?{
            for(i in 0 until count){
               if(getItem(i)!!.third == content)
                   return i
            }
            return null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val (key, _, _ ) = getItem(position) as Triple<String, String, Any>
            return (convertView as TextView?)?.apply{
                text = key
            }?: (LayoutInflater.from(context).inflate(R.layout.item_spinner, null) as TextView).apply{
                text = key
            }
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val (key, explanation, _ ) = getItem(position) as Triple<String, String, Any>
            return (convertView as LinearLayout?)?.apply{
                findViewById<TextView>(R.id.item_title).text = key
                findViewById<TextView>(R.id.item_content).text = explanation

            }?: (LayoutInflater.from(parent.context!!).inflate(R.layout.item_with_explanation_spinner, null) as LinearLayout).apply{
                findViewById<TextView>(R.id.item_title).text = key
                findViewById<TextView>(R.id.item_content).text = explanation
            }
        }

    }

}