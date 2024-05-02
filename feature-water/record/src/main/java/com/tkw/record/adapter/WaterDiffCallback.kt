package com.tkw.record.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tkw.domain.model.Water

class WaterDiffCallback: DiffUtil.ItemCallback<Water>() {
    //이전 어댑터와 바뀌는 어댑터의 아이템이 동일한지 확인.
    // true면 areContentsTheSame 호출, false면 RecyclerView에 notify
    override fun areItemsTheSame(oldItem: Water, newItem: Water): Boolean {
        return oldItem.dateTime == newItem.dateTime
    }

    //이전 어댑터와 바뀌는 어댑터의 아이템 내 내용 비교(eqauls).
    override fun areContentsTheSame(oldItem: Water, newItem: Water): Boolean {
        return oldItem == newItem
    }
}