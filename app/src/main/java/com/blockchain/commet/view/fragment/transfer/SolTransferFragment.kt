package com.blockchain.commet.view.fragment.transfer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.util.Utils
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentSolTransferBinding
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.util.toast
import com.blockchain.commet.view.fragment.export.ExportBottomSheet
import com.example.mysolana.contact.AirDropComponent
import com.example.mysolana.contact.AirDropComponentInterface
import com.example.mysolana.contact.BalanceComponentInterface
import com.example.mysolana.withdrow.StateWithdraw
import com.example.mysolana.withdrow.WithdrawComponent
import com.example.mysolana.withdrow.WithdrawInterface
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.style.Color
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoPadding
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
import com.google.android.material.button.MaterialButton
import com.google.zxing.integration.android.IntentIntegrator
import com.solana.Config
import `in`.aabhasjindal.otptextview.OtpTextView
import java.math.BigDecimal

class SolTransferFragment : BaseFragment() , BalanceComponentInterface , WithdrawInterface , AirDropComponentInterface{

    private lateinit var binding: FragmentSolTransferBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

//        BalanceComponent(this)
//            .getUserBalance(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(R.color.white)
    }

    override fun create(error: String?, userpublickey: String?) {
//        if (error.equals("false")){
//            requireActivity().runOnUiThread {
//                val result: String = (userpublickey?.toFloat()?.div(1000000000)).toString()
//                SharedPrefsHelper.getSharedPrefsHelper().put("walletbalance", result).toString()
//                binding.lblAmount.text = "$result"
//            }
//        } else {
//            requireActivity().runOnUiThread {
//                binding.lblAmount.text = "Connection Error"
//                binding.lblSol.text = ""
//            }
//        }
    }

    override fun sendWithdrawRequest(error: String?, stateWithdraw: StateWithdraw?) {
        if (error.equals("SUCCESS")){
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Transfer Done!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupViews() {
        binding.apply {
            imgBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            txtAmountAvailable.text = requireArguments().getString("balance")
            userAddress.text = SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"]
            btnAirdrop.setOnClickListener {
                AirDropComponent(this@SolTransferFragment)
                    .SetAirDrop(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
            }
            layoutWord.setOnClickListener { changeUI("deposit") }
            layoutPrivateKey.setOnClickListener { changeUI("withdraw") }
            userAddress.setOnClickListener {
                (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .setPrimaryClip(ClipData.newPlainText("Address"
                        ,
                        SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"]))
                toast("Copied!!!")
            }
            btnSend.setOnClickListener {
                if (address.text.isNullOrEmpty() || amount.text.isNullOrEmpty()) toast("Please enter address & amount")
                else
                    WithdrawComponent(this@SolTransferFragment)
                        .sendWithdrawRequest(
                            SharedPrefsHelper.getSharedPrefsHelper().get("private_key"),
                            address.text.trim().toString(),
                            (BigDecimal(amount.text.trim().toString()).multiply(BigDecimal("1000000000"))).toLong())
            }
            if (Config.network == "Main"){
                btnAirdrop.visibility = View.GONE
            } else {
                btnAirdrop.visibility = View.VISIBLE
            }

            val options = createQrVectorOptions {

                padding = .125f

                logo {
                    drawable = ContextCompat
                        .getDrawable(requireContext(), R.drawable.communication)
                    size = .25f
                    padding = QrVectorLogoPadding.Natural(.2f)
                    shape = QrVectorLogoShape
                        .Circle
                }
                colors {
                    dark = QrVectorColor
                        .Solid(Color(0xff345288))
                    ball = QrVectorColor.Solid(
                        ContextCompat.getColor(requireContext(), R.color.light_blue_600)
                    )
                    frame = QrVectorColor.LinearGradient(
                        colors = listOf(
                            0f to Color.CYAN,
                            1f to Color.BLUE,
                        ),
                        orientation = QrVectorColor.LinearGradient
                            .Orientation.LeftDiagonal
                    )
                }
                shapes {
                    darkPixel = QrVectorPixelShape
                        .RoundCorners(.5f)
                    ball = QrVectorBallShape
                        .RoundCorners(.25f)
                    frame = QrVectorFrameShape
                        .RoundCorners(.25f)
                }
            }

            imgQR.setImageDrawable(
                QrCodeDrawable(QrData.Url((SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])), options)
            )
        }
    }

    private fun changeUI(type: String){
        if (type == "deposit"){
            binding.txtAmountAvailable.visibility = View.GONE
            binding.textView22.visibility = View.GONE
            binding.textView23.visibility = View.GONE
            binding.textView25.visibility = View.GONE
            binding.address.visibility = View.GONE
            binding.amount.visibility = View.GONE
            binding.imgQR.visibility = View.VISIBLE
            binding.userAddress.visibility = View.VISIBLE
        } else {
            binding.txtAmountAvailable.visibility = View.VISIBLE
            binding.textView22.visibility = View.VISIBLE
            binding.textView23.visibility = View.VISIBLE
            binding.textView25.visibility = View.VISIBLE
            binding.address.visibility = View.VISIBLE
            binding.amount.visibility = View.VISIBLE
            binding.imgQR.visibility = View.GONE
            binding.userAddress.visibility = View.GONE
        }
    }

    private fun getPassword() {
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

            val promptInfo = PromptInfo.Builder()
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

    override fun result(error: String?, state: String?) {
        if (error.equals("true")) {
            requireActivity().runOnUiThread {
                if (state?.contains("code=429")!!) {
                    Toast.makeText(requireContext(), "Too Many Request", Toast.LENGTH_SHORT).show()
                } else if (state.contains("Internal error")) {
                    Toast.makeText(requireContext(), "Solana DevNet Network Error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), state, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Have 4 Sol Airdrop!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}