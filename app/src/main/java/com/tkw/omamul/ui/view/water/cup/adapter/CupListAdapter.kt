package com.tkw.omamul.ui.view.water.cup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tkw.omamul.common.C
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.databinding.ItemManagedCupAddBinding
import com.tkw.omamul.databinding.ItemManagedCupBinding
import com.tkw.omamul.ui.view.water.main.home.adapter.CupDiffCallback

class CupListAdapter(
    private val editListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit,
    private val addListener: () -> Unit
): ListAdapter<Cup, ViewHolder>(CupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(C.ViewType.values()[viewType]) {
            C.ViewType.CUP -> {
                val binding = ItemManagedCupBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CupListViewHolder(binding, editListener, deleteListener)
            }
            C.ViewType.ADD -> {
                val binding = ItemManagedCupAddBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AddViewHolder(binding, addListener)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder is CupListViewHolder) holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount - 1) C.ViewType.ADD.viewType
        else C.ViewType.CUP.viewType
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    class CupListViewHolder(
        val binding: ItemManagedCupBinding,
        editListener: (Int) -> Unit,
        deleteListener: (Int) -> Unit
    ): ViewHolder(binding.root) {
        init {
            with(binding) {
                ibEdit.setOnClickListener { editListener(adapterPosition) }
                ibDelete.setOnClickListener { deleteListener(adapterPosition) }
            }
        }

        fun onBind(data: Cup) {
            binding.cup = data
        }
    }

    class AddViewHolder(
        binding: ItemManagedCupAddBinding,
        addListener: () -> Unit
    ): ViewHolder(binding.root) {
        init {
            binding.tvCupListAdd.setOnClickListener { addListener() }
        }
    }
}