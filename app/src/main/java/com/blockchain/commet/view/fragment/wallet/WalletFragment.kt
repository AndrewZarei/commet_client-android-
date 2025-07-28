package com.blockchain.commet.view.fragment.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentWalletBinding
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.util.toast
import com.blockchain.commet.view.activity.MainActivity
import com.blockchain.commet.view.fragment.transaction.TransactionAdapter
import com.example.mysolana.contact.BalanceComponent
import com.example.mysolana.contact.BalanceComponentInterface
import com.example.mysolana.contact.SignatureForAddressComponent
import com.solana.models.buffer.GetSignaturesForAddressModel
import com.solana.models.buffer.GetSignaturesForAddressModel.getSignaturesForAddressModelData

class WalletFragment : BaseFragment() , BalanceComponentInterface {

    private lateinit var binding: FragmentWalletBinding
    private var adapter = TransactionAdapter()

    companion object{
        var list = ArrayList<getSignaturesForAddressModelData>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        BalanceComponent(this)
            .getUserBalance(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(R.color.white)
    }

    override fun create(error: String?, userpublickey: String?) {
        if (error.equals("false")){
            requireActivity().runOnUiThread {
                val result: String = (userpublickey?.toFloat()?.div(1000000000)).toString()
                SharedPrefsHelper.getSharedPrefsHelper().put("walletbalance", result).toString()
                binding.lblAmount.text = "$result"
            }
        } else {
            requireActivity().runOnUiThread {
                binding.lblAmount.text = "Connection Error"
                binding.lblSol.text = ""
            }
        }
    }

    private fun setupViews() {
        binding.apply {
            btnDeposit.setOnClickListener { MainActivity.start(requireContext(), R.id.depositFragment, bundleOf("balance" to lblAmount.text.toString())) }
            btnWithdraw.setOnClickListener { MainActivity.start(requireContext(), R.id.withdrawFragment, bundleOf("balance" to lblAmount.text.toString())) }
            recycleHistory.layoutManager = LinearLayoutManager(requireContext())
            recycleHistory.adapter = adapter
            refresh.setOnRefreshListener {
                binding.loading.visibility = View.VISIBLE
                binding.recycleHistory.visibility = View.GONE
                BalanceComponent(this@WalletFragment)
                    .getUserBalance(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
                getData()
            }
            if (list.isEmpty()){
                getData()
            } else {
                binding.loading.visibility = View.GONE
                binding.recycleHistory.visibility = View.VISIBLE
                adapter.setData(list,requireContext())
            }
        }
    }

    private fun getData(){
        binding.refresh.isRefreshing = false
        SignatureForAddressComponent { error: Boolean, data: GetSignaturesForAddressModel? ->
            if (error){
                requireActivity().runOnUiThread {
                    list = data?.result as ArrayList<getSignaturesForAddressModelData>
                    binding.loading.visibility = View.GONE
                    binding.recycleHistory.visibility = View.VISIBLE
                    adapter.setData(list,requireContext())
                }
            } else {
                requireActivity().runOnUiThread { toast("Connection Error") }
            }
        }.getSignaturesForAddress(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
    }

}