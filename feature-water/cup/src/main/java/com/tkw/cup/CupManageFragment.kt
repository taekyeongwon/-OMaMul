package com.tkw.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.common.ItemTouchHelperCallback
import com.tkw.common.OnItemDrag
import com.tkw.common.autoCleared
import com.tkw.domain.model.Cup
import com.tkw.ui.DividerDecoration
import com.tkw.cup.adapter.CupListAdapter
import com.tkw.cup.databinding.FragmentCupManageBinding
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

    private fun initView() {
        cupListAdapter = CupListAdapter(
            adapterEditListener,
            adapterDeleteListener,
            object : OnItemDrag<Cup> {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {}
                override fun onStopDrag(list: List<Cup>) {
                    viewModel.updateAll(list)
                }
            }
        )
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(cupListAdapter, true))

        dataBinding.rvCupList.apply {
            adapter = cupListAdapter
            addItemDecoration(DividerDecoration(10f))
        }
    }

    private fun initObserver() {
        viewModel.cupListLiveData.observe(viewLifecycleOwner) {
            cupListAdapter.submitList(it) {
                dataChanged()
                manageItemTouchHelper()
            }
        }
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            findNavController().navigate(CupManageFragmentDirections
                .actionCupManageFragmentToCupCreateFragment(null))
        }

        dataBinding.btnReorder.setOnClickListener {
            findNavController().navigate(CupManageFragmentDirections
                .actionCupManageFragmentToCupListEditFragment())
        }
    }

    private val adapterEditListener: (Int) -> Unit = { position ->
        val currentItem = cupListAdapter.currentList[position]
            .apply { this.createMode = false}
        findNavController().navigate(CupManageFragmentDirections
            .actionCupManageFragmentToCupCreateFragment(currentItem))
    }

    private val adapterDeleteListener: (Int) -> Unit = { position ->
        val currentItem = cupListAdapter.currentList[position]
        viewModel.deleteCup(currentItem.cupId)
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
            if(cupListAdapter.itemCount > 1) View.VISIBLE
            else View.GONE
    }

    private fun manageItemTouchHelper() {
        if(cupListAdapter.itemCount > 1) {
            itemTouchHelper.attachToRecyclerView(dataBinding.rvCupList)
        } else {
            itemTouchHelper.attachToRecyclerView(null)
        }
    }
}