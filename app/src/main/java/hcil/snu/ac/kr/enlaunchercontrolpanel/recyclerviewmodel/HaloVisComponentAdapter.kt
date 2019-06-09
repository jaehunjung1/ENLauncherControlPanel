package hcil.snu.ac.kr.enlaunchercontrolpanel.recyclerviewmodel

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import hcil.snu.ac.kr.enlaunchercontrolpanel.R

class HaloVisComponentAdapter(private val context: Context, hlmArrayList: List<HaloVisComponent>) : RecyclerView.Adapter<HaloVisComponentAdapter.HaloLayoutViewHolder>() {
    private val layoutInflater: LayoutInflater
    private val haloLayoutModelArrayList: List<HaloVisComponent>

    init {
        layoutInflater = LayoutInflater.from(context)
        this.haloLayoutModelArrayList = hlmArrayList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HaloVisComponentAdapter.HaloLayoutViewHolder {

        val view = layoutInflater.inflate(R.layout.recyler_item_controlpanel, viewGroup, false)
        val holder = HaloLayoutViewHolder(view)
        holder.itemView.setOnClickListener { view ->
            val card = (view as ViewGroup).getChildAt(0) as CardView
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chip_background))
        }
        return holder
    }

    override fun onBindViewHolder(viewHolder: HaloLayoutViewHolder, i: Int) {
        viewHolder.imageView.setImageResource(this.haloLayoutModelArrayList[i].drawableId)
        viewHolder.textView.text = this.haloLayoutModelArrayList[i].label
    }

    override fun getItemCount(): Int {
        return this.haloLayoutModelArrayList.size
    }

    inner class HaloLayoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.recycler_item_imageview)
        var textView: TextView = itemView.findViewById(R.id.recycler_item_textview)
    }
}
