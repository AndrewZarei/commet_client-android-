package com.blockchain.commet.view.fragment.conversations

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blockchain.commet.MyApplication.Companion.appContext
import com.blockchain.commet.R
import com.blockchain.commet.data.database.DBHelper
import com.blockchain.commet.data.database.Logs
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.ConversationsFragmentBinding
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.view.activity.MainActivity.Companion.start
import com.example.mysolana.contact.BalanceComponent
import com.example.mysolana.contact.BalanceComponentInterface
import com.example.mysolana.conversations.ConversationsComponent
import com.example.mysolana.conversations.ConversationsInterface
import com.example.mysolana.conversations.StateConversations
import com.google.gson.Gson
import com.solana.models.buffer.ConversationItemModel
import com.solana.models.buffer.ProfileModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone

class ConversationsFragment : Fragment(), ConversationsInterface, BalanceComponentInterface {
    private lateinit var binding: ConversationsFragmentBinding
    private lateinit var balanceComponent: BalanceComponent
    private lateinit var component: ConversationsComponent
    private lateinit var adapter: ConversationsAdapter
    private lateinit var dbHelperContactList: DBHelper
    private lateinit var gson: Gson
    private lateinit var runnable: Runnable
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var conversationItemModel: ArrayList<ConversationItemModel>
    var profileModel: ProfileModel? = null
    var amount: Float? = null
    var ch: Boolean = true
    var get_conv_handler: Handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConversationsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(R.color.white)
    }

    override fun onResume() {
        super.onResume()
        val list = dbHelperContactList.GetConversations()
        requireActivity().runOnUiThread(Runnable {
            try {
                if (checkNewConversation()) {
                    if (!list.isEmpty()) {
                        adapter.addAll(list, ch)
                    }
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    override fun create(error: String, userpublickey: String) {
        if (error == "false") {
            val result = (userpublickey.toFloat() / 1000000000).toString()
            SharedPrefsHelper.getSharedPrefsHelper().put("walletbalance", result).toString()
            amount = userpublickey.toFloat() / 1000000000
        }
    }

    private fun setupViews(){
        binding.apply {

            gson = Gson()
            balanceComponent = BalanceComponent(this@ConversationsFragment)
            linearLayoutManager = LinearLayoutManager(requireActivity())
            dbHelperContactList = DBHelper(requireActivity())
            conversationItemModel = dbHelperContactList.GetConversations()

            changeUI(1)
            getConversations()

            adapter = ConversationsAdapter(ConversationsAdapter.OnReply { model: ConversationItemModel? ->
                ch = false
                val bundle = Bundle()
                bundle.putString("id", Objects.requireNonNull<ConversationItemModel?>(model).conversation_id)
                bundle.putBoolean("isChat", model!!.conversation_name.contains("&_#"))
                bundle.putString("name", model.conversation_name)
                bundle.putString("userName", model.conversation_name)
                bundle.putBoolean("startChat", false)
                bundle.putString("avatar", model.avatar)
                model.new_conversation = false
                start(requireContext(), R.id.conversationFragment, bundle)
            }, requireActivity())
            rec.setLayoutManager(linearLayoutManager)
            rec.setAdapter(adapter)
            ref.setOnRefreshListener { getConversations() }

            runnable = object : Runnable {
                override fun run() {
                    getConversations()
                    get_conv_handler.postDelayed(this, 4000)
                }
            }

            addConversation.setOnClickListener { v ->
                if (amount!! < 0.01) {
                    callModalBalance()
                } else {
                    ch = true
                    val bundle = Bundle()
                    bundle.putBoolean("select", false)
                    start(requireContext(), R.id.contactsFragment, bundle)
                }
            }

            appCompatButton.setOnClickListener { view12 ->
                changeUI(1)
                getConversations()
            }

            if (!conversationItemModel.isEmpty()) {
                try {
                    requireActivity().runOnUiThread(Runnable {
                        adapter.addAll(
                            conversationItemModel,
                            false
                        )
                    })
                } catch (ignored: Exception) {}
                changeUI(2)
            } else {
                changeUI(3)
            }

            if (isAdded) {
                balanceComponent.getUserBalance(
                    SharedPrefsHelper.getSharedPrefsHelper().get("base_pubkey")
                )
            }
        }

    }

    override fun getConversations(
        str: String,
        profileModel: ProfileModel,
        stateConversations: StateConversations?
    ) {
        if (str == "SUCCESS") {
            if (binding.ref.isRefreshing) binding.ref.isRefreshing = false
            try {
                var conversationItemModel: MutableList<ConversationItemModel?>
                conversationItemModel = dbHelperContactList.GetConversations()
                this.profileModel = profileModel
                if (!profileModel.conversation_list.isEmpty()) {
                    if (conversationItemModel.isEmpty()) {
                        for (i in profileModel.conversation_list.indices) {
                            dbHelperContactList.insertConversations(
                                profileModel.conversation_list[i]
                            )
                        }
                    } else if (profileModel.conversation_list.size > conversationItemModel.size) {
                        dbHelperContactList.deleteConversations()
                        for (i in profileModel.conversation_list.indices) {
                            dbHelperContactList.insertConversations(
                                profileModel.conversation_list[i]
                            )
                        }
                    }
                    try {
                        conversationItemModel = dbHelperContactList.GetConversations()
                        val finalConversationItemModel1: MutableList<ConversationItemModel?> =
                            conversationItemModel
                        requireActivity().runOnUiThread(Runnable {
                            adapter.addAll(finalConversationItemModel1 as List<ConversationItemModel>, false)
                            binding.rec.visibility = ViewGroup.VISIBLE
                            binding.textView8.visibility = ViewGroup.GONE
                            binding.progressBar.visibility = ViewGroup.GONE
                        })
                    } catch (ignored: Exception) {
                    }
                } else {
                    if (!conversationItemModel.isEmpty()) {
                        try {
                            val finalConversationItemModel: MutableList<ConversationItemModel?> =
                                conversationItemModel
                            requireActivity().runOnUiThread(Runnable {
                                adapter.addAll(finalConversationItemModel as List<ConversationItemModel>, false)
                                binding.rec.visibility = ViewGroup.VISIBLE
                                binding.textView8.visibility = ViewGroup.GONE
                                binding.progressBar.visibility = ViewGroup.GONE
                            })
                        } catch (ignored: Exception) {
                        }
                    } else {
                        binding.textView8.visibility = ViewGroup.VISIBLE
                        binding.rec.visibility = ViewGroup.GONE
                        binding.progressBar.visibility = ViewGroup.GONE
                    }
                }
            } catch (e: Exception) {
                binding.textView8.visibility = ViewGroup.VISIBLE
                binding.rec.visibility = ViewGroup.GONE
                binding.progressBar.visibility = ViewGroup.GONE
            }
        } else {
            val dbHelperContactList = DBHelper(appContext)
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            df.timeZone = TimeZone.getTimeZone("GMT+3:30")
            val nowAsISO = df.format(Date())
            try {
                val dbHelper = DBHelper(activity)
                val CustomText = "FAILURE-" + "getConversation-" + str
                Log.e("erfan", CustomText)
                dbHelper.insertLogs(Logs(CustomText, "", nowAsISO, ""))
                if (binding.ref.isRefreshing) binding.ref.isRefreshing = false
                val conversationItemModel = dbHelperContactList.GetConversations()
                if (!conversationItemModel.isEmpty()) {
                    try {
                        requireActivity().runOnUiThread(Runnable {
                            adapter.addAll(conversationItemModel, false)
                        })
                        binding.rec.visibility = ViewGroup.VISIBLE
                        binding.textView8.visibility = ViewGroup.GONE
                        binding.progressBar.visibility = ViewGroup.GONE
                    } catch (ignored: Exception) { }
                } else {
                    binding.textView8.visibility = ViewGroup.VISIBLE
                    binding.rec.visibility = ViewGroup.GONE
                    binding.progressBar.visibility = ViewGroup.GONE
                    binding.textView8.text = getString(R.string.error_connection)
                }
            } catch (e: Exception) {
                Log.e("TAG", "getConversation: " + e.message)
            }
        }
    }

    private fun getConversations(){
        component = ConversationsComponent(this@ConversationsFragment)
        component.getConversations(SharedPrefsHelper.getSharedPrefsHelper().get("id"))
    }

    private fun changeUI(State: Int){
        when(State){
            1 -> {
                binding.progressBar.visibility = ViewGroup.VISIBLE
                binding.textView8.visibility = ViewGroup.GONE
                binding.rec.visibility = ViewGroup.GONE
            }

            2 -> {
                binding.rec.visibility = ViewGroup.VISIBLE
                binding.textView8.visibility = ViewGroup.GONE
                binding.progressBar.visibility = ViewGroup.GONE
            }

            3 -> {
                binding.rec.visibility = ViewGroup.GONE
                binding.progressBar.visibility = ViewGroup.GONE
                binding.textView8.visibility = ViewGroup.VISIBLE
            }
        }
    }

    private fun callModalBalance() {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setMessage("Please Increase Your Balance").setPositiveButton(
            "OK",
            DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int -> dialogInterface!!.dismiss() })
            .show()
    }

    private fun checkNewConversation(): Boolean {
        if (profileModel != null) {
            return profileModel?.conversation_list?.size!! < dbHelperContactList.GetConversations().size
        } else {
            return false
        }
    }

}