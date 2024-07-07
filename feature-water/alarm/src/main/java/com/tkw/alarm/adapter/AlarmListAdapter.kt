package com.tkw.alarm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tkw.alarm.databinding.ItemAlarmBinding
import com.tkw.alarm.databinding.ItemAlarmEditBinding
import com.tkw.base.C
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.Cup
import com.tkw.ui.ItemMoveListener
import com.tkw.ui.OnItemDrag

class AlarmListAdapter(
    private val editListener: (Int) -> Unit = {},
    private val deleteCheckListener: (Int, Boolean) -> Unit = {_, _ -> },
    private val longClickListener: (Int) -> Unit = {},
    private val dragListener: OnItemDrag<Alarm>? = null
): ListAdapter<Alarm, RecyclerView.ViewHolder>(AlarmDiffCallback()),
    ItemMoveListener {

    private var draggable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(C.AlarmListViewType.values()[viewType]) {
            C.AlarmListViewType.NORMAL -> {
                val binding = ItemAlarmBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AlarmListViewHolder(binding, editListener, longClickListener)
            }
            C.AlarmListViewType.DRAG -> {
                val binding = ItemAlarmEditBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AlarmEditViewHolder(binding, deleteCheckListener, dragListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is AlarmListViewHolder -> holder.onBind(getItem(position))
            is AlarmEditViewHolder -> holder.onBind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(draggable) C.AlarmListViewType.DRAG.viewType
        else C.AlarmListViewType.NORMAL.viewType
    }

    override fun onItemMove(from: Int, to: Int) {
        val current = currentList[from]
        submitList(currentList.apply {
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

    class AlarmListViewHolder(
        private val binding: ItemAlarmBinding,
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

        fun onBind(alarm: Alarm) {
            binding.alarm = alarm
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    class AlarmEditViewHolder(
        private val binding: ItemAlarmEditBinding,
        deleteCheckListener: (Int, Boolean) -> Unit,
        dragListener: OnItemDrag<Alarm>?
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                ibDrag.setOnTouchListener { v, event ->
                    if(event.action == MotionEvent.ACTION_DOWN) {
                        dragListener?.onStartDrag(this@AlarmEditViewHolder)
                    }
                    true
                }
                cbDelete.setOnCheckedChangeListener { buttonView, isChecked ->
                    deleteCheckListener(adapterPosition, isChecked)
                }
                root.setOnClickListener { cbDelete.isChecked = !cbDelete.isChecked }
            }
        }

        fun onBind(alarm: Alarm) {
            binding.alarm = alarm
        }
    }
}