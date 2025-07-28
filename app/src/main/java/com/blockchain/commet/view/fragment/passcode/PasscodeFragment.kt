package com.blockchain.commet.view.fragment.passcode

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.FragmentPasscodeBinding
import com.blockchain.commet.util.Utils
import com.blockchain.commet.view.activity.BaseActivity
import com.google.zxing.integration.android.IntentIntegrator
import `in`.aabhasjindal.otptextview.OTPListener

class PasscodeFragment : Fragment() {
    private  lateinit var binding: FragmentPasscodeBinding
    var passcode: String? = null
    var isFirst: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasscodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews(){
        binding.apply {

            isFirst = requireArguments().getBoolean("isFirst")
            val biometricManager = BiometricManager.from(requireActivity())
            val executor = ContextCompat.getMainExecutor(requireActivity())
            val promptInfo = PromptInfo.Builder()
                .setTitle("Biometric")
                .setSubtitle("Authenticate user via Biometric")
                .setDescription("Please authenticate yourself here")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(true)
                .build()

            val prompt = BiometricPrompt(
                requireActivity(), executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        startActivity(Intent(context, BaseActivity::class.java))
                        requireActivity().finish()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                    }
                })

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {

                    BiometricManager.BIOMETRIC_SUCCESS -> { fingerprint.setVisibility(View.VISIBLE) }

                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                        enrollIntent.putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                        )
                        startActivityForResult(enrollIntent, IntentIntegrator.REQUEST_CODE)
                    }

                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {}

                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {}

                    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {}

                    BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {}

                    BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {}
                }
            }

            if (isFirst) fingerprint.setVisibility(View.GONE)

            fingerprint.setOnClickListener({ v ->
                prompt.authenticate(promptInfo)
            })

            saveAndContinue.setOnClickListener { v ->
                try {
                    if (isFirst) {
                        SharedPrefsHelper.getSharedPrefsHelper().put("PASSWORD", passcode)
                        startActivity(Intent(context, BaseActivity::class.java))
                        requireActivity().finish()
                    } else {
                        val realPassword = SharedPrefsHelper.getSharedPrefsHelper().get("PASSWORD")
                        if (realPassword == passcode) {
                            startActivity(Intent(context, BaseActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Utils.getToasts(activity, "Wrong Password")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            otpView.otpListener = object : OTPListener {
                override fun onInteractionListener() {}
                override fun onOTPComplete(otp: String?) { passcode = otp }
            }

        }

    }
}