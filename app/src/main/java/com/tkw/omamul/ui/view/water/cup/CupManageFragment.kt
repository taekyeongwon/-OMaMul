package com.tkw.omamul.ui.view.water.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.tkw.omamul.common.ItemTouchHelperCallback
import com.tkw.omamul.common.OnItemDragStop
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.common.getViewModelFactory
import com.tkw.omamul.data.model.Draggable
import com.tkw.omamul.databinding.FragmentCupManageBinding
import com.tkw.omamul.ui.custom.DividerDecoration
import com.tkw.omamul.ui.view.water.cup.adapter.CupListAdapter

class CupManageFragment: Fragment(), OnItemDragStop {
    private var dataBinding by autoCleared<FragmentCupManageBinding>()
    private val viewModel: CupViewModel by viewModels { getViewModelFactory(null) }
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
        cupListAdapter = CupListAdapter(adapterEditListener, adapterDeleteListener, this)
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(cupListAdapter))

        dataBinding.rvCupList.apply {
            adapter = cupListAdapter
            addItemDecoration(DividerDecoration(10f))
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun initObserver() {
        viewModel.cupListLiveData.observe(viewLifecycleOwner) {
            cupListAdapter.submitList(it)
        }
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            findNavController().navigate(CupManageFragmentDirections
                .actionCupManageFragmentToCupCreateFragment(null))
        }
    }

    override fun onStopDrag(list: List<Draggable>) {

    }

    private val adapterEditListener: (Int) -> Unit = { position ->
        val currentItem = cupListAdapter.currentList[position]
        findNavController().navigate(CupManageFragmentDirections
            .actionCupManageFragmentToCupCreateFragment(currentItem))
    }

    private val adapterDeleteListener: (Int) -> Unit = { position ->
        val currentItem = cupListAdapter.currentList[position]
        viewModel.deleteCup(currentItem.cupId)
    }
}