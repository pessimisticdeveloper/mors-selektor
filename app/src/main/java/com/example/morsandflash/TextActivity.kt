package com.example.morsandflash

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class TextActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private var isFlashOn = false
    private lateinit var gonder : Button
    private lateinit var mic : ImageButton
    private lateinit var sesli_mesaj : TextView
    private val id = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        gonder = findViewById(R.id.gonder)
        mic = findViewById(R.id.mic)
        sesli_mesaj = findViewById(R.id.sesli_mesaj)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        mic.setOnClickListener {
            sesOlayi()
        }
        gonder.setOnClickListener{
            val inputText = sesli_mesaj.text.toString()
            val morseCode = convertToMorseCode(inputText)
            flashMorseCode(morseCode)
        }
        sesli_mesaj.setText("")
    }

    private fun sesOlayi() {
        try {
            val sesIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            sesIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            sesIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            startActivityForResult(sesIntent, id)
        } catch (e: Exception) {
            Toast.makeText(this@TextActivity, "Telefonunuz Bu Servisi Desteklemiyor", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val yaziyaDonusen = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            sesli_mesaj.text = yaziyaDonusen?.get(0).toString()
        }
    }

    private fun convertToMorseCode(text: String): String {
            val morseCodeMap = mapOf(
                'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
                'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
                'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
                'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
                'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
                'Z' to "--..", '0' to "-----", '1' to ".----", '2' to "..---",
                '3' to "...--", '4' to "....-", '5' to ".....", '6' to "-....",
                '7' to "--...", '8' to "---..", '9' to "----."
            )
            val upperCaseText = text.uppercase(Locale.ROOT)
            val morseCodeBuilder = StringBuilder()

            for (char in upperCaseText) {
                if (char == ' ') {
                    morseCodeBuilder.append(" ")
                } else if (morseCodeMap.containsKey(char)) {
                    morseCodeBuilder.append(morseCodeMap[char])
                    morseCodeBuilder.append(" ")
                }
            }

            return morseCodeBuilder.toString()
        }

        private fun flashMorseCode(morseCode: String) {
            GlobalScope.launch(Dispatchers.Main) {
                for (char in morseCode) {
                    if (char == '.') {
                        flashLightOn()
                        delay(200) // . işareti için ışık açık kalma süresi
                        flashLightOff()
                        delay(100) // İşaretler arası bekleme süresi
                    } else if (char == '-') {
                        flashLightOn()
                        delay(500) // - işareti için ışık açık kalma süresi
                        flashLightOff()
                        delay(100) // İşaretler arası bekleme süresi
                    } else if (char == ' ') {
                        delay(300) // Harfler arası bekleme süresi
                    }
                }
            }
        }

        private suspend fun flashLightOn() {
            withContext(Dispatchers.IO) {
                try {
                    val cameraId = cameraManager.cameraIdList[0]
                    cameraManager.setTorchMode(cameraId, true)
                    isFlashOn = true
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }

        private suspend fun flashLightOff() {
            withContext(Dispatchers.IO) {
                try {
                    val cameraId = cameraManager.cameraIdList[0]
                    cameraManager.setTorchMode(cameraId, false)
                    isFlashOn = false
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }