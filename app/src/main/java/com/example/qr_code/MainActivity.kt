package com.example.qr_code

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.Xfermode
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.tooling.preview.Preview
import com.example.qr_code.ui.theme.QRCodeTheme
import java.util.EnumMap
import kotlin.math.log

class MainActivity : ComponentActivity() {
    private lateinit var editText: EditText
    private lateinit var generateButton: Button
    private lateinit var qrCodeImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.editText)
        generateButton = findViewById(R.id.generateButton)
        qrCodeImageView = findViewById(R.id.qrCodeImageView)

        generateButton.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isNotEmpty())
                generateQRCodeWithLogo(text)
        }
    }
    private fun generateQRCodeWithLogo(text: String) {
        val qrCodeWriter = QRCodeWriter()
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        hints[EncodeHintType.MARGIN] = 1
        try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if(bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.logo)
            val logoWidth = bitmap.width / 4
            val logoHeight = bitmap.height / 4
            val logoX = (bitMatrix.width - logoWidth) / 2
            val logoY = (bitMatrix.height - logoHeight) / 2

            val combinedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config!!)
            val canvas = android.graphics.Canvas(combinedBitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.drawBitmap(logoBitmap, Rect(0, 0, logoBitmap.width, logoBitmap.height), Rect(logoX, logoY, logoX + logoWidth, logoY + logoHeight), null)
            qrCodeImageView.setImageBitmap(combinedBitmap)
            qrCodeImageView.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}