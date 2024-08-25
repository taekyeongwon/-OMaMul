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
    private val deleteCheckListener: (Int, Boolean) -> Unit = { _, _ -> },
    private val longClickListener: (Int) -> Unit = {},
    private val dragListener: OnItemDrag<Alarm>? = null,
    private val alarmOnOffListener: (Int, Boolean) -> Unit = { _, _ -> }
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
                AlarmListViewHolder(binding, editListener, longClickListener, alarmOnOffListener)
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
        notifyDataSetChanged()
    }

    class AlarmListViewHolder(
        private val binding: ItemAlarmBinding,
        editListener: (Int) -> Unit,
        longClickListener: (Int) -> Unit,
        alarmOnOffListener: (Int, Boolean) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                root.setOnLongClickListener {
                    longClickListener(adapterPosition)
                    return@setOnLongClickListener true
                }
                root.setOnClickListener {
                    editListener(adapterPosition)
                }
                svSwitch.setCheckedChangeListener { _, isChecked ->
                    if(binding.svSwitch.getChecked() != alarm?.enabled) {   //toggle로 변경한 후 submit되어 onBind() 다시 불릴 때 중복으로 리스너 값 전달하지 않도록
                        if (alarm?.weekList?.isNotEmpty() == true) {
                            alarmOnOffListener(adapterPosition, isChecked)
                        } else {
                            binding.svSwitch.setChecked(false)
                        }
                    }
                }
            }
        }

        fun onBind(alarm: Alarm) {
            binding.alarm = alarm
            if(alarm.enabled) {
                binding.root.alpha = 1f
            } else {
                binding.root.alpha = 0.5f
            }
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