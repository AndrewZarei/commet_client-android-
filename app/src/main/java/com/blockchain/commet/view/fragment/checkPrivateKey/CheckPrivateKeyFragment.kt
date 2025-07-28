package com.blockchain.commet.view.fragment.checkPrivateKey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.R
import com.blockchain.commet.databinding.CheckprivatekeyFragmentBinding
import com.example.mysolana.check_private_key.CheckPrivateKeyComponent
import com.example.mysolana.check_private_key.CheckPrivateKeyInterface
import com.example.mysolana.check_private_key.StateCheckPrivateKey

class CheckPrivateKeyFragment : Fragment(), CheckPrivateKeyInterface {

    private lateinit var binding: CheckprivatekeyFragmentBinding
    private lateinit var checkPrivateKeyComponent: CheckPrivateKeyComponent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.checkprivatekey_fragment, container, false)
        checkPrivateKeyComponent = CheckPrivateKeyComponent(CheckPrivateKeyInterface { publicKey: String?, stateCheckPrivateKey: StateCheckPrivateKey?, phrase: String?, privateKey: String? ->
                this.checkPrivateKey(
                    publicKey,
                    stateCheckPrivateKey,
                    phrase,
                    privateKey
                )
            })
        binding.checkprivate.setOnClickListener({ view ->
            checkPrivateKeyComponent.CheckMatchPrivateKey(binding.phrase.getText().toString().trim())
        })
        return binding.root
    }

    override fun checkPrivateKey(
        publicKey: String?,
        stateCheckPrivateKey: StateCheckPrivateKey?,
        phrase: String?,
        privateKey: String?
    ) {
        val current_public_ley = SharedPrefsHelper.getSharedPrefsHelper().get("base_pubkey")
        if (current_public_ley == publicKey) {
            SharedPrefsHelper.getSharedPrefsHelper().put("login", true)
                .put("private_key", privateKey).put("phrase", phrase)
            val bundle = Bundle()
            bundle.putBoolean("isFirst", true)
            findNavController(binding.root).navigate(
                R.id.action_checkPrivateKeyFragment_to_passcodeFragment,
                bundle
            )
        } else {
            Toast.makeText(requireActivity(), "not match private key with public key", Toast.LENGTH_SHORT).show()
        }
    }
}