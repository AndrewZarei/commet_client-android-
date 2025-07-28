package com.blockchain.commet.view.fragment.withdraw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentWithdrawBinding
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.util.toast
import com.example.mysolana.contact.BalanceComponentInterface
import com.example.mysolana.withdrow.StateWithdraw
import com.example.mysolana.withdrow.WithdrawComponent
import com.example.mysolana.withdrow.WithdrawInterface
import java.math.BigDecimal

class WithdrawFragment : BaseFragment() , BalanceComponentInterface , WithdrawInterface {

    private lateinit var binding: FragmentWithdrawBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

//        BalanceComponent(this)
//            .getUserBalance(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(R.color.white)
    }

    override fun create(error: String?, userpublickey: String?) {
//        if (error.equals("false")){
//            requireActivity().runOnUiThread {
//                val result: String = (userpublickey?.toFloat()?.div(1000000000)).toString()
//                SharedPrefsHelper.getSharedPrefsHelper().put("walletbalance", result).toString()
//                binding.lblAmount.text = "$result"
//            }
//        } else {
//            requireActivity().runOnUiThread {
//                binding.lblAmount.text = "Connection Error"
//                binding.lblSol.text = ""
//            }
//        }
    }

    override fun sendWithdrawRequest(error: String?, stateWithdraw: StateWithdraw?) {
        if (error.equals("SUCCESS")) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Transfer Done!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupViews() {
        binding.apply {
            imgBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            txtAmountAvailable.text = requireArguments().getString("balance")
            btnSend.setOnClickListener {
                if (address.text.isNullOrEmpty() || amount.text.isNullOrEmpty()) toast("Please enter address & amount")
                else
                    WithdrawComponent(this@WithdrawFragment)
                        .sendWithdrawRequest(
                            SharedPrefsHelper.getSharedPrefsHelper().get("private_key"),
                            address.text.trim().toString(),
                            (BigDecimal(
                                amount.text.trim().toString()
                            ).multiply(BigDecimal("1000000000"))).toLong()
                        )
            }

        }
    }

}