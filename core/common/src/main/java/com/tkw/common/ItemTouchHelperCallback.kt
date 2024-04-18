package com.tkw.common

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
            //https://velog.io/@chris_seed/AndroidKotlin-RecyclerView%EC%9D%98-%EB%AA%A8%EB%93%A0%EA%B2%83-%EA%B8%B0%EB%B3%B8-%EC%82%AC%EC%9A%A9%EB%B2%95
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                viewHolder?.itemView?.alpha = 0.5f
            }
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                moveListener.onStopDrag()
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1f
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //옆으로 스와프해서 지울 수 있도록
    }

    override fun isLongPressDragEnabled(): Boolean {
        return longClickEnabled
    }
}