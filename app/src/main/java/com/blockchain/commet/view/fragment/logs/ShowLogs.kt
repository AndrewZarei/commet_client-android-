package com.blockchain.commet.view.fragment.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blockchain.commet.data.database.DBHelper
import com.blockchain.commet.data.database.Logs
import com.blockchain.commet.databinding.ShowlogsBinding

class ShowLogs : Fragment() {
    private lateinit var binding: ShowlogsBinding
    private lateinit var adapterLogs: AdapterLogs
    private var customLogs = ArrayList<Logs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShowlogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews(){
        binding.apply {
            val logs = DBHelper(requireActivity()).GetLogs("DESC")
            for (i in logs.indices) {
                customLogs.add(Logs(logs[i]!!.getType(), logs[i]!!.getName(), logs[i]!!.getDate(), logs[i]!!.getLogName()))
            }
            recyclerviewLogs.setLayoutManager(LinearLayoutManager(requireActivity()))
            adapterLogs = AdapterLogs(customLogs as ArrayList<Logs?>)
            recyclerviewLogs.setAdapter(adapterLogs)
            radiogroup.setOnCheckedChangeListener { radioGroup, index ->
                when (index) {
                    (1) -> {
                        requireActivity().runOnUiThread(Runnable {
                            val logs = DBHelper(requireActivity()).GetLogs("ASC")
                            customLogs = ArrayList<Logs>()
                            for (i in logs.indices) {
                                customLogs.add(Logs(logs[i]!!.getType(), logs[i]!!.getName(), logs[i]!!.getDate(), logs[i]!!.getLogName()))
                            }
                            adapterLogs = AdapterLogs(customLogs as ArrayList<Logs?>)
                            recyclerviewLogs.setAdapter(adapterLogs)
                        })
                    }

                    (2) -> {
                        requireActivity().runOnUiThread(Runnable {
                            val logs = DBHelper(requireActivity()).GetLogs("DESC")
                            customLogs = ArrayList<Logs>()
                            for (i in logs.indices) {
                                customLogs.add(Logs(logs[i]!!.getType(), logs[i]!!.getName(), logs[i]!!.getDate(), logs[i]!!.getLogName()))
                            }
                            adapterLogs = AdapterLogs(customLogs as ArrayList<Logs?>)
                            recyclerviewLogs.setAdapter(adapterLogs)
                        })
                    }
                }
            }
        }
    }
}