package com.blockchain.commet.view.fragment.logs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blockchain.commet.data.database.Logs
import com.blockchain.commet.databinding.RecyclerviewLogsBinding

class AdapterLogs(var logs: ArrayList<Logs?>) : RecyclerView.Adapter<AdapterLogs.Holder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val binding = RecyclerviewLogsBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(logs[position]!!)
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    inner class Holder(private val binding: RecyclerviewLogsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Logs?){
            binding.apply {
                titleLogs.text = item?.name
                dateLogs.text = item?.date
                typeLogs.text = item?.type
                lognameLogs.text = item?.logName

            }
        }
    }
}