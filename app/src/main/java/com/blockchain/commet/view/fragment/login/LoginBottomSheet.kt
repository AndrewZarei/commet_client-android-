package com.blockchain.commet.view.fragment.login

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation.findNavController
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.BottomsheetLoginBinding
import com.blockchain.commet.util.Utils
import com.blockchain.commet.view.activity.BaseActivity
import com.example.mysolana.auth.AuthComponent
import com.example.mysolana.auth.AuthInterface
import com.example.mysolana.auth.MyUser
import com.example.mysolana.auth.StateAuthLogin
import com.example.mysolana.check_private_key.CheckPrivateKeyComponent
import com.example.mysolana.check_private_key.CheckPrivateKeyInterface
import com.example.mysolana.check_private_key.StateCheckPrivateKey
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.solana.customConfig.CustomContactPda
import java.util.concurrent.atomic.AtomicReference

class LoginBottomSheet(
    private val btr: LoginFragment
) : BottomSheetDialogFragment() , AuthInterface , CheckPrivateKeyInterface {

    private lateinit var binding: BottomsheetLoginBinding
    private var authComponent = AuthComponent(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = STATE_EXPANDED
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    /***
     * setup Views of layout
     * set onClicks for views
     */
    private fun setupViews() {
        binding.apply {
            btnSearch.setOnClickListener {
                if (txtBtnSearch.text.equals("Continue")) {
                    val username = binding.username.getText().toString().trim()
                    if (username.isEmpty()) {
                        Toast.makeText(activity, R.string.enter_your_username, Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    if (username.contains("&_#")) {
                        Toast.makeText(activity, R.string.invalid_input, Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    binding.loadingLogin.visibility = View.VISIBLE
                    binding.btnSearch.visibility = View.INVISIBLE
                    authComponent.Login(CustomContactPda.getContactPda(), username, false)
                } else {
                    CheckPrivateKeyComponent(this@LoginBottomSheet).CheckMatchPrivateKey(binding.phraseKey.text.toString().trim())
                }
            }

        }
    }

    override fun login(myUser: MyUser, message: String?) {
        when (myUser.stateAuthLogin) {
            StateAuthLogin.EXCEPTION -> {
                activity?.runOnUiThread {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    binding.loadingLogin.visibility = View.INVISIBLE
                    binding.btnSearch.visibility = View.VISIBLE
                    Utils.getToasts(activity, message)
                }
            }

            StateAuthLogin.GET_INDEX -> SharedPrefsHelper.getSharedPrefsHelper()
                .put("id", myUser.id)
                .put("username", myUser.username)
                .put("index_profile", myUser.indexProfile)
                .put("base_pubkey", myUser.base_pubkey)

            StateAuthLogin.REPETITIOUS -> try {
                activity?.runOnUiThread {
                    binding.loadingLogin.visibility = View.INVISIBLE
                    binding.btnSearch.visibility = View.VISIBLE
                    SharedPrefsHelper.getSharedPrefsHelper().put("id", myUser.id)
                        .put("username", myUser.username)
                        .put("login", myUser.isLogin)
                        .put("index_profile", myUser.indexProfile)
                        .put("base_pubkey", myUser.base_pubkey)
                    binding.textView22.text = "Please Enter Your Phrase Key"
                    binding.phraseKey.visibility = View.VISIBLE
                    binding.username.visibility = View.INVISIBLE
                    binding.txtBtnSearch.text = "Login"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            StateAuthLogin.NOT_REPETITIOUS -> {
                SharedPrefsHelper.getSharedPrefsHelper().put("id", myUser.id)
                    .put("username", myUser.username).put("login", myUser.isLogin)
                    .put("base_pubkey", myUser.base_pubkey)
                SharedPrefsHelper.getSharedPrefsHelper().put("private_key", myUser.privateKey)
                SharedPrefsHelper.getSharedPrefsHelper().put("phrase", charsToString(myUser))
                SharedPrefsHelper.getSharedPrefsHelper().put("index_profile", myUser.indexProfile)
                activity?.runOnUiThread {
                    binding.loadingLogin.visibility = View.INVISIBLE
                    binding.btnSearch.visibility = View.VISIBLE
                    this.dismiss()
                    findNavController(btr.binding?.root!!).navigate(R.id.action_loginFragment_to_showPrivateKeyFragment)
                }
            }

            StateAuthLogin.SHOW_MODAL -> activity?.runOnUiThread {
                val username = java.lang.String.valueOf(binding.username.getText())
                callModal(username)
            }

            StateAuthLogin.CHECK_EQUALS_FALSE -> {}
            StateAuthLogin.NOT_REPETITIOUS_FAILURE -> {
                SharedPrefsHelper.getSharedPrefsHelper()
                    .put("id", myUser.id)
                    .put("username", myUser.username)
                    .put("login", myUser.isLogin)
                    .put("index_profile", myUser.indexProfile)
                    .put("base_pubkey", myUser.base_pubkey)
                requireActivity().runOnUiThread {
                    Utils.getToasts(activity, message)
                    binding.loadingLogin.visibility = View.INVISIBLE
                    binding.btnSearch.visibility = View.VISIBLE
                }
            }

            else -> {}
        }
    }

    override fun checkPrivateKey(
        publicKey: String?,
        stateCheckPrivateKey: StateCheckPrivateKey?,
        phrase: String?,
        privateKey: String?
    ) {
        val current_public_ley = SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"]
        if (current_public_ley == publicKey) {
            SharedPrefsHelper.getSharedPrefsHelper().put("login", true).put("private_key", privateKey).put("phrase", phrase)
            val bundle = Bundle()
            bundle.putBoolean("isFirst", true)
            this.dismiss()
            Utils.getToasts(activity, "Login Successful")
            startActivity(Intent(context, BaseActivity::class.java))
            requireActivity().finish()
        } else {
            Toast.makeText(activity, "not match private key with public key", Toast.LENGTH_SHORT).show()
        }
    }

    private fun charsToString(myUser: MyUser): String {
        val charList = myUser.mnemonicCode.chars
        val charArray = CharArray(myUser.mnemonicCode.chars.size)
        for (i in myUser.mnemonicCode.chars.indices) {
            charArray[i] = charList[i]
        }
        return String(charArray)
    }

    private fun callModal(username: String?) {
        val alertDialog = AlertDialog.Builder(requireContext())
        val inflater = this.getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.dialog_select_avatar, null)
        alertDialog.setView(dialogView)
        val image_user1 = dialogView.findViewById<ImageView>(R.id.user1)
        val image_user2 = dialogView.findViewById<ImageView>(R.id.user2)
        val image_user3 = dialogView.findViewById<ImageView>(R.id.user3)
        val image_user4 = dialogView.findViewById<ImageView>(R.id.user4)
        val image_user5 = dialogView.findViewById<ImageView>(R.id.user5)
        val image_user6 = dialogView.findViewById<ImageView>(R.id.user6)
        val tick1 = dialogView.findViewById<ImageView>(R.id.tick_1)
        val tick2 = dialogView.findViewById<ImageView>(R.id.tick_2)
        val tick3 = dialogView.findViewById<ImageView>(R.id.tick_3)
        val tick4 = dialogView.findViewById<ImageView>(R.id.tick_4)
        val tick5 = dialogView.findViewById<ImageView>(R.id.tick_5)
        val tick6 = dialogView.findViewById<ImageView>(R.id.tick_6)
        val id = AtomicReference("1")
        image_user1.setOnClickListener { view: View? ->
            tick1.setVisibility(View.VISIBLE)
            tick2.setVisibility(View.INVISIBLE)
            tick3.setVisibility(View.INVISIBLE)
            tick4.setVisibility(View.INVISIBLE)
            tick5.setVisibility(View.INVISIBLE)
            tick6.setVisibility(View.INVISIBLE)
            id.set("1")
        }
        image_user2.setOnClickListener { view: View? ->
            tick1.setVisibility(View.INVISIBLE)
            tick2.setVisibility(View.VISIBLE)
            tick3.setVisibility(View.INVISIBLE)
            tick4.setVisibility(View.INVISIBLE)
            tick5.setVisibility(View.INVISIBLE)
            tick6.setVisibility(View.INVISIBLE)
            id.set("2")
        }
        image_user3.setOnClickListener { view: View? ->
            tick1.setVisibility(View.INVISIBLE)
            tick2.setVisibility(View.INVISIBLE)
            tick3.setVisibility(View.VISIBLE)
            tick4.setVisibility(View.INVISIBLE)
            tick5.setVisibility(View.INVISIBLE)
            tick6.setVisibility(View.INVISIBLE)
            id.set("3")
        }
        image_user4.setOnClickListener { view: View? ->
            tick1.setVisibility(View.INVISIBLE)
            tick2.setVisibility(View.INVISIBLE)
            tick3.setVisibility(View.INVISIBLE)
            tick4.setVisibility(View.VISIBLE)
            tick5.setVisibility(View.INVISIBLE)
            tick6.setVisibility(View.INVISIBLE)
            id.set("4")
        }
        image_user5.setOnClickListener { view: View? ->
            tick1.setVisibility(View.INVISIBLE)
            tick2.setVisibility(View.INVISIBLE)
            tick3.setVisibility(View.INVISIBLE)
            tick4.setVisibility(View.INVISIBLE)
            tick5.setVisibility(View.VISIBLE)
            tick6.setVisibility(View.INVISIBLE)
            id.set("5")
        }
        image_user6.setOnClickListener { view: View? ->
            tick1.setVisibility(View.INVISIBLE)
            tick2.setVisibility(View.INVISIBLE)
            tick3.setVisibility(View.INVISIBLE)
            tick4.setVisibility(View.INVISIBLE)
            tick5.setVisibility(View.INVISIBLE)
            tick6.setVisibility(View.VISIBLE)
            id.set("6")
        }
        alertDialog.setMessage("Your username is new, do you want to continue with the same name?")
            .setPositiveButton("OK"
            ) { dialogInterface: DialogInterface, i: Int ->
                activity?.runOnUiThread {
                    dialogInterface.dismiss()
                    authComponent.createAccounts(username, id.get(), id.get())
                }
            }.setNegativeButton("cancel"
            ) { dialogInterface: DialogInterface, i: Int ->
                activity?.runOnUiThread {
                    dialogInterface.dismiss()
                    binding.loadingLogin.visibility = View.INVISIBLE
                    binding.btnSearch.visibility = View.VISIBLE
                    Utils.getToasts(activity, "You have canceled the creation of a new profile")
                }
            }.show()
    }

}