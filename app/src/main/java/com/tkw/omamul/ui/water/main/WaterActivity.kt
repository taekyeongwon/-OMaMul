package com.tkw.omamul.ui.water.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.ActivityWaterBinding
import com.tkw.omamul.ui.base.BaseActivity

class WaterActivity : BaseActivity<ActivityWaterBinding, WaterViewModel>(R.layout.activity_water) {

    override val isSplash: Boolean = true
    override val viewModel:WaterViewModel by viewModels { ViewModelFactory }

    private var countObject: List<WaterEntity>? = null
    override fun initView() {
//        viewModel.getCount()
    }

    override fun initObserver() {
        viewModel.countStreamLiveData.observe(this, Observer {
            countObject = it.dayOfList
            dataBinding.tvCount.text = "${
                it.dayOfList.sumOf { water ->
                    water.amount
                }
            }"
        })
    }

    override fun initListener() {
        dataBinding.btnAdd.setOnClickListener {
            viewModel.addCount()
        }

        dataBinding.btnRemove.setOnClickListener {
            if(!countObject.isNullOrEmpty()) {
                viewModel.removeCount(countObject!!.last())
            }
        }
    }
}