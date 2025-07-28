package com.blockchain.commet.view.fragment.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.base.BaseFragment
import com.blockchain.commet.databinding.FragmentQrcodeBinding
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

class QRcodeFragment : BaseFragment() {

    private lateinit var binding: FragmentQrcodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrcodeBinding.inflate(inflater, container, false)
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
                            0f to android.graphics.Color.CYAN,
                            1f to android.graphics.Color.BLUE,
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

            lblUsername.text = SharedPrefsHelper.getSharedPrefsHelper().get("username")
        }
    }

}