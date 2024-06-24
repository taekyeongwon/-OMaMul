package com.tkw.alarm.dialog

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.tkw.alarm.WaterAlarmViewModel
import com.tkw.alarm.databinding.DialogExactAlarmBinding
import com.tkw.common.autoCleared
import com.tkw.ui.dialog.CustomDialog

@RequiresApi(Build.VERSION_CODES.S)
class ExactAlarmDialog(
    private val doNotShowFlag: Boolean = false,
    private val cancelAction: () -> Unit = {},
    private val confirmAction: () -> Unit = {}
): CustomDialog() {
    private var dataBinding by autoCleared<DialogExactAlarmBinding>()
    private val viewModel: WaterAlarmViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DialogExactAlarmBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        setView(dataBinding.root)
        isCancelable = false
        setInvisibleCheckBox(doNotShowFlag)
        initResultLauncher()
    }


    private fun initListener() {
        setButtonListener(
            cancelButtonTitle = getString(com.tkw.ui.R.string.close),
            confirmButtonTitle = getString(com.tkw.ui.R.string.move),
            cancelAction = {
                //다시 보지 않기 여부 저장 -> action에 파라미터로 flag값 넘겨주면 밖에서 저장하는걸로.
                // 여기서 viewModel 제거하고 base모듈쪽으로 뺄 수 있음
                cancelAction()
                dismiss()
            },
            confirmAction = {
                //다시 보지 않기 여부 저장

                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${requireContext().packageName}")
                )
                resultLauncher.launch(intent)
            }
        )

    }

    private fun setInvisibleCheckBox(flag: Boolean) {
        dataBinding.clDontShowAgain.visibility =
            if (flag) View.INVISIBLE
            else View.VISIBLE
    }

    private fun initResultLauncher() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            confirmAction()
            dismiss()
        }
    }
}