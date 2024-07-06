package com.tkw.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.common.autoCleared
import com.tkw.cup.adapter.CupListAdapter
import com.tkw.cup.databinding.FragmentCupListEditBinding
import com.tkw.domain.model.Cup
import com.tkw.ui.DividerDecoration
import com.tkw.ui.ItemTouchHelperCallback
import com.tkw.ui.OnItemDrag
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback

@AndroidEntryPoint
class CupListEditFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentCupListEditBinding>()
    private val viewModel: CupViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<CupViewModel.AssistFactory> { factory ->
                factory.create(Cup())
            }
        }
    )
    private lateinit var cupListAdapter: CupListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val saveCupList: ArrayList<Cup> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentCupListEditBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cupListAdapter.unregisterAdapterDataObserver(positionObserver)
    }

    private fun initView() {
        initAdapter()
        initList()
        setRecyclerView()
    }

    private fun initListener() {
        dataBinding.btnDelete.setOnClickListener {
            saveCupList
                .filter { it.isChecked }
                .forEach {
                    viewModel.deleteCup(it.cupId)
                }
            findNavController().navigateUp()
        }
    }

    private fun initAdapter() {
        cupListAdapter = CupListAdapter(
            deleteCheckListener = { position, isChecked ->
                saveCupList[position].isChecked = isChecked

                setDeleteBtnVisibility()
            },
            dragListener = object : OnItemDrag<Cup> {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(viewHolder)
                }

                override fun onStopDrag(list: List<Cup>) {
                    saveCupList.clear()
                    saveCupList.addAll(list)
                    viewModel.updateAll(list)
                }
            }
        )
        cupListAdapter.setDraggable(true)
        cupListAdapter.registerAdapterDataObserver(positionObserver)
    }

    private fun initList() {
        val cupArgs: CupListEditFragmentArgs by navArgs()
        cupArgs.cupArgument?.let {
            val cupList = it.cupList
            saveCupList.addAll(cupList)
            cupListAdapter.submitList(cupList)
        }

        setDeleteBtnVisibility()
    }

    private fun setRecyclerView() {
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(cupListAdapter, false))

        dataBinding.rvCupList.apply {
            adapter = cupListAdapter
            addItemDecoration(DividerDecoration(10f))
            itemTouchHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
        }
    }

    private fun setDeleteBtnVisibility() {
        saveCupList
            .count { it.isChecked }
            .also {
                dataBinding.btnDelete.visibility =
                    if (it > 0) View.VISIBLE
                    else View.INVISIBLE
            }
    }

    private val positionObserver = object: RecyclerView.AdapterDataObserver() {
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            if(fromPosition == 0 || toPosition == 0) {
                dataBinding.rvCupList.scrollToPosition(0)
            }
        }
    }
}