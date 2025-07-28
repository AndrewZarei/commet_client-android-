package com.blockchain.commet.view.fragment.export

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.blockchain.commet.R
import com.blockchain.commet.databinding.BottomsheetExportBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

class ExportBottomSheet(
    val privateKey: String,
    val words: String
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetExportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetExportBinding.inflate(inflater, container, false)
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
            btnImportWallet.text = "BackUp!"
            title.text = "Private Key"
            title2.text = "Phrase Key"
            textView22.text = "Your Private Key"
            edtPrivateKey.text = privateKey
            title.setTextColor(resources.getColor(R.color.colorPrimary))
            title2.setTextColor(resources.getColor(R.color.grey))
            layoutWord.background = resources.getDrawable(R.drawable.shape_filter_deselect)
            layoutPrivateKey.background = resources.getDrawable(R.drawable.shape_filter_select)

            layoutWord.setOnClickListener {
                textView22.text = "Your Phrase Key"
                edtPrivateKey.text = words
                title.setTextColor(resources.getColor(R.color.grey))
                title2.setTextColor(resources.getColor(R.color.colorPrimary))
                layoutWord.background = resources.getDrawable(R.drawable.shape_filter_select)
                layoutPrivateKey.background = resources.getDrawable(R.drawable.shape_filter_deselect)

            }
            layoutPrivateKey.setOnClickListener {
                textView22.text = "Your Private Key"
                edtPrivateKey.text = privateKey
                title.setTextColor(resources.getColor(R.color.colorPrimary))
                title2.setTextColor(resources.getColor(R.color.grey))
                layoutWord.background = resources.getDrawable(R.drawable.shape_filter_deselect)
                layoutPrivateKey.background = resources.getDrawable(R.drawable.shape_filter_select)

            }

            txtCopyKey.setOnClickListener {
                (context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .setPrimaryClip(ClipData.newPlainText("Address", binding.edtPrivateKey.text))
                Toast.makeText(context, "Copied!!!", Toast.LENGTH_SHORT).show()
            }

            btnBackup.setOnClickListener {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("text/plain")
                intent.putExtra(Intent.EXTRA_TITLE, "SampleWallet.txt")
                startActivityForResult(intent, 101)
            }

        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("text/plain")
                intent.putExtra(Intent.EXTRA_TITLE, "SampleWallet.txt")
                startActivityForResult(intent, 101)
            } else {
//                Toast.makeText(context, "دسترسی دریافت نشد لطفا مجددا تلاش کنید!!", Toast.LENGTH_SHORT).show()
            }
        }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    writeInFile(data?.data!!)
                }

                Activity.RESULT_CANCELED -> {}
            }
        }
    }

    private fun writeInFile(uri: Uri) {
        val outputStream: OutputStream
        try {
            outputStream = context?.contentResolver?.openOutputStream(uri)!!
            val bw = BufferedWriter(OutputStreamWriter(outputStream))
            bw.write(privateKey)
            bw.flush()
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}