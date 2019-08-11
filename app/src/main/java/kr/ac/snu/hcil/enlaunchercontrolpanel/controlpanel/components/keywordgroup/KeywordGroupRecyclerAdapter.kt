package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.nex3z.flowlayout.FlowLayout
import com.robertlevonyan.views.chip.Chip
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import java.util.*

class KeywordGroupRecyclerAdapter(private val viewModel: AppHaloConfigViewModel)
    : RecyclerView.Adapter<KeywordGroupRecyclerAdapter.KeywordGroupViewHolder>(), KeywordGroupItemTouchHelperAdapter{
    companion object{
        private const val TAG = "KeywordGroupAdapter"
    }

    private val keywordGroupData: MutableList<Triple<Long, String, MutableSet<String>>> = mutableListOf()
    private var layoutInflater: LayoutInflater? = null
    var startDragListener: OnStartDragListener? = null


    var tracker: SelectionTracker<Long>? = null


    init{
        setHasStableIds(true)
        viewModel.appHaloConfigLiveData.value?.let{ appConfig ->
            keywordGroupData.addAll(appConfig.keywordGroupPatterns.getOrderedKeywordGroupImportancePatterns().map{Triple(it.id, it.group,  it.keywords)})
        }
        notifyDataSetChanged()
    }

    override fun onItemDismiss(pos: Int) {
        val source = keywordGroupData[pos]
        keywordGroupData.removeAt(pos)
        notifyItemRemoved(pos)
        viewModel.appHaloConfigLiveData.value?.let{ appConfig ->
            appConfig.keywordGroupPatterns.deleteKeywordGroup(source.second)
            viewModel.appHaloConfigLiveData.value = appConfig
        }
    }

    override fun onItemMove(from: Int, to: Int): Boolean {
        val source = keywordGroupData[from]
        Log.i(TAG, "id${source.first}: $from to $to")
        Collections.swap(keywordGroupData, from, to)
        notifyItemMoved(from, to)
        viewModel.appHaloConfigLiveData.value?.let{ appConfig ->
            appConfig.keywordGroupPatterns.changeRankOfGroup(source.second, to)
            viewModel.appHaloConfigLiveData.value = appConfig
        }
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordGroupViewHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        return KeywordGroupViewHolder(layoutInflater!!.inflate(R.layout.item_keyword_group, parent, false))
    }

    override fun onBindViewHolder(holder: KeywordGroupViewHolder, position: Int) {
        /*
        tracker?.let{
            holder.bind(keywordGroupData[position], it.isSelected(keywordGroupData[position].first))
        }
        */
        holder.bind(keywordGroupData[position])
    }

    override fun getItemCount(): Int {
        return keywordGroupData.size
    }

    override fun getItemId(position: Int): Long = keywordGroupData[position].first

    private fun isKeywordInsertable(keyword: String): Boolean{
        return keywordGroupData.filter{keyword in it.second}.isEmpty()
    }

    /*
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
    */

    inner class KeywordGroupViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), KeywordGroupItemTouchHelperViewHolder{
        private val handle: ImageView = itemView.findViewById(R.id.handle)
        private var groupText: TextView = itemView.findViewById(R.id.flow_layout_text)
        private var flowLayout: FlowLayout = itemView.findViewById(R.id.flow_layout)
        private var button: Button = itemView.findViewById(R.id.flow_layout_button)

        private fun addKeywordChipView(group: String, keyword: String, isNew: Boolean = false){
            (layoutInflater?.inflate(R.layout.chip_view_layout, null) as Chip).let{chip ->
                chip.tag = group
                chip.chipText = keyword
                chip.setOnCloseClickListener{
                    val gName: String = chip.tag as String
                    val kName: String = chip.chipText
                    chip.visibility = View.GONE
                    keywordGroupData.find{pair -> pair.second == gName}!!.third.remove(kName)
                    flowLayout.removeView(chip)
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

        fun bind(value: Triple<Long, String, MutableSet<String>>, activated: Boolean = false){
            groupText.text = value.second

            handle.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    startDragListener?.onStartDrag(this)
                }
                false
            }

            value.third.forEach{ keyword ->
                addKeywordChipView(value.second, keyword)
            }

            button.setOnClickListener {
                val context = layoutInflater!!.context
                val mDialog = AlertDialog.Builder(context).let { mBuilder ->
                    val editText = EditText(context)
                    mBuilder.setView(editText)
                    mBuilder.setPositiveButton("OK") { dialogInterface, i ->
                        val newKeyword = editText.text.toString()
                        if(newKeyword.isNotEmpty() && isKeywordInsertable(newKeyword)){
                            keywordGroupData.find{it.first == value.first}!!.third.add(newKeyword)
                            addKeywordChipView(value.second, newKeyword, true)
                        }
                    }
                    mBuilder.setNegativeButton("Cancel") { _, _ -> }
                    mBuilder.create()
                }
                mDialog.show()
            }

            itemView.isActivated  = activated
        }

        override fun onItemClear() {
           itemView.setBackgroundColor(0)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object: ItemDetailsLookup.ItemDetails<Long>(){
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long? = itemId
            /*
            override fun inDragRegion(e: MotionEvent): Boolean { return super.inDragRegion(e) }
            override fun inSelectionHotspot(e: MotionEvent): Boolean { return super.inSelectionHotspot(e) }
            */
        }
    }
}