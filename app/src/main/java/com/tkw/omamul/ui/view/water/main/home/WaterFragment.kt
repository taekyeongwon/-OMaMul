package com.tkw.omamul.ui.view.water.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentWaterBinding
import com.tkw.omamul.ui.dialog.WaterIntakeDialog
import com.tkw.omamul.ui.view.water.main.home.adapter.CupPagerAdapter
import com.tkw.omamul.ui.view.water.main.home.adapter.SnapDecoration
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.data.model.Water


class WaterFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterBinding>()
    private val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }
    private var countObject: List<Water>? = null
    private lateinit var cupPagerAdapter: CupPagerAdapter
    private lateinit var snapHelper: PagerSnapHelper

    private var i = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentWaterBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initView()
        initObserver()
        initListener()
    }

    private fun initBinding() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WaterFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initView() {
        snapHelper = PagerSnapHelper()
        cupPagerAdapter = CupPagerAdapter(clickScrollListener, addListener)
        dataBinding.rvList.apply {
            adapter = cupPagerAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            snapHelper.attachToRecyclerView(this)
        }
        initItemMenu()
        initGlobalLayout()
    }

    private fun initObserver() {
        viewModel.countStreamLiveData.observe(viewLifecycleOwner) {
            countObject = it.toMap().dayOfList
        }

        viewModel.cupListLiveData.observe(viewLifecycleOwner) {
            cupPagerAdapter.submitList(it) //{ snapFirstItemAdded() }
        }
    }

    private fun initListener() {
        dataBinding.btnAdd.setOnClickListener {
            viewModel.addCount(100, DateTimeUtils.getToday())
        }

        dataBinding.btnRemove.setOnClickListener {
            if(!countObject.isNullOrEmpty()) {
                viewModel.removeCount(countObject!!.last())
            }
        }
    }

    private fun initItemMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.waterIntakeDialog -> {
                        val dialog = WaterIntakeDialog()
                        dialog.show(childFragmentManager, dialog.tag)
                        true
                    }
                    else -> {
                        menuItem.onNavDestinationSelected(findNavController())
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initGlobalLayout() {
        dataBinding.root.let {
            it.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    it.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    dataBinding.rvList.addItemDecoration(SnapDecoration(dataBinding.root.width))
                }
            })
        }
    }

    private val clickScrollListener: (Int) -> Unit = { position ->
        scrollToPosition(dataBinding.rvList, position)
    }

    private val addListener = {
        val lastPosition = cupPagerAdapter.itemCount - 1
        val touchedView: View? = dataBinding.rvList.layoutManager!!.findViewByPosition(lastPosition)
        if(touchedView != null && isSnapped(touchedView)) {
            //add버튼 선택했고, snap된 상태면 관리화면 이동
            findNavController().navigate(R.id.cupManageFragment)
        } else {
            //snap되지 않은 상태면 맨 마지막으로 스크롤
            scrollToPosition(dataBinding.rvList, lastPosition)
        }
    }

    private fun scrollToPosition(rv: RecyclerView, position: Int) {
        val touchedView: View? = rv.layoutManager!!.findViewByPosition(position)
        if(touchedView != null) {
            val itemWidth = touchedView.measuredWidth
            val centerView = snapHelper.findSnapView(rv.layoutManager)
            val centerPosition = rv.getChildAdapterPosition(centerView!!)
            //클릭한 포지션에서 현재 snap된 포지션의 차이 증감분 만큼 이동
            rv.smoothScrollBy((position - centerPosition) * itemWidth, 0)
        }

    }

    private fun isSnapped(view: View): Boolean {
        val centerView = snapHelper.findSnapView(dataBinding.rvList.layoutManager)
        return view === centerView
    }

    private fun snapFirstItemAdded() {  //todo 화면 진입 시 시점 문제로 적용 안됨. 추후 수정 필요
        if(cupPagerAdapter.itemCount > 0)
            scrollToPosition(dataBinding.rvList, cupPagerAdapter.itemCount - 1)
    }
}