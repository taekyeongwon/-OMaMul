package com.tkw.common

import androidx.recyclerview.widget.RecyclerView

interface OnItemDrag<T> {
    //adapter -> ItemTouchHelper에 이벤트 전달
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    //adapter -> activity로 이벤트 전달
    fun onStopDrag(list: List<T>)
}