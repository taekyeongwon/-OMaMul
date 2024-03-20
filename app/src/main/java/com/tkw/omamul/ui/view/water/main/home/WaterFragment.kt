package com.tkw.omamul.ui.view.water.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentWaterBinding
import com.tkw.omamul.ui.dialog.WaterIntakeDialog
import com.tkw.omamul.ui.view.water.main.home.adapter.CupPagerAdapter
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.common.util.DimenUtils
import com.tkw.omamul.data.model.Water


class WaterFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterBinding>()
    private val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }
    private var countObject: List<Water>? = null
    private lateinit var cupPagerAdapter: CupPagerAdapter

    //컵 관리 화면 이동 후 돌아왔을 때 위치 저장용
    private var cupPagerScrollPosition: Int = 0

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
        initViewPager()
        initItemMenu()
    }

    private fun initObserver() {
        viewModel.countStreamLiveData.observe(viewLifecycleOwner) {
            countObject = it.toMap().dayOfList
        }

        viewModel.cupListLiveData.observe(viewLifecycleOwner) {
            cupPagerAdapter.submitList(it) {
                dataBinding.vpList.doOnLayout { snapSavedPosition() }
            }
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

    /**
     * PageTransformer
     * 현재 선택된 page기준 position은 0
     * offsetPx -> 화면 가로 픽셀에서 카드뷰+마진 제외한 만큼의 길이
     * 각 page 포지션 값에 -offsetPx 곱한 만큼 옮긴다.
     */
    private fun initViewPager() {
        cupPagerAdapter = CupPagerAdapter(clickScrollListener, addListener)

        val pageMarginPx = DimenUtils.dpToPx(requireContext(), 10)
        val pagerWidth = DimenUtils.dpToPx(requireContext(), 100)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        dataBinding.vpList.apply {
            adapter = cupPagerAdapter
            offscreenPageLimit = 3
            setPageTransformer { page, position ->
                page.translationX = position * -offsetPx
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

    private val clickScrollListener: (Int) -> Unit = { position ->
        scrollToPosition(position, true)
    }

    private val addListener = {
        val lastPosition = cupPagerAdapter.itemCount - 1
        if(dataBinding.vpList.currentItem == lastPosition) {
            //add버튼 선택했고, snap된 상태면 관리화면 이동
            findNavController().navigate(R.id.cupManageFragment)
        } else {
            //snap되지 않은 상태면 맨 마지막으로 스크롤
            scrollToPosition(lastPosition, true)
        }
    }

    private fun scrollToPosition(position: Int, smoothFlag: Boolean) {
        cupPagerScrollPosition = position
        dataBinding.vpList.setCurrentItem(position, smoothFlag)
    }

    private fun snapSavedPosition() {
        if(cupPagerAdapter.itemCount > 1)
            scrollToPosition(cupPagerScrollPosition, true)
    }
}