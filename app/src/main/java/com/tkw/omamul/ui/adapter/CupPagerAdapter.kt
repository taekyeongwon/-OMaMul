package com.tkw.omamul.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.databinding.ItemCupAddBinding
import com.tkw.omamul.databinding.ItemCupBinding

class CupPagerAdapter(private val addListener: OnAddListener)
    : ListAdapter<CupEntity, ViewHolder>(DiffCallback()) {
    enum class ViewType(val viewType: Int) {
        CUP(0), ADD(1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(ViewType.values()[viewType]) {
            ViewType.CUP -> {
                val binding = ItemCupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CupViewHolder(binding)
            }
            ViewType.ADD -> {
                val binding = ItemCupAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddViewHolder(binding, addListener)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position < itemCount - 1) (holder as CupViewHolder).onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount - 1) ViewType.ADD.viewType
        else ViewType.CUP.viewType
    }

    class CupViewHolder(private val binding: ItemCupBinding): ViewHolder(binding.root) {
        fun onBind(item: CupEntity) {
            binding.tvName.text = item.cupName
        }
    }

    class AddViewHolder(binding: ItemCupAddBinding, listener: OnAddListener): ViewHolder(binding.root) {
        init {
            binding.ivAdd.setOnClickListener {
                listener.onClick()
            }
        }
    }
}