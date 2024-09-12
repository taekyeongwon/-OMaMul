package com.tkw.cup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.common.autoCleared
import com.tkw.cup.adapter.CupListAdapter
import com.tkw.cup.databinding.FragmentCupManageBinding
import com.tkw.domain.model.Cup
import com.tkw.ui.decoration.DividerDecoration
import com.tkw.ui.ItemTouchHelperCallback
import com.tkw.ui.OnItemDrag
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback

@AndroidEntryPoint
class CupManageFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentCupManageBinding>()
    private val viewModel: CupViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<CupViewModel.AssistFactory> { factory ->
                factory.create(Cup())
            }
        }
    )
    private lateinit var cupListAdapter: CupListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val draggedList: ArrayList<Cup> = ArrayList()

    private val adapterEditListener: (Int) -> Unit = { position ->
        val currentItem = cupListAdapter.currentList[position]
            .apply { this.createMode = false }
        findNavController().navigate(CupManageFragmentDirections
            .actionCupManageFragmentToCupCreateFragment(currentItem))
    }

    private val deleteCheckListener: (Int, Boolean) -> Unit = { position, isChecked ->
        cupListAdapter.currentList[position].isChecked = isChecked
        setDeleteBtnVisibility()
    }

    private val adapterLongClickListener: (Int) -> Unit = { position ->
        cupListAdapter.currentList[position].isChecked = true
        viewModel.setModifyMode(true)
    }

    private val dragListener = object : OnItemDrag<Cup> {
        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
            itemTouchHelper.startDrag(viewHolder)
        }

        override fun onStopDrag(list: List<Cup>) {
            draggedList.clear()
            draggedList.addAll(list)
            viewModel.updateAll(list)
        }
    }

    private val positionObserver = object: RecyclerView.AdapterDataObserver() {
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            if(fromPosition == 0 || toPosition == 0) {
                dataBinding.rvCupList.scrollToPosition(0)
            }
        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(viewModel.modifyMode.value == true) {
                clearChecked()
                viewModel.setModifyMode(false)
            } else {
                findNavController().navigateUp()
            }
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
        dataBinding = FragmentCupManageBinding.inflate(inflater, container, false)
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
        cupListAdapter.unregisterAdapterDataObserver(positionObserver)
    }

//    override fun onDetach() {
//        super.onDetach()
//        callback.remove()   //라이프사이클 소멸되면 자동으로 해제되므로 호출 안해도 됨.
//    }

    private fun initView() {
        cupListAdapter = CupListAdapter(
            editListener = adapterEditListener,
            deleteCheckListener = deleteCheckListener,
            longClickListener = adapterLongClickListener,
            dragListener = dragListener
        )
        cupListAdapter.registerAdapterDataObserver(positionObserver)

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(cupListAdapter, false))
        dataBinding.rvCupList.apply {
            adapter = cupListAdapter
            addItemDecoration(DividerDecoration(10f))
            itemTouchHelper.attachToRecyclerView(this)
//            setHasFixedSize(true)
        //            -> recyclerview visible gone일 때 submitList한 뒤에
        //            visible하게 바꾸므로 size를 고정하고 지속적으로 item이 변경 되는 것이 아니므로 true값이 의미가 없음.
        }
    }

    private fun initObserver() {
        viewModel.cupListLiveData.observe(viewLifecycleOwner) {
            val list = draggedList.ifEmpty { it }
            cupListAdapter.submitList(list.map { it.copy() }) {
                draggedList.clear()
                dataChanged()
            }
        }

        viewModel.modifyMode.observe(viewLifecycleOwner) {
            modeChanged(it)
        }

        viewModel.nextEvent.observe(viewLifecycleOwner) {
            viewModel.setModifyMode(false)
        }
    }

    private fun dataChanged() {
        if(cupListAdapter.itemCount == 0) {
            dataBinding.rvCupList.visibility = View.GONE
            dataBinding.tvEmptyCup.visibility = View.VISIBLE
        } else {
            dataBinding.rvCupList.visibility = View.VISIBLE
            dataBinding.tvEmptyCup.visibility = View.GONE
        }
        dataBinding.btnReorder.visibility =
            if(cupListAdapter.itemCount > 1 &&
                viewModel.modifyMode.value == false) View.VISIBLE
            else View.GONE
    }

    private fun modeChanged(isModified: Boolean) {
        cupListAdapter.setDraggable(isModified)
        if(isModified) {
            setDeleteBtnVisibility()
            dataBinding.btnNext.visibility = View.GONE
            dataBinding.btnReorder.visibility = View.GONE
        } else {
            dataBinding.btnDelete.visibility = View.GONE
            dataBinding.btnNext.visibility = View.VISIBLE
            dataBinding.btnReorder.visibility =
                if(cupListAdapter.itemCount > 1) View.VISIBLE
                else View.GONE
        }
    }

    private fun setDeleteBtnVisibility() {
        cupListAdapter.currentList
            .count { it.isChecked }
            .also {
                dataBinding.btnDelete.visibility =
                    if (it > 0) View.VISIBLE
                    else View.INVISIBLE
            }
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            findNavController().navigate(CupManageFragmentDirections
                .actionCupManageFragmentToCupCreateFragment(null))
        }

        dataBinding.btnReorder.setOnClickListener {
            viewModel.setModifyMode(true)
        }

        dataBinding.btnDelete.setOnClickListener {
            cupListAdapter.currentList
                .filter { it.isChecked }
                .forEach {
                    viewModel.deleteCup(it.cupId)
                }
        }
    }

    private fun clearChecked() {
        cupListAdapter.currentList
            .forEach {
                it.isChecked = false
            }
    }
}