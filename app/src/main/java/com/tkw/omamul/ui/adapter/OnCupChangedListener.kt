package com.tkw.omamul.ui.adapter

import com.tkw.omamul.data.model.CupEntity

interface OnCupChangedListener {
    fun onChanged(cup: CupEntity)
}