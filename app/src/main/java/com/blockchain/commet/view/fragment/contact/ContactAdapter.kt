package com.blockchain.commet.view.fragment.contact

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blockchain.commet.R
import com.blockchain.commet.databinding.ItemContactBinding
import com.solana.models.buffer.ContactModel

class ContactAdapter(val onItemClick: (ContactModel,Boolean) -> Unit) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var list = mutableListOf<ContactModel>()
    private var type = false
    private lateinit var context: Context

    fun setData(list: List<ContactModel>, context: Context, type: Boolean = false) {
        this.list.clear()
        this.list.addAll(list)
        this.context = context
        this.type = type
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ContactViewHolder {
        val binding =
            ItemContactBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ContactViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(viewHolder: ContactViewHolder, position: Int) {
        viewHolder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ContactViewHolder(
        private val binding: ItemContactBinding,
        val onItemClick: (ContactModel,Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: ContactModel) {
            binding.txtName.text = item.user_name
            binding.txtAddress.text = item.base_pubkey
            if (type){
                binding.imgAction.setImageDrawable(context.resources.getDrawable(R.drawable.cancel))
            } else {
                binding.imgAction.setImageDrawable(context.resources.getDrawable(R.drawable.add))
            }
            binding.imgAction.setOnClickListener {
                onItemClick(item,type)
            }
        }
    }
}