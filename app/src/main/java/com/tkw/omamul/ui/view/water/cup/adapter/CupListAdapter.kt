package com.tkw.omamul.ui.view.water.cup.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tkw.omamul.common.C
import com.tkw.omamul.common.ItemMoveListener
import com.tkw.omamul.common.OnItemDrag
import com.tkw.model.Cup
import com.tkw.omamul.databinding.ItemCupListEditBinding
import com.tkw.omamul.databinding.ItemManagedCupBinding

class CupListAdapter(
    private val editListener: (Int) -> Unit = {},
    private val deleteListener: (Int) -> Unit = {},
    private val dragListener: OnItemDrag? = null
): ListAdapter<Cup, RecyclerView.ViewHolder>(CupDiffCallback()),
    ItemMoveListener {

    private var draggable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(C.CupListViewType.values()[viewType]) {
            C.CupListViewType.NORMAL -> {
                val binding = ItemManagedCupBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CupListViewHolder(binding, editListener, deleteListener)
            }
            C.CupListViewType.DRAG -> {
                val binding = ItemCupListEditBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CupListEditViewHolder(binding, dragListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is CupListViewHolder -> holder.onBind(getItem(position))
            is CupListEditViewHolder -> holder.onBind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(draggable) C.CupListViewType.DRAG.viewType
        else C.CupListViewType.NORMAL.viewType
    }

    override fun onItemMove(from: Int, to: Int) {
        val current = currentList[from]
        submitList(currentList.toMutableList().apply {
            removeAt(from)
            add(to, current)
        })
    }

    override fun onStopDrag() {
        dragListener?.onStopDrag(currentList)
    }

    fun setDraggable(isDraggable: Boolean) {
        draggable = isDraggable
    }

    class CupListViewHolder(
        val binding: ItemManagedCupBinding,
        editListener: (Int) -> Unit,
        deleteListener: (Int) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
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

    @SuppressLint("ClickableViewAccessibility")
    class CupListEditViewHolder(
        val binding: ItemCupListEditBinding,
        dragListener: OnItemDrag?
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                ibDrag.setOnTouchListener { v, event ->
                    if(event.action == MotionEvent.ACTION_DOWN) {
                        dragListener?.onStartDrag(this@CupListEditViewHolder)
                    }
                    false
                }
            }
        }

        fun onBind(data: Cup) {
            binding.cup = data
        }
    }
}