package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class KeywordGroupDetailsLookup(private val recyclerView: RecyclerView)
    : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        return recyclerView.findChildViewUnder(e.x, e.y)?.let{view ->
            (recyclerView.getChildViewHolder(view) as KeywordGroupRecyclerAdapter.KeywordGroupViewHolder).getItemDetails()
        }
    }
}