package com.blockchain.commet.view.fragment.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blockchain.commet.R
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentDetailTransactionBinding
import com.blockchain.commet.util.gone
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.util.show
import com.blockchain.commet.util.toast
import com.example.mysolana.contact.GetTransactionComponent
import com.example.mysolana.contact.GetTransactionInterface
import com.solana.models.buffer.GetTransactionModel

class TransactionFragment : BaseFragment() , GetTransactionInterface {

    private lateinit var binding: FragmentDetailTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailTransactionBinding.inflate(inflater, container, false)
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
        GetTransactionComponent(this).getTransaction(arguments?.getString("sign"))
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun getTransactionInterfaceResult(hasError: Boolean, data: GetTransactionModel?) {
        if (!hasError){
            requireActivity().runOnUiThread {
                try {
                    val before =
                        ((data?.result?.meta?.preBalances?.get(0))?.toFloat()!! / 1000000000).toBigDecimal()
                    val after =
                        ((data.result?.meta?.postBalances?.get(0))?.toFloat()!! / 1000000000).toBigDecimal()
                    val address = (data.result.transaction.message.accountKeys.get(1)).toString()
                    binding.apply {
                        txtDate.text = arguments?.getString("date")
                        txtAddress.text = address.substring(0,5) + "..." + address.substring(40,44)
                        txtFee.text = (data.result.meta.fee.toFloat() / 1000000000).toBigDecimal().toString()
                        if (after > before) {
                            imageView8.setImageDrawable(requireContext().resources.getDrawable(R.drawable.deposit))
                            lblSol.setTextColor(requireContext().resources.getColor(R.color.Deposit))
                            txtSol.setTextColor(requireContext().resources.getColor(R.color.Deposit))
                            lblType.setTextColor(requireContext().resources.getColor(R.color.Deposit))
                            lblType.text = "Deposit"
                            txtSol.text = " + " + (after - before).toString()
                            lblAddress.text = "From"
                        } else {
                            imageView8.setImageDrawable(requireContext().resources.getDrawable(R.drawable.withdrow))
                            lblSol.setTextColor(requireContext().resources.getColor(R.color.Withdraw))
                            txtSol.setTextColor(requireContext().resources.getColor(R.color.Withdraw))
                            lblType.setTextColor(requireContext().resources.getColor(R.color.Withdraw))
                            lblType.text = "Withdrow"
                            txtSol.text = " - " + (before - after).toString()
                            lblAddress.text = "To"
                        }
                    }
                    changeUI()
                } catch(e: Exception) {
                    toast("Connection Error")
                    binding.loading.visibility = View.GONE
                    requireActivity().onBackPressed()
                }
            }
        } else {
            requireActivity().runOnUiThread {
                toast("Connection Error")
                binding.loading.visibility = View.GONE
                requireActivity().onBackPressed()
            }
        }
    }

    private fun changeUI(){
        binding.apply {
            imageView8.show()
            lblType.show()
            lblDate.show()
            lblBalance.show()
            lblSol.show()
            txtDate.show()
            txtSol.show()
            view2.show()
            view3.show()
            view4.show()
            view5.show()
            lblAddress.show()
            lblState.show()
            lblNetwork.show()
            lblFee.show()
            txtState.show()
            txtFee.show()
            txtAddress.show()
            loading.gone()
        }
    }
}