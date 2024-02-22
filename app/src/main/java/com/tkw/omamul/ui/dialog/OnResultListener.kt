package com.tkw.omamul.ui.dialog

interface OnResultListener<T> {
    fun onResult(vararg data: T)
}