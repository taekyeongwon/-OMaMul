package com.tkw.alarm.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tkw.domain.model.Alarm

class AlarmDiffCallback: DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem.alarmId == newItem.alarmId
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }
}