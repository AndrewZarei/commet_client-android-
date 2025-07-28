package com.blockchain.commet.view.fragment.contact

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blockchain.commet.R
import com.blockchain.commet.data.database.DBHelper
import com.blockchain.commet.data.database.Logs
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.ContactsFragmentBinding
import com.blockchain.commet.databinding.DialogEdittextBinding
import com.blockchain.commet.view.activity.MainActivity.Companion.start
import com.example.mysolana.contact.BalanceComponent
import com.example.mysolana.contact.BalanceComponentInterface
import com.example.mysolana.contact.ContactComponent
import com.example.mysolana.contact.ContactInterface
import com.example.mysolana.contact.CreateConversationInterface
import com.example.mysolana.contact.StateContact
import com.example.mysolana.contact.StateCreateConversation
import com.google.gson.Gson
import com.solana.core.PublicKey
import com.solana.customConfig.CustomContactPda
import com.solana.models.buffer.ContactModel
import com.solana.models.buffer.ConversationItemModel
import com.solana.models.buffer.MessageModel
import com.solana.models.buffer.UserModel
import org.sol4k.Base58.decode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask
import java.util.stream.Collectors
import androidx.core.view.isVisible

class ContactsFragment : Fragment(), ContactInterface, CreateConversationInterface,
    BalanceComponentInterface,
    ClickAddContact {

    private lateinit var binding: ContactsFragmentBinding
    private lateinit var contactModels: MutableList<ContactModel>
    private lateinit var dbHelperContactList: DBHelper
    private lateinit var vis: ObservableInt
    private lateinit var gson: Gson

    private var adapterGroup: AdapterContacts? = null
    var adapterContact: AdapterContacts? = null
    private var finalContactModel2: List<ContactModel>? = null
    private var contactComponent: ContactComponent = ContactComponent(this, this)
    private var contactModel: List<ContactModel> = ArrayList()
    private var newConversationName: String? = null
    var balanceComponent: BalanceComponent? = null
    private var sigMassTimer: Timer = Timer()
    private var nameGroup: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vis = ObservableInt(0)
        gson = Gson()
        contactModels = ArrayList()
        dbHelperContactList = DBHelper(activity)
        (contactModels as ArrayList<ContactModel>).clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContactsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (binding.parentAdd.isVisible) {
                        changeUI(true)
                        return@setOnKeyListener true
                    } else if (binding.parentContact.isVisible) {
                        return@setOnKeyListener false
                    }
                }
            }
            false
        }
        contactModels.clear()
        adapterGroup = AdapterContacts({ model: ContactModel, check: Boolean ->
            if (check) addContact(model)
            else removeContact(model)
        }, { model: ContactModel ->
            val bundle = Bundle()
            bundle.putString("userID", model.public_key)
            bundle.putString("userName", model.user_name)
            bundle.putBoolean("startChat", true)
            bundle.putBoolean("isChat", true)
            bundle.putBoolean("first_chat", true)
            bundle.putString("name", model.user_name)
            bundle.putString("avatar", model.avatar)
            try {
                requireActivity().onBackPressed()
                start(requireContext(), R.id.conversationFragment, bundle)
            } catch (ignored: Exception) {
            }
        }, false, this)
        adapterContact = AdapterContacts({ model: ContactModel, check: Boolean ->
            if (check) addContact(model)
            else removeContact(model)
        }, { model: ContactModel ->
            dbHelperContactList.insertContacts(model)
            val bundle = Bundle()
            bundle.putString("userID", model.public_key)
            bundle.putString("userName", model.user_name)
            bundle.putBoolean("startChat", true)
            bundle.putBoolean("isChat", true)
            bundle.putBoolean("first_chat", true)
            bundle.putString("name", model.user_name)
            bundle.putString("avatar", model.avatar)
            try {
                requireActivity().onBackPressed()
                start(requireContext(), R.id.conversationFragment, bundle)
            } catch (ignored: Exception) {
            }
        }, true, this)

        if (requireArguments().getBoolean("select")) {
            binding.addContact.visibility = View.GONE
            binding.add.setImageResource(R.drawable.done)
            binding.cancel.visibility = View.VISIBLE
            contacts
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(R.string.create_group)
            builder.setCancelable(false)
            val binding: DialogEdittextBinding = DataBindingUtil.inflate(
                LayoutInflater.from(
                    activity
                ), R.layout.dialog_edittext, null, false
            )
            builder.setView(binding.root)
            builder.setPositiveButton(R.string.create) { dialogInterface, i ->
                if (binding.edit.getText().toString().trim().isEmpty()) {
                    Toast.makeText(activity, R.string.invalid_input, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                } else {
                    newConversationName = java.lang.String.valueOf(binding.edit.getText())
                }
                this.binding.cancel.visibility = View.GONE
                nameGroup = binding.edit.getText().toString().trim()
            }
            builder.setNegativeButton(R.string.cancel, null)
            builder.show()
        } else {
            binding.addContact.visibility = View.VISIBLE
            binding.add.visibility = View.INVISIBLE
            binding.cancel.visibility = View.GONE
            contactModels.clear()
        }
        adapterGroup!!.setSelectable(requireArguments().getBoolean("select"))
        val linearLayoutManager = LinearLayoutManager(
            activity
        )
        binding.rec.setLayoutManager(linearLayoutManager)
        binding.rec.setAdapter(adapterGroup)
        val linearLayoutManager2 = LinearLayoutManager(
            activity
        )
        binding.recAdd.setLayoutManager(linearLayoutManager2)
        binding.recAdd.setAdapter(adapterContact)
        val contactModel = dbHelperContactList.GetContacts()
        if (contactModel.isNotEmpty()) {
            contactModel.sortWith { object1: ContactModel, object2: ContactModel ->
                object1.user_name.compareTo(
                    object2.user_name
                )
            }
            vis.set(1)
        } else {
            vis.set(3)
        }
        val contactModelTemp = dbHelperContactList.GetContactsTemp()
        if (contactModelTemp.isNotEmpty()) {
            contactModelTemp.sortWith { object1: ContactModel, object2: ContactModel ->
                object1.user_name.compareTo(
                    object2.user_name
                )
            }
            requireActivity().runOnUiThread { adapterGroup!!.addAll(contactModelTemp) }
            vis.set(1)
        } else {
            vis.set(3)
        }
        binding.addContact.setOnClickListener { view13 -> changeUI(false) }
        binding.ref.setOnRefreshListener {}
        if (contactModel.isEmpty()) vis.set(0)
        contact
        binding.add.setOnClickListener { view1 -> addConversation() }
        binding.cancel.setOnClickListener { view1 ->
            binding.add.setImageResource(R.drawable.add)
            adapterGroup!!.setSelectable(false)
            binding.cancel.visibility = View.GONE
            contactModels.clear()
        }
        binding.appCompatButton.setOnClickListener { view12 ->
            vis.set(0)
            contact
        }
        val walletbalance = SharedPrefsHelper.getSharedPrefsHelper()["walletbalance"]
        binding.textView3.text = "$walletbalance SOL"
        binding.textView32.text = "$walletbalance SOL"
        binding.editText.setOnEditorActionListener { textView, i, keyEvent ->
            if (i === EditorInfo.IME_ACTION_SEARCH || textView.getText().length >= 3) {
                showCurrentContacts(contactModelTemp)
                return@setOnEditorActionListener true
            }
            false
        }
        binding.searchCurrentContact.setOnClickListener { v ->
            showCurrentContacts(
                contactModelTemp
            )
        }
        finalContactModel2 = dbHelperContactList.GetContacts()
        binding.editText2.setOnEditorActionListener { textView, i, keyEvent ->
            if (i === EditorInfo.IME_ACTION_SEARCH) {
                showContactsOnAddContacts()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.editText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 3) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val result = (finalContactModel2 as MutableList<ContactModel>?)?.stream()
                            ?.filter { item: ContactModel -> item.user_name.contains(s) }
                            ?.collect(Collectors.toList())
                        requireActivity().runOnUiThread { adapterContact!!.addAll(result) }
                    }
                }
            }
        })

        //        getWalletValue();
    }

    private val contact: Unit
        get() {
            contactComponent.getContacts(CustomContactPda.getContactPda())
        }

    private fun showContactsOnAddContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val result = finalContactModel2!!.stream()
                .filter { item: ContactModel -> item.user_name.contains(binding.editText2.getText()) }
                .collect(Collectors.toList())
            requireActivity().runOnUiThread { adapterContact!!.addAll(result) }
        }
    }

    private val contacts: Unit
        get() {
            contact
            val contactGroup = dbHelperContactList.GetContactsTemp()
            contactGroup.sortWith { object1: ContactModel, object2: ContactModel ->
                object1.user_name.compareTo(
                    object2.user_name
                )
            }
            requireActivity().runOnUiThread { adapterGroup!!.addAll(contactGroup) }
        }

    private fun showCurrentContacts(finalContactModel1: List<ContactModel>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val result = finalContactModel1.stream()
                .filter { item: ContactModel -> item.user_name.contains(binding.editText.getText()) }
                .collect(Collectors.toList())
            requireActivity().runOnUiThread {
                if (result.isNotEmpty()) adapterGroup!!.addAll(result)
            }
        }
    }

    fun changeUI(isContact: Boolean) {
        if (isContact) {
            binding.parentContact.visibility = View.VISIBLE
            binding.parentAdd.visibility = View.GONE
        } else {
            binding.parentContact.visibility = View.GONE
            binding.parentAdd.visibility = View.VISIBLE
            showContactsOnAddContacts()
        }
    }

    private fun checkContact(contactModel: ContactModel): Boolean {
        for (i in contactModels.indices) {
            if (contactModels[i].public_key == contactModel.public_key) return true
        }
        return false
    }

    private fun addContact(contactModel: ContactModel) {
        contactModels.add(contactModel)
    }

    private fun removeContact(contactModel: ContactModel) {
        for (i in contactModels.indices) {
            if (contactModels[i].public_key == contactModel.public_key) contactModels.remove(
                contactModels[i]
            )
        }
    }

    private fun addConversation() {
        if (adapterGroup!!.selectable) {
            requireActivity().runOnUiThread { vis.set(0) }
            val indexAvatar = SharedPrefsHelper.getSharedPrefsHelper()["index_profile"]
            contactComponent.createConversation(
                nameGroup,
                contactModels,
                SharedPrefsHelper.getSharedPrefsHelper()["id"],
                SharedPrefsHelper.getSharedPrefsHelper()["username"],
                true,
                decode(SharedPrefsHelper.getSharedPrefsHelper()["private_key"]),
                "walletUser1",
                "walletUser2",
                indexAvatar
            )
        } else {
            adapterGroup!!.setSelectable(true)
            binding.add.setImageResource(R.drawable.done)
            binding.cancel.visibility = View.VISIBLE
            Toast.makeText(activity, R.string.select_contacts, Toast.LENGTH_SHORT).show()
        }
    }


    override fun getContact(
        str: String,
        contactListModel: List<ContactModel>?,
        stateContact: StateContact
    ) {
        contactModel = dbHelperContactList.GetContacts()
        when (stateContact) {
            StateContact.SUCCESS -> {
                if (binding.ref.isRefreshing) binding.ref.isRefreshing = false
                if (contactListModel != null) {
                    if (contactListModel.isNotEmpty()) {
                        finalContactModel2 = contactListModel
                        dbHelperContactList.deleteContacts()
                        var i = 0
                        while (i < contactListModel.size) {
                            dbHelperContactList.insertContacts(contactListModel[i])
                            i++
                        }
                        try {
                            if (activity == null) return
                            if (contactListModel.isNotEmpty()) {
                                Collections.sort(
                                    contactListModel
                                ) { object1: ContactModel, object2: ContactModel ->
                                    object1.user_name.compareTo(
                                        object2.user_name
                                    )
                                }
                            }
                        } catch (ee: Exception) {
                            Log.e("TAG", "getContact:$ee")
                        }
                        vis.set(1)
                    } else {
                        vis.set(2)
                    }
                }
            }

            StateContact.FAILURE -> {
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                df.timeZone = TimeZone.getTimeZone("GMT+3:30")
                val nowAsISO = df.format(Date())
                val CustomText = "FAILURE-getContact-$str"
                Log.e("erfan", CustomText)
                dbHelperContactList.insertLogs(Logs(CustomText, "", nowAsISO, ""))
                if (binding.ref.isRefreshing) binding.ref.isRefreshing = false
                if (contactModel.isNotEmpty()) {
                    vis.set(1)
                } else {
                    vis.set(2)
                    return
                }
                vis.set(2)
            }
        }
    }

    override fun create(stateCreateConversation: StateCreateConversation, str: String) {
        when (stateCreateConversation) {
            StateCreateConversation.SUCCESS -> try {
                requireActivity().runOnUiThread { vis.set(1) }
                val conversationItemModel = ConversationItemModel()
                conversationItemModel.conversation_name = newConversationName
                conversationItemModel.conversation_id = str
                if (dbHelperContactList.conversationExists(conversationItemModel.conversation_name)) {
                    conversationItemModel.paren_id =
                        dbHelperContactList.getMaxParentConversation(conversationItemModel.conversation_name)
                }
                dbHelperContactList.insertConversations(conversationItemModel)
                val messageModel = MessageModel()
                val admin = UserModel()
                val members: MutableList<UserModel> = ArrayList()
                admin.user_address = SharedPrefsHelper.getSharedPrefsHelper()["id"]
                admin.user_name = SharedPrefsHelper.getSharedPrefsHelper()["username"]
                var i = 0
                while (i < contactModels.size) {
                    if (i == 0) {
                        members.add(admin)
                    }
                    members.add(
                        UserModel(
                            PublicKey(contactModels[i].public_key).toBase58(),
                            contactModels[i].user_name,
                            ""
                        )
                    )
                    i++
                }
                val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                df1.timeZone = TimeZone.getTimeZone("GMT+3:30")
                val customcreated_time = df1.format(Date())
                messageModel.time = customcreated_time
                dbHelperContactList.insertMessages(
                    messageModel, str, members, admin, newConversationName,
                    customcreated_time, "text", "", "", ""
                )
                contactModels.clear()
                if (activity == null) return
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        activity,
                        R.string.successfully_completed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Timer().schedule(
                    object : TimerTask() {
                        override fun run() {
                            requireActivity().runOnUiThread { findNavController(requireView()).popBackStack() }
                        }
                    }, 1500
                )
            } catch (e: Exception) {
                Log.i("erfan2", Objects.requireNonNull(e.message).toString())
            }

            StateCreateConversation.FAILURE -> try {
                contactModels.clear()
                requireActivity().runOnUiThread {
                    vis.set(1)
                    Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
                }
            } catch (ignored: Exception) {
            }
        }
    }

    val walletValue: Unit
        get() {
            sigMassTimer.schedule(object : TimerTask() {
                override fun run() {
                    val mainHandler = Handler(requireContext().mainLooper)
                    val myRunnable = Runnable {
                        balanceComponent = BalanceComponent(this@ContactsFragment)
                        balanceComponent!!.getUserBalance(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
                    }
                    mainHandler.post(myRunnable)
                }
            }, 5000)
        }

    @SuppressLint("SetTextI18n")
    override fun create(error: String, userpublickey: String) {
        when (error) {
            ("false") -> {
                requireActivity().runOnUiThread {
                    val result = (userpublickey.toFloat() / 1000000000).toString()
                    SharedPrefsHelper.getSharedPrefsHelper().put("walletbalance", result).toString()
                    binding.textView3.text = "$result SOL"
                    binding.textView32.text = "$result SOL"
                }
            }

            ("true") -> {
                val walletbalance = SharedPrefsHelper.getSharedPrefsHelper()["walletbalance"]
                requireActivity().runOnUiThread {
                    binding.textView3.text = "$walletbalance SOL"
                    binding.textView32.text = "$walletbalance SOL"
                }
            }
        }
    }

    override fun add(model: ContactModel) {
        dbHelperContactList.insertContactsTemp(model)
        changeUI(true)
        showCurrentContacts(dbHelperContactList.GetContactsTemp())
    }
}