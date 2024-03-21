package com.tkw.omamul.ui.view.water.cup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.databinding.ItemManagedCupBinding

class CupListAdapter(
    private val editListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit
): ListAdapter<Cup, CupListAdapter.CupListViewHolder>(CupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CupListViewHolder {
        val binding = ItemManagedCupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CupListViewHolder(binding, editListener, deleteListener)
    }

    override fun onBindViewHolder(holder: CupListViewHolder, position: Int) {
        holder.onBind(getItem(position))
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
}