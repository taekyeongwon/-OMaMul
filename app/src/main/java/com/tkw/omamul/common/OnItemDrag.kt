package com.tkw.omamul.common

import androidx.recyclerview.widget.RecyclerView
import com.tkw.model.Cup

interface OnItemDrag {
    //adapter -> ItemTouchHelper에 이벤트 전달
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    //adapter -> activity로 이벤트 전달
    fun onStopDrag(list: List<Cup>)
}