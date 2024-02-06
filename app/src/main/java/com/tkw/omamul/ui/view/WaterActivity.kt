package com.tkw.omamul.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.ActivityWaterBinding
import com.tkw.omamul.ui.viewmodel.WaterViewModel

class WaterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWaterBinding
    private val viewModel: WaterViewModel by viewModels { ViewModelFactory }
    private var countObject: List<WaterEntity>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            viewModel.addCount()
        }

        binding.btnRemove.setOnClickListener {
            if(!countObject.isNullOrEmpty()) {
                viewModel.removeCount(countObject!!.last())
            }
        }

//        viewModel.countLiveData.observe(this, Observer {
//            binding.tvCount.text = "$it"
//        })
        viewModel.countStreamLiveData.observe(this, Observer {
            countObject = it.dayOfList
            binding.tvCount.text = "${
                it.dayOfList.sumOf { water ->
                    water.amount
                }
            }"
        })

//        viewModel.getCount()
    }
}