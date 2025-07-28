package com.blockchain.commet.view.fragment.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.blockchain.commet.data.database.DBHelper
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.util.Utils
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentSettingBinding
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.view.activity.AuthActivity
import com.blockchain.commet.view.activity.MainActivity
import com.blockchain.commet.view.fragment.changepasscode.ChangePasscodeBottomSheet
import com.blockchain.commet.view.fragment.export.ExportBottomSheet
import com.blockchain.commet.view.fragment.wallet.WalletFragment
import com.example.mysolana.contact.BalanceComponent
import com.example.mysolana.contact.BalanceComponentInterface
import com.google.android.material.button.MaterialButton
import com.google.zxing.integration.android.IntentIntegrator
import `in`.aabhasjindal.otptextview.OtpTextView

class ProfileFragment : BaseFragment() , BalanceComponentInterface {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        BalanceComponent(this)
            .getUserBalance(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(R.color.disable2)
    }

    /***
     * setup Views of layout
     * set onClicks for views
     */
    private fun setupViews() {
        binding.apply {
            imgQR.setOnClickListener { MainActivity.start(requireContext(), R.id.qrcodeFragment) }
            btnLanguage.setOnClickListener { MainActivity.start(requireContext(), R.id.show_logFragment) }
            btnAccount.setOnClickListener {
                if (SharedPrefsHelper.getSharedPrefsHelper().get("PASSWORD").isNullOrEmpty()){
                    ChangePasscodeBottomSheet().show(parentFragmentManager, "changePassFragment")
                } else {
                    passcode()
                }
            }
            btnExport.setOnClickListener {
                if (SharedPrefsHelper.getSharedPrefsHelper().get("PASSWORD").isNullOrEmpty()){
                    ExportBottomSheet(
                        SharedPrefsHelper.getSharedPrefsHelper()["private_key"],
                        SharedPrefsHelper.getSharedPrefsHelper()["phrase"]
                    )
                        .show(parentFragmentManager, "exportFragment")
                } else {
                    export()
                }
            }
            btnLogout.setOnClickListener { callModal() }
            name.text = SharedPrefsHelper.getSharedPrefsHelper().get("username")
            when (SharedPrefsHelper.getSharedPrefsHelper()["index_profile"]) {
                "1" -> imgAvatar.setImageResource(R.drawable.user_1)
                "2" -> imgAvatar.setImageResource(R.drawable.user_2)
                "3" -> imgAvatar.setImageResource(R.drawable.user_3)
                "4" -> imgAvatar.setImageResource(R.drawable.user_4)
                "5" -> imgAvatar.setImageResource(R.drawable.user_5)
                "6" -> imgAvatar.setImageResource(R.drawable.user_6)
            }
        }
    }

    override fun create(error: String?, userpublickey: String?) {
        when (error) {
            "false" -> {
                requireActivity().runOnUiThread {
                    val result: String = (userpublickey?.toFloat()!! / 1000000000).toString()
                    SharedPrefsHelper.getSharedPrefsHelper().put("walletbalance", result).toString()
                    binding.amount.text = "$result SOL"
                }
            }

            "true" -> {
                binding.amount.text = "Connection Error"
            }
        }
    }

    fun callModal() {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setMessage("Are You Sure To Exit?").setPositiveButton(
            "OK"
        ) { dialogInterface: DialogInterface, i: Int ->
            requireActivity().runOnUiThread {
                dialogInterface.dismiss()
                val dbHelper =
                    DBHelper(activity)
                dbHelper.deleteLogsAll()
                dbHelper.deleteConversations()
                dbHelper.deleteContacts()
                dbHelper.deleteConversationsMessage()
                Toast.makeText(activity, "Exit", Toast.LENGTH_SHORT).show()
                SharedPrefsHelper.getSharedPrefsHelper().Clear()
                WalletFragment.list = arrayListOf()
                startActivity(Intent(context, AuthActivity::class.java))
                requireActivity().finish()
            }
        }.setNegativeButton("cancel") { dialogInterface: DialogInterface, i: Int ->
            if (activity == null) return@setNegativeButton
            requireActivity().runOnUiThread {
                dialogInterface.dismiss()
                val dbHelper =
                    DBHelper(activity)
                dbHelper.deleteLogsAll()
                dbHelper.deleteConversations()
                dbHelper.deleteContacts()
                dbHelper.deleteConversationsMessage()
            }
        }.show()
    }

