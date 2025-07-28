package com.blockchain.commet.view.fragment.showPrivateKey

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.ShowprivatekeyFragmentBinding
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.view.activity.BaseActivity

class ShowPrivateKeyFragment : Fragment() {

    private lateinit var binding: ShowprivatekeyFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShowprivatekeyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(R.color.white)
    }

    private fun setupViews() {
        binding.apply {

            showPrivate.text = SharedPrefsHelper.getSharedPrefsHelper()["private_key"]
            showPhrase.text = SharedPrefsHelper.getSharedPrefsHelper()["phrase"]

            cotinue.setOnClickListener { view1 ->
                startActivity(Intent(context, BaseActivity::class.java))
                requireActivity().finish()
            }

            showPrivate.setOnClickListener { view ->
                val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("", showPrivate.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireActivity(), "copy to clipboard", Toast.LENGTH_SHORT).show()
            }

            showPhrase.setOnClickListener { view ->
                val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("", showPhrase.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireActivity(), "copy to clipboard", Toast.LENGTH_SHORT).show()
            }

        }
    }
}