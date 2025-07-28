package com.blockchain.commet.view.fragment.changepasscode

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.BottomsheetChangePasscodeBinding
import com.blockchain.commet.util.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangePasscodeBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetChangePasscodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetChangePasscodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = STATE_EXPANDED
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    /***
     * setup Views of layout
     * set onClicks for views
     */
    private fun setupViews() {
        binding.apply {
            btnSearch.setOnClickListener {
                if (pass.text.length < 4){
                    pass.error = "please set 4 digit"
                } else {
                    SharedPrefsHelper.getSharedPrefsHelper().put("PASSWORD", pass.text.toString())
                    toast("Passcode Changes!!!")
                    this@ChangePasscodeBottomSheet.dismiss()
                }
            }
        }
    }
}