    private fun passcode() {
        val alertDialog1 = AlertDialog.Builder(requireActivity())
        val inflater = this.layoutInflater
        val dialogView1 = inflater.inflate(R.layout.passcode, null)
        alertDialog1.setView(dialogView1)
        val btn = dialogView1.findViewById<MaterialButton>(R.id.checkBtn)
        val otp: OtpTextView = dialogView1.findViewById(R.id.otp_view)
        val fingerprint = dialogView1.findViewById<ImageView>(R.id.passcode_fingerprint)
        val dialog = alertDialog1.create()
        dialog.show()
        val biometricManager = BiometricManager.from(requireActivity())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    fingerprint.visibility = View.VISIBLE
                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Log.e(
                    "MY_APP_TAG",
                    "No biometric features available on this device."
                )

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Log.e(
                    "MY_APP_TAG",
                    "Biometric features are currently unavailable."
                )

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // Prompts the user to create credentials that your app accepts.
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                    enrollIntent.putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    startActivityForResult(enrollIntent, IntentIntegrator.REQUEST_CODE)
                }
            }
        }

        fingerprint.setOnClickListener {
            val executor = ContextCompat.getMainExecutor(requireActivity())
            val prompt = BiometricPrompt(
                requireActivity(),
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        dialog.cancel()
                        ChangePasscodeBottomSheet().show(parentFragmentManager, "changePassFragment")
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric")
                .setSubtitle("Authenticate user via Biometric")
                .setDescription("Please authenticate yourself here")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(true)
                .build()
            prompt.authenticate(promptInfo)
        }

        btn.setOnClickListener {
            val realPassword =
                SharedPrefsHelper.getSharedPrefsHelper()["PASSWORD"]
            if (otp.getOTP() == realPassword) {
                ChangePasscodeBottomSheet().show(parentFragmentManager, "changePassFragment")
            } else {
                Utils.getToasts(activity, "Wrong Password")
            }
            dialog.cancel()
        }

    }

    private fun export() {
        val alertDialog1 = AlertDialog.Builder(requireActivity())
        val inflater = this.layoutInflater
        val dialogView1 = inflater.inflate(R.layout.passcode, null)
        alertDialog1.setView(dialogView1)
        val btn = dialogView1.findViewById<MaterialButton>(R.id.checkBtn)
        val otp: OtpTextView = dialogView1.findViewById(R.id.otp_view)
        val fingerprint = dialogView1.findViewById<ImageView>(R.id.passcode_fingerprint)
        val dialog = alertDialog1.create()
        dialog.show()
        val biometricManager = BiometricManager.from(requireActivity())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    fingerprint.visibility = View.VISIBLE
                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Log.e(
                    "MY_APP_TAG",
                    "No biometric features available on this device."
                )

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Log.e(
                    "MY_APP_TAG",
                    "Biometric features are currently unavailable."
                )

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // Prompts the user to create credentials that your app accepts.
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                    enrollIntent.putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    startActivityForResult(enrollIntent, IntentIntegrator.REQUEST_CODE)
                }
            }
        }

        fingerprint.setOnClickListener {
            val executor = ContextCompat.getMainExecutor(requireActivity())
            val prompt = BiometricPrompt(
                requireActivity(),
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        dialog.cancel()
                        ExportBottomSheet(
                            SharedPrefsHelper.getSharedPrefsHelper()["private_key"],
                            SharedPrefsHelper.getSharedPrefsHelper()["phrase"]
                        ).show(parentFragmentManager, "exportFragment")
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric")
                .setSubtitle("Authenticate user via Biometric")
                .setDescription("Please authenticate yourself here")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(true)
                .build()
            prompt.authenticate(promptInfo)
        }

        btn.setOnClickListener {
            val realPassword =
                SharedPrefsHelper.getSharedPrefsHelper()["PASSWORD"]
            if (otp.getOTP() == realPassword) {
                ExportBottomSheet(
                    SharedPrefsHelper.getSharedPrefsHelper()["private_key"],
                    SharedPrefsHelper.getSharedPrefsHelper()["phrase"]
                )
                    .show(parentFragmentManager, "exportFragment")
            } else {
                Utils.getToasts(activity, "Wrong Password")
            }
            dialog.cancel()
        }

    }

}