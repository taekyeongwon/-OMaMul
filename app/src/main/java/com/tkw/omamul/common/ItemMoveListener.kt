package com.tkw.omamul.common

interface ItemMoveListener {
    fun onItemMove(from: Int, to: Int)
    fun onStopDrag()
}