package com.blockchain.commet.view.fragment.deposit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentDepositBinding
import com.blockchain.commet.util.setLightStatusBar
import com.blockchain.commet.util.toast
import com.example.mysolana.contact.AirDropComponent
import com.example.mysolana.contact.AirDropComponentInterface
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
import com.solana.Config

class DepositFragment : BaseFragment() , AirDropComponentInterface{

    private lateinit var binding: FragmentDepositBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepositBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(R.color.white)
    }

    private fun setupViews() {
        binding.apply {
            imgBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            userAddress.text = SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"]
            btnAirdrop.setOnClickListener {
                AirDropComponent(this@DepositFragment)
                    .SetAirDrop(SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
            }
            userAddress.setOnClickListener {
                (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .setPrimaryClip(
                        ClipData.newPlainText(
                            "Address", SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"]
                        )
                    )
                toast("Copied!!!")
            }
            if (Config.network == "Main") {
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
                QrCodeDrawable(
                    QrData.Url((SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])),
                    options
                )
            )
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