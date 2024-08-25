package com.tkw.setting

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.Authentication
import com.tkw.domain.BackupManager
import com.tkw.domain.DriveAuthorize
import com.tkw.firebase.BackupForeground
import com.tkw.firebase.CloudStorage
import com.tkw.home.dialog.WaterIntakeDialog
import com.tkw.navigation.DeepLinkDestination
import com.tkw.navigation.deepLinkNavigateTo
import com.tkw.setting.databinding.FragmentSettingBinding
import com.tkw.setting.dialog.LanguageDialog
import com.tkw.setting.dialog.UnitDialog
import com.tkw.ui.dialog.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class WaterSettingFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentSettingBinding>()
    private val viewModel by activityViewModels<SettingViewModel>()

    @Inject
    lateinit var oAuth: Authentication

    @Inject
    lateinit var googleDrive: BackupManager

    @Inject
    lateinit var googleDriveAuth: DriveAuthorize<AuthorizationResult>

    private val googleDriveSyncLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it != null) {
                try {
                    val authorizationResult = Identity.getAuthorizationClient(requireActivity())
                        .getAuthorizationResultFromIntent(it.data)
                    startBackupService(false, authorizationResult.accessToken)
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }

    private val googleDriveUploadLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it != null) {
                try {
                    val authorizationResult = Identity.getAuthorizationClient(requireActivity())
                        .getAuthorizationResultFromIntent(it.data)
                    startBackupService(true, authorizationResult.accessToken)
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
        initListener()
    }

    override fun onStart() {
        super.onStart()
        updateUI(oAuth.isLoggedIn())
    }

    private fun initView() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WaterSettingFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initObserver() {
        viewModel.lastSync.observe(viewLifecycleOwner) {
            if(it == -1L) {
                dataBinding.settingInfo.tvLastSync.text = getString(com.tkw.ui.R.string.setting_last_sync_empty)
            } else {
                dataBinding.settingInfo.tvLastSync.text = DateTimeUtils.getDateTimeString(DateTimeUtils.getDateTimeFromMillis(it))
            }
        }
    }

    private fun initListener() {
        dataBinding.settingInfo.tvSync.setOnClickListener {
            doLogin()
        }
        dataBinding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
        dataBinding.settingInfo.ivSync.setOnClickListener {
//            cloudStorageUpload()
            viewModel.lastSync.value?.let {
                if(it == -1L) {
                    googleDriveStartSync()
                } else {
                    val currentTime = System.currentTimeMillis()
                    if(currentTime > it + 1000 * 60)
                        googleDriveUpload()
                }
            }

        }

        dataBinding.settingWater.clWaterSettingIntake.setOnClickListener {
            val dialog = WaterIntakeDialog()
            dialog.show(childFragmentManager, dialog.tag)
        }
        dataBinding.settingWater.clWaterSettingCup.setOnClickListener {
            findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Cup)
        }
        dataBinding.settingWater.clWaterSettingUnit.setOnClickListener {
            lifecycleScope.launch {
                val dialog = UnitDialog(viewModel.unitFlow.first())
                dialog.show(childFragmentManager, dialog.tag)
            }
        }

        dataBinding.settingAlarm.clAlarmSetting.setOnClickListener {
            findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Alarm)
        }

        dataBinding.settingEtc.clEtcSettingLanguage.setOnClickListener {
            lifecycleScope.launch {
                val dialog = LanguageDialog(viewModel.currentLangFlow.firstOrNull() ?: "")
                dialog.show(childFragmentManager, dialog.tag)
            }
        }
    }

    private fun updateUI(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            dataBinding.settingInfo.tvSync.text = oAuth.fetchInfo()?.name ?: "-"
            dataBinding.settingInfo.tvLastSync.visibility = View.VISIBLE
            dataBinding.settingInfo.ivSync.visibility = View.VISIBLE
            dataBinding.btnLogout.visibility = View.VISIBLE
        } else {
            dataBinding.settingInfo.tvSync.text = getString(com.tkw.ui.R.string.setting_sync)
            dataBinding.settingInfo.tvLastSync.visibility = View.GONE
            dataBinding.settingInfo.ivSync.visibility = View.GONE
            dataBinding.btnLogout.visibility = View.GONE
        }
        Glide
            .with(requireContext())
            .load(oAuth.fetchInfo()?.photoUrl)
            .placeholder(com.tkw.ui.R.drawable.account_circle)
            .error(com.tkw.ui.R.drawable.account_circle)
            .fallback(com.tkw.ui.R.drawable.account_circle)
            .apply(RequestOptions().circleCrop())
            .into(dataBinding.settingInfo.ivImage)
    }

    private fun doLogin() {
        oAuth.signOut()
        oAuth.signIn {
            if(it) {
                updateUI(true)
            }
        }
    }

    private fun showLogoutDialog() {
        val dialog = CustomDialog()
        dialog.show(childFragmentManager, dialog.tag)
        lifecycleScope.launch {
            dialog.withStarted {
                dialog.setTextView(getString(com.tkw.ui.R.string.setting_logout_confirm))
                dialog.setButtonListener(
                    confirmButtonTitle = getString(com.tkw.ui.R.string.ok),
                    cancelAction = {
                        dialog.dismiss()
                    },
                    confirmAction = {
                        dialog.dismiss()
                        oAuth.signOut()
                        updateUI(false)
                    }
                )
            }
        }
    }

    private fun cloudStorageUpload() {
        val storage = CloudStorage()
        val file = File(requireContext().filesDir, "default.realm")
        storage.upload(oAuth.fetchInfo()?.uId ?: "", file, file.name)
    }

    private fun googleDriveStartSync() {
        googleDriveAuth.authorize {
            it.onSuccess { result ->
                googleAuthResultHasResolution(
                    googleDriveSyncLauncher,
                    result
                ) {
                    startBackupService(false, result.accessToken)
                }
            }
        }
    }

    private fun googleDriveUpload() {
        googleDriveAuth.authorize {
            it.onSuccess { result ->
                googleAuthResultHasResolution(
                    googleDriveUploadLauncher,
                    result
                ) {
                    startBackupService(true, result.accessToken)
                }
            }
        }
    }

    private fun googleAuthResultHasResolution(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        result: AuthorizationResult,
        block: () -> Unit
    ) {
        if(result.hasResolution()) {
            val pendingIntent = result.pendingIntent
            try {
                val intent = IntentSenderRequest.Builder(pendingIntent!!.intentSender).build()
                launcher.launch(intent)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            } catch (npe: NullPointerException) {
                npe.printStackTrace()
            }
        } else {
            block()
        }
    }

    private fun startBackupService(isUpdate: Boolean, accessToken: String?) {
        Intent(requireActivity(), BackupForeground::class.java).apply {
            putExtra(BackupForeground.EXTRA_IS_UPDATE, isUpdate)
            putExtra(BackupForeground.EXTRA_ACCESS_TOKEN, accessToken)
            requireActivity().startForegroundService(this)
        }
    }

    private fun syncRotateStart(view: View): ObjectAnimator {
        val currentDegree = view.rotation
        val anim = ObjectAnimator.ofFloat(view, View.ROTATION, currentDegree, currentDegree + 360f)
            .setDuration(1000)
        anim.repeatCount = ValueAnimator.INFINITE
        anim.start()
        return anim
    }

    private fun syncRotateStop(animator: ObjectAnimator) {
        animator.cancel()
    }
}