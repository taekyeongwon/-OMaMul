package com.tkw.ui

interface ItemMoveListener {
    fun onItemMove(from: Int, to: Int)
    fun onStopDrag()
}