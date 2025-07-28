package com.blockchain.commet.view.fragment.transaction

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.blockchain.commet.R
import com.blockchain.commet.databinding.ItemTransactionHistoryBinding
import com.blockchain.commet.view.activity.MainActivity.Companion.start
import com.solana.models.buffer.GetSignaturesForAddressModel.getSignaturesForAddressModelData
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var list = ArrayList<getSignaturesForAddressModelData>()
    private lateinit var context: Context
    private lateinit var binding: ItemTransactionHistoryBinding

    fun setData(list: List<getSignaturesForAddressModelData>?, context: Context) {
        this.list.clear()
        this.list.addAll(list!!)
        this.context = context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TransactionViewHolder {
        binding =
            ItemTransactionHistoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return TransactionViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: TransactionViewHolder, position: Int) {
        viewHolder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: getSignaturesForAddressModelData) {
            binding.txtDate.text = SimpleDateFormat(" yyyy  , MMM dd").format(Date.from(Instant.ofEpochSecond(item.blockTime.toLong())).time)
            binding.lblSol.text = SimpleDateFormat("HH:mm").format(Date.from(Instant.ofEpochSecond(item.blockTime.toLong())).time)
            binding.layout.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("date", binding.txtDate.text.toString() + "  at  " + binding.lblSol.text.toString())
                bundle.putString("sign", item.signature)
                start(context, R.id.transactionFragment, bundle)
            }
        }
    }
}