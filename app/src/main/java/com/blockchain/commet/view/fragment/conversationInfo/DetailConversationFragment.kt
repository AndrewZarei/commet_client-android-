package com.blockchain.commet.view.fragment.conversationInfo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blockchain.commet.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.FragmentDetailConversationBinding
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.view.fragment.users.UserAdapter
import com.example.mysolana.contact.BalanceComponent
import com.example.mysolana.contact.BalanceComponentInterface
import com.example.mysolana.contact.SignatureForAddressComponent
import com.example.mysolana.contact.SignatureForAddressInterface
import com.solana.models.buffer.GetSignaturesForAddressModel
import com.solana.models.buffer.UserModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class DetailConversationFragment : Fragment() {
    private lateinit var binding: FragmentDetailConversationBinding
    private lateinit var signatureForAddressComponent: SignatureForAddressComponent
    private lateinit var balanceComponent: BalanceComponent
    private lateinit var publicKey: String
    private lateinit var id: ArrayList<UserModel>
    var userName: String? = null
    var cahtName: String? = null
    var avatar: String? = null
    var sigMassTimer: Timer = Timer()
    var adapter: UserAdapter = UserAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailConversationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(R.color.primary_200)
    }

    override fun onResume() {
        super.onResume()
        walletValue
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.apply {

            recycleUsers.setLayoutManager(LinearLayoutManager(requireActivity()))
            id = requireArguments().getParcelableArrayList<Parcelable?>("id") as ArrayList<UserModel>
            publicKey = requireArguments().getString("publicKey").toString()
            userName = requireArguments().getString("userName")
            cahtName = requireArguments().getString("cahtName")
            avatar = requireArguments().getString("avatar")

            signatureForAddressComponent =
                SignatureForAddressComponent(SignatureForAddressInterface { hasError: Boolean, data: GetSignaturesForAddressModel ->
                    if (false) {
                        return@SignatureForAddressInterface
                    }
                    val data1 = data
                    val timestampInSeconds = data1.result[0].blockTime
                    var instant: Instant? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        instant = Instant.ofEpochSecond(timestampInSeconds.toLong())
                        val date2 = Date.from(instant)
                        Date(date2.time)
                        val sdf = SimpleDateFormat("yy-MM-dd HH:mm:ss")
                        val formattedDate = sdf.format(date2)
                        lastActivity.text = "Last Activity : $formattedDate"
                    }
                })

            signatureForAddressComponent.getSignaturesForAddress(SharedPrefsHelper.getSharedPrefsHelper().get("base_pubkey"))
            adapter.setData(id, requireContext())
            recycleUsers.setAdapter(adapter)

            when (avatar) {
                "1" -> detailConversationAvatar.setImageResource(R.drawable.user_1)
                "2" -> detailConversationAvatar.setImageResource(R.drawable.user_2)
                "3" -> detailConversationAvatar.setImageResource(R.drawable.user_3)
                "4" -> detailConversationAvatar.setImageResource(R.drawable.user_4)
                "5" -> detailConversationAvatar.setImageResource(R.drawable.user_5)
                "6" -> detailConversationAvatar.setImageResource(R.drawable.user_6)
            }

            try {
                if (cahtName!!.contains("&_#")) {
                    val names: Array<String?> = cahtName!!.split("&_#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (names[0] == SharedPrefsHelper.getSharedPrefsHelper().get("username")) textViewName.text = names[1]
                    else textViewName.text = names[0]
                } else textViewName.text = cahtName
            } catch (e: Exception) {
                textViewName.text = cahtName
            }
        }
    }

    val walletValue: Unit
        get() {
            sigMassTimer.schedule(object : TimerTask() {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    requireActivity().runOnUiThread(Runnable {
                        balanceComponent =
                            BalanceComponent(BalanceComponentInterface { error: String?, userpublickey: String? ->
                                val result = userpublickey?.toLongOrNull()?.div(1_000_000_000)?.toString() ?: "0"
                                requireActivity().runOnUiThread(Runnable { binding.textView3.text = "$result SOL" })
                            })
                        balanceComponent.getUserBalance(SharedPrefsHelper.getSharedPrefsHelper().get("base_pubkey"))
                    })
                }
            }, 5000)
        }
}