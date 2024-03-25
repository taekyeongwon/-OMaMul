package com.tkw.omamul.common

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * 참조 : https://yuar.tistory.com/entry/RecyclerView-Drag-Drop
 */
class ItemTouchHelperCallback(
    private val moveListener: ItemMoveListener,
    private val longClickEnabled: Boolean
): ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        )
    }

    //드래그하는 동안 onItemMove 호출
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        moveListener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    //놓는 순간 어댑터에 onStopDrag 호출
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        when(actionState) {
            ItemTouchHelper.ACTION_STATE_IDLE -> moveListener.onStopDrag()
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //옆으로 스와프해서 지울 수 있도록
    }

    override fun isLongPressDragEnabled(): Boolean {
        return longClickEnabled
    }
}