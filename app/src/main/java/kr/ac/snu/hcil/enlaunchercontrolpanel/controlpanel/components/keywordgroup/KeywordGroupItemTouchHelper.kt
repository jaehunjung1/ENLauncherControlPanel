package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup

import android.graphics.Canvas
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue


interface KeywordGroupItemTouchHelperAdapter{
    //RecyclerView.Adapter#notifyItemMoved(int, int)
    fun onItemMove(from: Int, to: Int): Boolean
    //RecyclerView.Adapter#notifyItemRemoved(int)
    fun onItemDismiss(pos: Int)
}

interface KeywordGroupItemTouchHelperViewHolder{
    fun onItemSelected()
    fun onItemClear()
}

interface OnStartDragListener{
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}

class KeywordGroupItemTouchHelperCallback(private val adapter: KeywordGroupItemTouchHelperAdapter): ItemTouchHelper.Callback() {

    companion object{
        private const val TAG = "KeywordGroupItemTouchHelperCallback"
    }

    override fun isLongPressDragEnabled(): Boolean = true
    override fun isItemViewSwipeEnabled(): Boolean = true


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return recyclerView.layoutManager?.let{ layoutManager ->
            when(layoutManager){
                is LinearLayoutManager -> {
                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    makeMovementFlags(dragFlags, swipeFlags)
                }
                is GridLayoutManager -> {
                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    val swipeFlags = 0
                    makeMovementFlags(dragFlags, swipeFlags)
                }
                else ->{
                    makeMovementFlags(0, 0)
                }
            }
        }?: throw Exception("$TAG: LayoutManager is Null ")
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (viewHolder.itemViewType != target.itemViewType) {
            return false
        }
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            val alpha = 1.0f - (dX.absoluteValue / viewHolder.itemView.width)
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is KeywordGroupItemTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                val itemViewHolder = viewHolder as KeywordGroupItemTouchHelperViewHolder?
                itemViewHolder!!.onItemSelected()
            }
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        viewHolder.itemView.alpha = 1.0f

        if (viewHolder is KeywordGroupItemTouchHelperViewHolder) {
            // Tell the view holder it's time to restore the idle state
            val itemViewHolder = viewHolder as KeywordGroupItemTouchHelperViewHolder
            itemViewHolder.onItemClear()
        }
    }
}