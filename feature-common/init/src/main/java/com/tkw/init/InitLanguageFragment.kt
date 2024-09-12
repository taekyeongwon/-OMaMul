package com.tkw.init

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.common.LocaleHelper
import com.tkw.common.PermissionHelper
import com.tkw.common.autoCleared
import com.tkw.init.databinding.FragmentInitLanguageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class InitLanguageFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitLanguageBinding>()
    private val viewModel: InitViewModel by activityViewModels()

    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>

    private var isGrantNotificationPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
                PermissionHelper.getPermissionResultCallback(requireActivity())
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentInitLanguageBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        initListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
//        callback.remove()
    }

    private fun initView() {
        requestAppPermissions()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect {
                if(it is InitContract.State.Complete) {
                    viewModel.setEvent(InitContract.Event.SaveAlarmEnableFlag(isGrantNotificationPermission))
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sideEffect.collect {
                if(it is InitContract.SideEffect.OnMoveNext) {
                    requireActivity().recreate()
                    findNavController().navigate(R.id.initTimeFragment)
                }
            }
        }

    }

    private fun initListener() {
        dataBinding.rgLanguage.setOnCheckedChangeListener { group, checkedId ->
            val lang = when(checkedId) {
                R.id.rb_ko -> Locale.KOREAN.language
                R.id.rb_en -> Locale.ENGLISH.language
                R.id.rb_jp -> Locale.JAPANESE.language
                R.id.rb_cn -> Locale.CHINESE.language
                else -> Locale.KOREAN.language
            }
            LocaleHelper.setApplicationLocales(lang)
        }

        dataBinding.btnNext.setOnClickListener {
            if(dataBinding.rgLanguage.checkedRadioButtonId != -1) {
                val lang = when (dataBinding.rgLanguage.checkedRadioButtonId) {
                    R.id.rb_ko -> Locale.KOREAN.language
                    R.id.rb_en -> Locale.ENGLISH.language
                    R.id.rb_jp -> Locale.JAPANESE.language
                    R.id.rb_cn -> Locale.CHINESE.language
                    else -> Locale.KOREAN.language
                }
                viewModel.setEvent(InitContract.Event.SaveLanguage(lang))
            }
        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

        }
    }

    private fun requestAppPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionHelper.requestPerms(
                context = requireActivity(),
                perms = arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                ),
                permissionResultLauncher = permissionResultLauncher,
                grantAction = {
                    isGrantNotificationPermission = true
                },
                cancelAction = {
                    isGrantNotificationPermission = false
                }
            )
        }
    }
}