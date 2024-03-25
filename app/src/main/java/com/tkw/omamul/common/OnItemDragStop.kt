package com.tkw.omamul.common

import com.tkw.omamul.data.model.Draggable

interface OnItemDragStop {
    fun onStopDrag(list: List<Draggable>)
}