package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nex3z.flowlayout.FlowLayout
import com.robertlevonyan.views.chip.Chip
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.enlaunchercontrolpanel.R

class KeywordGroupRecyclerAdapter(private val viewModel: AppHaloConfigViewModel): RecyclerView.Adapter<KeywordGroupRecyclerAdapter.KeywordGroupViewHolder>() {
    private var keywordGroupData: MutableMap<String, MutableList<String>> = mutableMapOf()
    private var layoutInflater: LayoutInflater? = null
    var tracker: SelectionTracker<Long>? = null

    init{
        setHasStableIds(true)
        viewModel.appHaloConfigLiveData.value?.let{ appConfig ->
            keywordGroupData = appConfig.keywordGroupPatterns.getOrderedKeywordGroupImportancePatterns().map{it.group to it.keywords.toMutableList()}.toMap().toMutableMap()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordGroupViewHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        return KeywordGroupViewHolder(layoutInflater!!.inflate(R.layout.item_keyword_group, parent, false))
    }

    override fun onBindViewHolder(holder: KeywordGroupViewHolder, position: Int) {
        tracker?.let{
            holder.bind(keywordGroupData.toList()[position], it.isSelected(position.toLong()))
        }
    }

    override fun getItemCount(): Int {
        return keywordGroupData.size
    }

    override fun getItemId(position: Int): Long = position.toLong()

    private fun isKeywordInsertable(keyword: String): Boolean{
        return keywordGroupData.filter{keyword in it.value}.isEmpty()
    }

    fun addKeywordGroup(group: String){
        if (group !in keywordGroupData){
            keywordGroupData[group] = mutableListOf()
            viewModel.appHaloConfigLiveData.value?.let{ appConfig ->
                //appConfig.independentDataParameters[0].keywordGroupMap = keywordGroupData.toMap()
                viewModel.appHaloConfigLiveData.value = appConfig
            }
            notifyItemInserted(itemCount)
        }
    }

    fun removeKeywordGroup(group: String){
        if (group in keywordGroupData){
            val index = keywordGroupData.keys.toList().indexOf(group)
            keywordGroupData.remove(group)
            viewModel.appHaloConfigLiveData.value?.let{ appConfig ->
                //appConfig.independentDataParameters[0].keywordGroupMap = keywordGroupData.toMap()
                viewModel.appHaloConfigLiveData.value = appConfig
            }
            notifyItemRemoved(index)
        }
    }

    inner class KeywordGroupViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var groupText: TextView = itemView.findViewById(R.id.flow_layout_text)
        private var flowLayout: FlowLayout = itemView.findViewById(R.id.flow_layout)
        private var button: Button = itemView.findViewById(R.id.flow_layout_button)


        private fun addKeywordChipView(group: String, keyword: String, isNew: Boolean = false){
            (layoutInflater?.inflate(R.layout.chip_view_layout, null) as Chip).let{chip ->
                chip.tag = group
                chip.chipText = keyword
                chip.setOnCloseClickListener{
                    val gName: String = it.tag as String
                    val kName: String = (it as Chip).chipText
                    it.visibility = View.GONE
                    keywordGroupData[keyword]?.remove(keyword)
                    flowLayout.removeView(it)
                    viewModel.appHaloConfigLiveData.value?.let{appHaloConfig ->
                        appHaloConfig.keywordGroupPatterns.deleteKeywordInGroup(gName, kName)
                        viewModel.appHaloConfigLiveData.value = appHaloConfig
                    }
                }
                flowLayout.addView(chip)
            }
            if(isNew){
                viewModel.appHaloConfigLiveData.value?.let{appHaloConfig ->
                    appHaloConfig.keywordGroupPatterns.addKeywordToGroup(group, keyword)
                    viewModel.appHaloConfigLiveData.value = appHaloConfig
                }
            }
        }

        fun bind(value: Pair<String, List<String>>, activated: Boolean = false){
            groupText.text = value.first

            value.second.forEach{ keyword ->
                addKeywordChipView(value.first, keyword)
            }

            button.setOnClickListener {
                val context = layoutInflater!!.context
                val mDialog = AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar).let { mBuilder ->
                    val editText = EditText(context)
                    mBuilder.setView(editText)
                    mBuilder.setPositiveButton("OK") { dialogInterface, i ->
                        val newKeyword = editText.text.toString()
                        if(newKeyword.isNotEmpty() && isKeywordInsertable(newKeyword)){
                            keywordGroupData[value.first]!!.add(newKeyword)
                            addKeywordChipView(value.first, newKeyword, true)
                        }
                    }
                    mBuilder.setNegativeButton("Cancel") { _, _ -> }
                    mBuilder.create()
                }
                mDialog.show()
            }

            itemView.isActivated  = activated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object: ItemDetailsLookup.ItemDetails<Long>(){
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long? = itemId
        }
    }
}