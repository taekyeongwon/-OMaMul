package com.tkw.common

interface ItemMoveListener {
    fun onItemMove(from: Int, to: Int)
    fun onStopDrag()
}