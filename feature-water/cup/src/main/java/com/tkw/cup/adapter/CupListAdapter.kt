package com.tkw.cup.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tkw.base.C
import com.tkw.cup.databinding.ItemCupListBinding
import com.tkw.cup.databinding.ItemCupListEditBinding
import com.tkw.domain.model.Cup
import com.tkw.ui.ItemMoveListener
import com.tkw.ui.OnItemDrag

class CupListAdapter(
    private val editListener: (Int) -> Unit = {},
    private val deleteCheckListener: (Int, Boolean) -> Unit = {_, _ -> },
    private val longClickListener: (Int) -> Unit = {},
    private val dragListener: OnItemDrag<Cup>? = null
): ListAdapter<Cup, RecyclerView.ViewHolder>(CupDiffCallback()),
    ItemMoveListener {

    private var draggable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(C.CupListViewType.values()[viewType]) {
            C.CupListViewType.NORMAL -> {
                val binding = ItemCupListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CupListViewHolder(binding, editListener, longClickListener)
            }
            C.CupListViewType.DRAG -> {
                val binding = ItemCupListEditBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CupEditViewHolder(binding, deleteCheckListener, dragListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is CupListViewHolder -> holder.onBind(getItem(position))
            is CupEditViewHolder -> holder.onBind(getItem(position))
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
        private val binding: ItemCupListBinding,
        editListener: (Int) -> Unit,
        longClickListener: (Int) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                ibEdit.setOnClickListener { editListener(adapterPosition) }
                root.setOnLongClickListener {
                    longClickListener(adapterPosition)
                    return@setOnLongClickListener true
                }
            }
        }

        fun onBind(data: Cup) {
            binding.cup = data
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    class CupEditViewHolder(
        private val binding: ItemCupListEditBinding,
        deleteCheckListener: (Int, Boolean) -> Unit,
        dragListener: OnItemDrag<Cup>?
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                ibDrag.setOnTouchListener { v, event ->
                    if(event.action == MotionEvent.ACTION_DOWN) {
                        dragListener?.onStartDrag(this@CupEditViewHolder)
                    }
                    true
                }
                cbDelete.setOnCheckedChangeListener { buttonView, isChecked ->
                    deleteCheckListener(adapterPosition, isChecked)
                }
                root.setOnClickListener { cbDelete.isChecked = !cbDelete.isChecked }
            }
        }

        fun onBind(data: Cup) {
            binding.cup = data
        }
    }
}