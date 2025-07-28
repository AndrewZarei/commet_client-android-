package com.blockchain.commet.view.fragment.contactBook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentLanguageBinding
import com.blockchain.commet.util.gone
import com.blockchain.commet.util.show
import com.blockchain.commet.util.toast
import com.blockchain.commet.view.fragment.contact.ContactAdapter
import com.example.mysolana.contact.ContactComponent
import com.example.mysolana.contact.ContactInterface
import com.example.mysolana.contact.StateContact
import com.solana.customConfig.CustomContactPda
import com.solana.models.buffer.ContactModel

class ContactBookFragment : BaseFragment() , ContactInterface {

    private lateinit var binding: FragmentLanguageBinding
    private lateinit var adapter: ContactAdapter

    companion object{
        private var list = ArrayList<ContactModel>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.apply {
            imgBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            addItem.setOnClickListener {
                rec.gone()
                addItem.gone()
                loading.show()
                ContactComponent(this@ContactBookFragment).getContacts(CustomContactPda.getContactPda())
            }
            adapter = ContactAdapter { contact,type -> onContactClick(contact,type) }
            rec.layoutManager = LinearLayoutManager(requireContext())
            rec.adapter = adapter
            adapter.setData(list,requireContext(),true)
        }
    }

    private fun onContactClick(contact: ContactModel,type: Boolean){
        if (type){
            list.remove(contact)
        } else {
            list.add(contact)
        }
        binding.rec.show()
        binding.loading.gone()
        binding.addItem.visibility = View.VISIBLE
        adapter.setData(list,requireContext(),true)
    }

    override fun getContact(
        error: String?,
        contactListModel: List<ContactModel>?,
        stateContact: StateContact?
    ) {
        when (stateContact) {
            StateContact.SUCCESS -> {
                requireActivity().runOnUiThread {
                    val list = arrayListOf<ContactModel>()
                    for ( contact in contactListModel!!){
                        list.add(
                            ContactModel(contact.user_name,contact.last_name,contact.public_key,contact.base_pubkey,contact.avatar)
                        )
                    }
                    adapter.setData(list,requireContext())
                    binding.loading.gone()
                    binding.rec.show()
                    binding.addItem.visibility = View.VISIBLE
                }
            }

            StateContact.FAILURE -> {
                requireActivity().runOnUiThread {
                    binding.loading.gone()
                    binding.rec.show()
                    binding.addItem.visibility = View.VISIBLE
                    toast("Connection Error")
                }
            }

            else -> {}
        }
    }

}