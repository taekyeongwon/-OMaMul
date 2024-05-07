package com.tkw.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tkw.domain.model.Cup

class CupDiffCallback: DiffUtil.ItemCallback<Cup>() {
    //이전 어댑터와 바뀌는 어댑터의 아이템이 동일한지 확인. (어떤 조건으로 같은 아이템으로 볼건지)
    // true면 areContentsTheSame 호출, false면 RecyclerView에 notify
    override fun areItemsTheSame(oldItem: Cup, newItem: Cup): Boolean {
        return oldItem.cupId == newItem.cupId
    }

    //이전 어댑터와 바뀌는 어댑터의 아이템 내 내용 비교(eqauls).
    override fun areContentsTheSame(oldItem: Cup, newItem: Cup): Boolean {
        return oldItem == newItem
    }
}