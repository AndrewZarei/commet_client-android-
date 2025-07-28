package com.blockchain.commet.view.fragment.users

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.blockchain.commet.databinding.ItemUserBinding
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.QrVectorOptions
import com.solana.models.buffer.UserModel

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var list = mutableListOf<UserModel>()
    private lateinit var context: Context

    fun setData(list: ArrayList<UserModel>, context: Context) {
        this.list.clear()
        this.list.addAll(list)
        this.context = context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: UserViewHolder, position: Int) {
        viewHolder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserModel) {
            binding.apply {
                txtName.text = item.user_name
                txtAddress.text = item.user_address
                imgQR.setImageDrawable(QrCodeDrawable(QrData.Text(item.user_address),
                    QrVectorOptions.Builder().setPadding(.125f).build(), null))
                imgQR.setOnClickListener {
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                        .setPrimaryClip(ClipData.newPlainText("Address", binding.txtAddress.text))
                    Toast.makeText(context, "Copied!!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}