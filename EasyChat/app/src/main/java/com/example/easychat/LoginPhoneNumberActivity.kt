package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.hbb20.CountryCodePicker

class LoginPhoneNumberActivity : AppCompatActivity() {

    private var countryCodePicker: CountryCodePicker? = null
    private var phoneInput:EditText? =null
    private var sendOtpBtn:Button? = null
    private var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_phone_number)
        countryCodePicker = findViewById(R.id.login_countrycode)
        phoneInput = findViewById(R.id.login_mobile_number)
        sendOtpBtn = findViewById(R.id.send_otp_btn)
        progressBar = findViewById(R.id.progress_bar)

        progressBar?.visibility = View.GONE

        countryCodePicker?.registerCarrierNumberEditText(phoneInput)
        sendOtpBtn?.setOnClickListener {
            if(!countryCodePicker!!.isValidFullNumber){
                phoneInput?.error = "Phone number not valid!"
                return@setOnClickListener
            }
            val intent = Intent(this@LoginPhoneNumberActivity, LoginOtpActivity::class.java)
            intent.putExtra("phone", countryCodePicker?.fullNumberWithPlus)
            startActivity(intent)
        }

    }
}