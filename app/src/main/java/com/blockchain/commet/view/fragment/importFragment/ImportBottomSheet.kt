package com.blockchain.commet.view.fragment.importFragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blockchain.commet.databinding.BottomsheetImportBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImportBottomSheet (
    val privateKey: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetImportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetImportBinding.inflate(inflater, container, false)
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
            txtPriceRefundButton.text = "import"
            txtPriceRefundButton.visibility = View.VISIBLE
            btnRefund.setOnClickListener {
                if (binding.edtPrivateKey.text.isNullOrEmpty()) {
                    Toast.makeText(context, "Empty!!!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                privateKey(binding.edtPrivateKey.text.toString().trim())
                dismissAllowingStateLoss()
            }
        }
    }
}