package com.blockchain.commet.view.fragment.conversations

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blockchain.commet.R
import com.blockchain.commet.data.database.DBHelper
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.RowConversationBinding
import com.google.common.collect.Lists
import com.solana.models.buffer.ConversationItemModel
import java.util.Collections
import java.util.Random

class ConversationsAdapter(var onReply: OnReply, var activity: Activity) :
    RecyclerView.Adapter<ConversationsAdapter.MyViewHolder?>() {
    var list: MutableList<ConversationItemModel> = ArrayList()
    var dbHelperContactList: DBHelper = DBHelper(activity)

    fun interface OnReply {
        fun onClick(id: ConversationItemModel?)
    }

    fun add(model: ConversationItemModel) {
        list.add(model)
        notifyDataSetChanged()
    }

    fun addAll(models: List<ConversationItemModel>, check: Boolean?) {
        var models = models
        list.clear()
        models = Lists.reverse(models)
        models[0].new_conversation = check
        list.addAll(models)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: RowConversationBinding =
            RowConversationBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.Bind(list[position], list, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun clear() {
        list.clear()
    }

    inner class MyViewHolder(private val binding: RowConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun Bind(model: ConversationItemModel, list: List<ConversationItemModel?>, position: Int) {
            if (model.conversation_name.contains("&_#")) {
                val names =
                    model.conversation_name.split("&_#".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                if (names[0] == SharedPrefsHelper.getSharedPrefsHelper()["username"]) binding.text.text =
                    names[1]
                else binding.text.text = names[0]
            } else binding.text.text = model.conversation_name

            if (model.new_conversation) {
                binding.newMessage.visibility = View.VISIBLE
                Collections.swap(list, 0, position)
            } else {
                binding.newMessage.visibility = View.GONE
            }

            val model1 = dbHelperContactList.getMessages(model.conversation_id)
            if (model1.messages.isNotEmpty()) {
                if ((model1.messages[model1.messages.size - 1].message_type) != "text") {
                    binding.textLastMessage.text = "File Attachment"
                } else {
                    binding.textLastMessage.text = model1.messages[model1.messages.size - 1].text
                }
            } else {
                binding.textLastMessage.text = "Last Message"
            }
            binding.card.setOnClickListener { view -> onReply.onClick(model) }
            binding.executePendingBindings()
            val rand = Random()

            var res = 0
            try {
                res = model.avatar.toInt()
            } catch (ignored: Exception) {
            }
            when (res) {
                1 -> res = R.drawable.user_1
                2 -> res = R.drawable.user_2
                3 -> res = R.drawable.user_3
                4 -> res = R.drawable.user_4
                5 -> res = R.drawable.user_5
                6 -> res = R.drawable.user_6
            }

            binding.image.setImageResource(res)
        }
    }
}