package com.tkw.alarm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.alarm.adapter.AlarmListAdapter
import com.tkw.alarm.databinding.FragmentAlarmModeCustomBinding
import com.tkw.alarm.dialog.AlarmTimeBottomDialog
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.model.Alarm
import com.tkw.ui.ItemTouchHelperCallback
import com.tkw.ui.OnItemDrag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalTime

@AndroidEntryPoint
class AlarmModeCustomFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModeCustomBinding>()
    private lateinit var alarmListAdapter: AlarmListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val adapterEditListener: (Int) -> Unit = { position ->
        val currentAlarm = alarmListAdapter.currentList[position]
        showTimeDialog(currentAlarm)
    }

    private val deleteCheckListener: (Int, Boolean) -> Unit = { position, isChecked ->

    }

    private val adapterLongClickListener: (Int) -> Unit = { position ->

    }

    private val dragListener = object: OnItemDrag<Alarm> {
        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
            itemTouchHelper.startDrag(viewHolder)
        }

        override fun onStopDrag(list: List<Alarm>) {

        }
    }

    private val positionObserver = object: RecyclerView.AdapterDataObserver() {
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            if(fromPosition == 0 || toPosition == 0) {
                dataBinding.rvAlarm.scrollToPosition(0)
            }
        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAlarmModeCustomBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
        initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alarmListAdapter.unregisterAdapterDataObserver(positionObserver)
    }

    private fun initView() {
        dataBinding.alarmWeek.setPeriodLayoutVisibility(false)  //커스텀 화면은 알람 간격 안보이게 처리
        initAdapter()
        initRecyclerView()
    }

    private fun initObserver() {

    }

    private fun initListener() {
        dataBinding.btnAdd.setOnClickListener {
            //새로 추가할 알람 객체 생성
            val currentTime = System.currentTimeMillis()
            val alarmId = DateTimeUtils.getTimeHHmm(currentTime)
            val alarm = Alarm(
                alarmId,
                currentTime,
                true
            )
            showTimeDialog(alarm)
        }

        dataBinding.ivEdit.setOnClickListener {

        }
    }

    private fun initAdapter() {
        alarmListAdapter = AlarmListAdapter(
            editListener = adapterEditListener,
            deleteCheckListener = deleteCheckListener,
            longClickListener = adapterLongClickListener,
            dragListener = dragListener
        )
        alarmListAdapter.registerAdapterDataObserver(positionObserver)

    }

    private fun initRecyclerView() {
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(alarmListAdapter, false))
        dataBinding.rvAlarm.apply {
            adapter = alarmListAdapter
            itemTouchHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
        }
    }

    private fun dataChanged() {
        if(alarmListAdapter.itemCount == 0) {
            dataBinding.rvAlarm.visibility = View.GONE
            dataBinding.tvEmptyAlarm.visibility = View.VISIBLE
        } else {
            dataBinding.rvAlarm.visibility = View.VISIBLE
            dataBinding.tvEmptyAlarm.visibility = View.GONE
        }
        dataBinding.ivEdit.visibility =
            if (alarmListAdapter.itemCount > 1) View.VISIBLE
            else View.INVISIBLE
    }

    private fun modeChanged(isModified: Boolean) {
        alarmListAdapter.setDraggable(isModified)
        if(isModified) {
            setDeleteBtnVisibility()
            dataBinding.btnAdd.visibility = View.GONE
            dataBinding.ivEdit.visibility = View.GONE
        } else {
            dataBinding.btnDelete.visibility = View.GONE
            dataBinding.btnAdd.visibility = View.VISIBLE
            dataBinding.ivEdit.visibility = View.VISIBLE
        }
    }

    private fun setDeleteBtnVisibility() {
        alarmListAdapter.currentList
            .count { it.isChecked }
            .also {
                dataBinding.btnDelete.visibility =
                    if(it > 0) View.VISIBLE
                    else View.INVISIBLE
            }
    }

    private fun clearChecked() {
        alarmListAdapter.currentList
            .forEach {
                it.isChecked = false
            }
    }

    private fun showTimeDialog(alarm: Alarm) {
        val dialog = AlarmTimeBottomDialog(
            selectedStart = LocalTime.now(),
            resultListener = { start, end ->
                //매개변수로 받은 alarm에 선택한 시간으로 뷰모델 setAlarm 할 수 있도록 구현
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
        lifecycleScope.launch {
            dialog.withResumed {
                dialog.setRadioButtonVisibility(false)
            }
        }
    }
}