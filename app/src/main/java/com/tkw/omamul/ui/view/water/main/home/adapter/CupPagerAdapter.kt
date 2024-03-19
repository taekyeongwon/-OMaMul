package com.tkw.omamul.ui.view.water.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tkw.omamul.common.C
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.databinding.ItemCupAddBinding
import com.tkw.omamul.databinding.ItemCupBinding

class CupPagerAdapter(private val onClick: (Int) -> Unit, private val onClickAdd: () -> Unit)
    : ListAdapter<Cup, ViewHolder>(CupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(C.ViewType.values()[viewType]) {
            C.ViewType.CUP -> {
                val binding = ItemCupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CupViewHolder(binding, onClick)
            }
            C.ViewType.ADD -> {
                val binding = ItemCupAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddViewHolder(binding, onClickAdd)
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder is CupViewHolder) {
            holder.onBind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount - 1) C.ViewType.ADD.viewType
        else C.ViewType.CUP.viewType
    }

    class CupViewHolder(private val binding: ItemCupBinding, listener: (Int) -> Unit): ViewHolder(binding.root) {
        init {
            binding.ivCup.setOnClickListener {
                listener(adapterPosition)
            }
        }
        fun onBind(item: Cup) {
            binding.tvName.text = item.cupName
        }
    }

    class AddViewHolder(binding: ItemCupAddBinding, listener: () -> Unit): ViewHolder(binding.root) {
        init {
            binding.ivAdd.setOnClickListener {
                listener()
            }
        }
    }
}