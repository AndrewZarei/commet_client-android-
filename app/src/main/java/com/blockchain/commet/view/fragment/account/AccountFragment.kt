package com.blockchain.commet.view.fragment.account

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.util.Utils
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentAccountBinding
import com.blockchain.commet.view.fragment.changepasscode.ChangePasscodeBottomSheet
import com.blockchain.commet.view.fragment.export.ExportBottomSheet
import com.google.android.material.button.MaterialButton
import com.google.zxing.integration.android.IntentIntegrator
import `in`.aabhasjindal.otptextview.OtpTextView

class AccountFragment : BaseFragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
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
            imgBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnChangePass.setOnClickListener {
                if (SharedPrefsHelper.getSharedPrefsHelper().get("PASSWORD").isNullOrEmpty()){
                    ChangePasscodeBottomSheet().show(parentFragmentManager, "changePassFragment")
                } else {
                    passcode()
                }
            }
            btnExport.setOnClickListener { export() }
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

}