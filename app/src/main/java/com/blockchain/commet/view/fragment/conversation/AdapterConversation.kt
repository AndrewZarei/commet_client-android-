package com.blockchain.commet.view.fragment.conversation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.RowMessage2Binding
import com.blockchain.commet.databinding.RowMessageBinding
import com.blockchain.commet.view.activity.FullscreenActivity
import com.example.mysolana.customipfs.CustomIpfsComponent
import com.solana.models.buffer.MessageModel
import com.solana.models.buffer.MessageStatus
import com.solana.models.buffer.UserModel
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects
import kotlin.Boolean
import kotlin.Comparator
import kotlin.Int
import kotlin.String

class AdapterConversation(
    var context: Context,
    var isChat: Boolean,
    var customIpfsComponent: CustomIpfsComponent
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var list: MutableList<MessageModel?> = ArrayList<MessageModel?>()
    private lateinit var users: MutableList<UserModel?>

    fun setUsers(users: MutableList<UserModel?>) {
        this.users = ArrayList<UserModel?>(users)
    }

    fun imageUpdate() {
        list[list.size - 1]!!.status = MessageStatus.SUCCESS.name
    }

    fun add(model: MessageModel?) {
        list.add(list.size, model)
        sortByTime(list)
        notifyItemInserted(list.size)
        notifyDataSetChanged()
    }

    fun remove(message_id: String?) {
        var position = -1
        for (i in list.indices) {
            if (list[i]!!.message_id == message_id) {
                position = i
            }
        }
        if (position > -1) {
            list.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }
    }

    fun update(img_path: String?, position: Int) {
        list[position]!!.image = img_path
        list[position] = list[position]
        sortByTime(list)
        notifyItemInserted(list.size)
        notifyDataSetChanged()
    }

    fun addAll(models: MutableList<MessageModel?>) {
        if (!models.isEmpty()) {
            sortByTime(models)
            if (models.isEmpty()) {
                list.addAll(models)
                sortByTime(models)
                notifyDataSetChanged()
            } else {
                list.clear()
                if (models.size >= list.size && models[0]!!.message_id != null) {
                    list.addAll(models)
                    sortByTime(models)
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun sortByTime(messageList: MutableList<MessageModel?>) {
        val comparator: Comparator<MessageModel?> = object : Comparator<MessageModel?> {
            var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

            override fun compare(
                m1: MessageModel?,
                m2: MessageModel?
            ): Int {
                val d1: Date?
                val d2: Date?
                try {
                    d1 = format.parse(m1?.getTime()!!)
                    d2 = format.parse(m2?.getTime()!!)
                } catch (e: ParseException) {
                    return 0
                }
                return Objects.requireNonNull<Date?>(d1).compareTo(d2)
            }
        }

        Collections.sort<MessageModel?>(messageList, comparator)
    }

    fun clear() {
        list.clear()
    }

    internal inner class ViewHolderUser constructor(val binding: RowMessage2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun Bind(model: MessageModel) {
            binding.apply {
                setModel(model)

                if (!isChat) {
                    name.visibility = View.VISIBLE
                    name.text = getName(model.sender_address)
                    margin.visibility = View.GONE
                } else {
                    name.visibility = View.GONE
                    margin.visibility = View.VISIBLE
                }

                executePendingBindings()
            }

        }

        private fun getName(user_address: String?): String? {
            if (false) return ""
            for (i in users.indices) {
                if (users[i]!!.user_address == user_address) return users[i]!!.user_name
            }
            return ""
        }
    }

    internal inner class MyViewHolder constructor(val binding: RowMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(api = Build.VERSION_CODES.O)
        fun Bind(model: MessageModel) {
            binding.apply {

                setModel(model)
                executePendingBindings()
                Base64.getDecoder()

                if (model.status != null) {
                    if (model.status == MessageStatus.PENDING.name) {
                        imageViewP.visibility = ViewGroup.VISIBLE
                        imageView.visibility = ViewGroup.INVISIBLE
                        imageViewN.visibility = ViewGroup.INVISIBLE
                    } else if (model.status == MessageStatus.SUCCESS.name) {
                        imageViewP.visibility = ViewGroup.INVISIBLE
                        imageView.visibility = ViewGroup.VISIBLE
                        imageViewN.visibility = ViewGroup.INVISIBLE
                    } else if (model.status == MessageStatus.SUCCESS_NETWORK.name) {
                        imageViewP.visibility = ViewGroup.INVISIBLE
                        imageView.visibility = ViewGroup.INVISIBLE
                        imageViewN.visibility = ViewGroup.VISIBLE
                    }
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 0
        }
        return if (list[position]!!.sender_address == SharedPrefsHelper.getSharedPrefsHelper().get("id")) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == 0) {
            val itemBinding: RowMessageBinding =
                RowMessageBinding.inflate(layoutInflater, parent, false)
            return MyViewHolder(itemBinding)
        } else {
            val itemBinding: RowMessage2Binding =
                RowMessage2Binding.inflate(layoutInflater, parent, false)
            return ViewHolderUser(itemBinding)
        }
    }

    override fun getItemCount(): Int {
        return if (true) list.size else 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.setIsRecyclable(false)
        if (holder is MyViewHolder) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.Bind(list[position]!!)
            }

            holder.binding.apply {

                if (list[position]!!.message_type == "jpg" || list[position]!!.message_type == "png" || list[position]!!.message_type == "jpeg") {
                    parentImg.visibility = View.VISIBLE
                    meta.visibility = View.VISIBLE
                    textView4.visibility = View.GONE
                    progressImg.visibility = View.VISIBLE

                    img2.setOnClickListener { view ->
                        img2.visibility = View.GONE
                        progressImg.visibility = View.VISIBLE
                        customIpfsComponent.getIpfsRequest(
                            list[position]!!.text,
                            position,
                            list[position]!!.message_id
                        )
                    }

                } else if (list[position]!!.message_type == "text") {
                    parentImg.visibility = View.GONE
                    meta.visibility = View.GONE
                    textView4.visibility = View.VISIBLE
                } else {
                    parentImg.visibility = View.VISIBLE
                    meta.visibility = View.VISIBLE
                    textView4.visibility = View.GONE
                    progressImg.visibility = View.VISIBLE
                }

                if (list[position]!!.image != null) {
                    if (list[position]!!.message_type == "jpg" || list[position]!!.message_type == "png" || list[position]!!.message_type == "jpeg") {
                        val decodedString = android.util.Base64.decode(list[position]!!.image, android.util.Base64.DEFAULT)
                        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        img.setImageBitmap(decodedByte)

                        if (decodedByte != null) {
                            img2.visibility = View.GONE
                            progressImg.visibility = View.GONE
                        }

                        if (list[position]!!.size.isNotEmpty()) {
                            val bytes = list[position]!!.size.toDouble()
                            val df = DecimalFormat("#.##")
                            val formattedKilobytes = df.format(bytes)
                            meta.text = list[position]!!.name + " | " + formattedKilobytes + " KB"
                        }

                        if (list[position]!!.image.isEmpty()) {
                            img2.setOnClickListener { view ->
                                img2.visibility = View.GONE
                                progressImg.visibility = View.VISIBLE
                                customIpfsComponent.getIpfsRequest(list[position]!!.text, position, list[position]!!.message_id)
                        }

                        } else {
                            holder.binding.img.setOnClickListener { v ->
                                val fullImageIntent =
                                    Intent(context, FullscreenActivity::class.java)
                                fullImageIntent.putExtra("image", list[position]!!.image)
                                context.startActivity(fullImageIntent)
                            }
                        }
                    } else {
                        if (list[position]!!.size.isNotEmpty()) {
                            val bytes = list[position]!!.size.toDouble()
                            val df = DecimalFormat("#.##")
                            val formattedKilobytes = df.format(bytes)
                            meta.text = list[position]!!.name + " | " + formattedKilobytes + " KB"
                        }

                        img2.setOnClickListener { view ->
                            img2.visibility = View.GONE
                            progressImg.visibility = View.VISIBLE
                            customIpfsComponent.getIpfsRequest(
                                list[position]!!.text,
                                position,
                                list[position]!!.message_id
                            )
                        }
                    }
                }

                if (list[position]!!.checkUploadIpfs == "0" && list[position]!!.image == "") {
                    progress1.visibility = View.VISIBLE
                    img2.visibility = View.INVISIBLE
                } else if (list[position]!!.checkUploadIpfs == "1" && list[position]!!.image != "") {
                    progress1.visibility = View.INVISIBLE
                    img2.visibility = View.VISIBLE
                }

            }

        } else {

            (holder as ViewHolderUser).Bind(list[position]!!)
            holder.binding.apply {

                if (list[position]!!.message_type == "jpg" || list[position]!!.message_type == "png" || list[position]!!.message_type == "jpeg") {
                    parentImg.visibility = View.VISIBLE
                    textView4.visibility = View.GONE

                    if (list[position]!!.image.isEmpty()) {
                        img2.setOnClickListener { view ->
                            img2.visibility = View.GONE
                            progressImg.visibility = View.VISIBLE
                            customIpfsComponent.getIpfsRequest(list[position]!!.text, position, list[position]!!.message_id)
                        }
                    } else {
                        img.setOnClickListener { view ->
                            val fullImageIntent = Intent(context, FullscreenActivity::class.java)
                            fullImageIntent.putExtra("image", list[position]!!.image)
                            context.startActivity(fullImageIntent)
                        }
                    }
                } else if (list[position]!!.message_type == "text") {
                    parentImg.visibility = View.GONE
                    meta.visibility = View.GONE
                    textView4.visibility = View.VISIBLE
                } else {
                    parentImg.visibility = View.VISIBLE
                    meta.visibility = View.VISIBLE
                    textView4.visibility = View.GONE
                    progressImg.visibility = View.VISIBLE
                    img2.setOnClickListener { view ->
                        img2.setVisibility(View.GONE)
                        progressImg.visibility = View.VISIBLE
                        customIpfsComponent.getIpfsRequest(
                            list[position]!!.text,
                            position,
                            list[position]!!.message_id
                        )
                    }
                }

                if (list[position]!!.image != null) {
                    if (list[position]!!.size.isNotEmpty()) {
                        val bytes = list[position]!!.size.toDouble()
                        val df = DecimalFormat("#.##")
                        val formattedKilobytes = df.format(bytes)
                        meta.text = list[position]!!.name + " | " + formattedKilobytes + " KB"
                    }

                    if (list[position]!!.message_type == "jpg" || list[position]!!.message_type == "png" || list[position]!!.message_type == "jpeg") {
                        val decodedString = android.util.Base64.decode(list[position]!!.image, android.util.Base64.DEFAULT)
                        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        img.setImageBitmap(decodedByte)
                        if (decodedByte != null) {
                            img2.visibility = View.GONE
                            progressImg.visibility = View.GONE
                        }
                    }

                }

            }

        }

    }


}