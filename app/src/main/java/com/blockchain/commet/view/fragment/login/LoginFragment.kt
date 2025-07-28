package com.blockchain.commet.view.fragment.login

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.LoginFragBinding
import com.blockchain.commet.util.setCustomStatusBar
import com.blockchain.commet.view.activity.BaseActivity
import com.solana.Config

class LoginFragment : Fragment() {
    var binding: LoginFragBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_frag, container, false)
        return binding?.root!!
    }

    override fun onStart() {
        super.onStart()
        this.setCustomStatusBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (SharedPrefsHelper.getSharedPrefsHelper()["login", false]) {
            if (SharedPrefsHelper.getSharedPrefsHelper()["PASSWORD"].isEmpty()) {
                startActivity(Intent(context, BaseActivity::class.java))
                requireActivity().finish()
            } else {
                val bundle = Bundle()
                bundle.putBoolean("isFirst", false)
                findNavController(view).navigate(
                    R.id.action_loginFragment_to_passcodeFragment,
                    bundle
                )
            }
        }
        binding?.btnLogin?.setOnClickListener { v ->
            LoginBottomSheet(this).show(
                parentFragmentManager,
                "loginFragment"
            )
        }
        binding?.btnSetting?.setOnClickListener { v ->
            val dialog =
                AlertDialog.Builder(requireActivity())
            val inflater = this.layoutInflater
            val dialogView: View =
                inflater.inflate(R.layout.dialog_select_network, null)
            dialog.setView(dialogView)
            val main = dialogView.findViewById<TextView>(R.id.btn_main)
            val dev = dialogView.findViewById<TextView>(R.id.btn_dev)

            if (SharedPrefsHelper.getSharedPrefsHelper()["network"] == "Main") {
                main.setTextColor(resources.getColor(R.color.white))
                main.background = resources.getDrawable(R.drawable.btn_network)
                dev.setTextColor(resources.getColor(R.color.black))
                dev.background = resources.getDrawable(R.drawable.btn_network_null)
            } else {
                dev.setTextColor(resources.getColor(R.color.white))
                dev.background = resources.getDrawable(R.drawable.btn_network)
                main.setTextColor(resources.getColor(R.color.black))
                main.background = resources.getDrawable(R.drawable.btn_network_null)
            }

            main.setOnClickListener { view1: View? ->
                main.setTextColor(resources.getColor(R.color.white))
                main.background = resources.getDrawable(R.drawable.btn_network)
                dev.setTextColor(resources.getColor(R.color.black))
                dev.background = resources.getDrawable(R.drawable.btn_network_null)
                Config.network = "Main"
                SharedPrefsHelper.getSharedPrefsHelper().put("network", Config.network)
            }
            dev.setOnClickListener { view1: View? ->
                dev.setTextColor(resources.getColor(R.color.white))
                dev.background = resources.getDrawable(R.drawable.btn_network)
                main.setTextColor(resources.getColor(R.color.black))
                main.background = resources.getDrawable(R.drawable.btn_network_null)
                Config.network = "Dev"
                SharedPrefsHelper.getSharedPrefsHelper().put("network", Config.network)
            }
            dialog.setMessage("Please Select Your Network.").setPositiveButton(
                "OK"
            ) { dialogInterface: DialogInterface?, i: Int -> }
                .show()
        }
    }
}