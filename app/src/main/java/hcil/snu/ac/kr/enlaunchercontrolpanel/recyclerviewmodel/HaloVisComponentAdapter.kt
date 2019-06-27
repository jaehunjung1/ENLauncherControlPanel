package hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker

import hcil.snu.ac.kr.enlaunchercontrolpanel.R

class HaloVisComponentAdapter(private val context: Context, hlmArrayList: List<HaloVisComponent>) : RecyclerView.Adapter<HaloVisComponentAdapter.HaloLayoutViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val componentList: List<HaloVisComponent> = hlmArrayList
    var tracker: SelectionTracker<Long>? = null
    init{
        setHasStableIds(true)
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HaloLayoutViewHolder {

        val view = layoutInflater.inflate(R.layout.recyler_item_controlpanel, viewGroup, false)
        val holder = HaloLayoutViewHolder(view)
        holder.itemView.setOnClickListener { view ->
            val card = (view as ViewGroup).getChildAt(0) as CardView
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chip_background))
        }
        return holder
    }

    override fun onBindViewHolder(viewHolder: HaloLayoutViewHolder, i: Int) {
        tracker?.let{
            viewHolder.bind(componentList[i], it.isSelected(i.toLong()))
        }
    }

    override fun getItemCount(): Int {
        return this.componentList.size
    }

    override fun getItemId(position: Int): Long = position.toLong()

    inner class HaloLayoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.recycler_item_imageview)
        var textView: TextView = itemView.findViewById(R.id.recycler_item_textview)

        fun bind(value: HaloVisComponent, activated: Boolean = false){
            imageView.setImageResource(value.drawableId)
            textView.text = value.label
            itemView.isActivated = activated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object: ItemDetailsLookup.ItemDetails<Long>(){
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long = itemId
        }
    }
}
