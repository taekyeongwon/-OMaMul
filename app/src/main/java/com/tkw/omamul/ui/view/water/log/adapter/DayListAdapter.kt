package com.tkw.omamul.ui.view.water.log.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tkw.domain.util.DateTimeUtils
import com.tkw.domain.model.Water
import com.tkw.omamul.databinding.ItemDayAmountBinding

class DayListAdapter(
    private val editListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit
)
    : ListAdapter<Water, DayListAdapter.DayAmountViewHolder>(WaterDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayAmountViewHolder {
        val binding = ItemDayAmountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayAmountViewHolder(binding, editListener, deleteListener)
    }

    override fun onBindViewHolder(holder: DayAmountViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class DayAmountViewHolder(
        private val binding: ItemDayAmountBinding,
        private val editListener: (Int) -> Unit,
        private val deleteListener: (Int) -> Unit
        ): ViewHolder(binding.root) {
        init {
            with(binding) {
                ibEdit.setOnClickListener { editListener(adapterPosition) }
                ibDelete.setOnClickListener { deleteListener(adapterPosition) }
            }
        }
        fun onBind(item: Water) {
            binding.tvAmount.text = item.amount.toString()
            binding.tvDate.text = DateTimeUtils.getFormattedTime(item.dateTime)
        }
    }
}