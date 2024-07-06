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
import com.tkw.common.autoCleared
import com.tkw.cup.adapter.CupListAdapter
import com.tkw.cup.databinding.FragmentCupManageBinding
import com.tkw.domain.model.Cup
import com.tkw.domain.model.CupList
import com.tkw.ui.DividerDecoration
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
//    private lateinit var itemTouchHelper: ItemTouchHelper

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
            editListener = adapterEditListener,
//            adapterDeleteListener,
            longClickListener = adapterLongClickListener,
//            object : OnItemDrag<Cup> {
//                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {}
//                override fun onStopDrag(list: List<Cup>) {
//                    viewModel.updateAll(list)
//                }
//            }
        )
//        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(cupListAdapter, true))

        dataBinding.rvCupList.apply {
            adapter = cupListAdapter
            addItemDecoration(DividerDecoration(10f))
        }
    }

    private fun initObserver() {
        viewModel.cupListLiveData.observe(viewLifecycleOwner) {
            val list = ArrayList<Cup>()
            it.forEach {
                list.add(it.copy()) //copy()가 얕은 복사이나 Cup 파라미터가 모두 Primitive 타입이여서 사용함.
            }
            cupListAdapter.submitList(list) {
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
            val currentList = ArrayList<Cup>().apply {
                addAll(cupListAdapter.currentList)
            }
            findNavController().navigate(CupManageFragmentDirections
                .actionCupManageFragmentToCupListEditFragment(
                    CupList(
                        cupList = cupListAdapter.currentList
                    )
                ))
        }
    }

    private val adapterEditListener: (Int) -> Unit = { position ->
        val currentItem = cupListAdapter.currentList[position]
            .apply { this.createMode = false }
        findNavController().navigate(CupManageFragmentDirections
            .actionCupManageFragmentToCupCreateFragment(currentItem))
    }

//    private val adapterDeleteListener: (Int) -> Unit = { position ->
//        val currentItem = cupListAdapter.currentList[position]
//        viewModel.deleteCup(currentItem.cupId)
//    }

    private val adapterLongClickListener: (Int) -> Unit = { position ->
        val currentList = ArrayList<Cup>().apply {
            addAll(cupListAdapter.currentList)
        }
        cupListAdapter.currentList[position].isChecked = true
        findNavController().navigate(
            CupManageFragmentDirections
                .actionCupManageFragmentToCupListEditFragment(
                    CupList(
                        cupList = cupListAdapter.currentList
                    )
                )
        )
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
//        if(cupListAdapter.itemCount > 1) {
//            itemTouchHelper.attachToRecyclerView(dataBinding.rvCupList)
//        } else {
//            itemTouchHelper.attachToRecyclerView(null)
//        }
    }
}