package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easychat.utils.AndroidUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class LoginOtpActivity:AppCompatActivity() {

    private var phoneNumber:String? = null
    private lateinit var otpInput:EditText
    private lateinit var nextBtn:Button
    private lateinit var progressBar: ProgressBar
    private lateinit var resendOtpTextView:TextView

    private var timeoutSeconds:Long = 60L
    private var verificationCode:String = ""
    private var resendingToken:PhoneAuthProvider.ForceResendingToken? = null

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_otp)

        otpInput = findViewById(R.id.login_otp)
        nextBtn = findViewById(R.id.login_next_otp_btn)
        progressBar = findViewById(R.id.login_opt_progress_bar)
        resendOtpTextView = findViewById(R.id.resend_otp_txt)


        progressBar.visibility = View.GONE
        val phoneNumber = intent.extras?.getString("phone") ?: ""
        Toast.makeText(applicationContext, phoneNumber, Toast.LENGTH_LONG).show()

        sendOtp(phoneNumber, false)

        nextBtn.setOnClickListener {
            val enteredOtp = otpInput.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp)
            signIn(credential)
            setInProgress(true)
        }

        resendOtpTextView.setOnClickListener {
            sendOtp(phoneNumber, true)
        }
    }

    private fun sendOtp(phoneNumber:String, isResend:Boolean){
        startResendTimer()
        setInProgress(true)
        val builder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.d("LoginOtpActivity", "onVerificationCompleted")
                    signIn(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.d("LoginOtpActivity", "onVerificationFailed:${e.message}")
                    AndroidUtil.showToast(applicationContext, "OTP verification failed.")
                }

                override fun onCodeSent(p0: String, forceResendingToken: ForceResendingToken) {
                    super.onCodeSent(p0, forceResendingToken)
                    verificationCode = p0
                    resendingToken = forceResendingToken
                    Log.d("LoginOtpActivity", "onCodeSent:${resendingToken}")
                    AndroidUtil.showToast(applicationContext, "OTP sent successfully.")
                    setInProgress(false)
                }
            })

        Log.d("LoginOtpActivity", "isResend:${isResend}")
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken!!).build())
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }


    }

    private fun startResendTimer() {
        resendOtpTextView.isEnabled = false
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                timeoutSeconds--
                resendOtpTextView.text = "Resend OTP in $timeoutSeconds seconds"
                if(timeoutSeconds <=0){
                    timeoutSeconds = 60L
                    timer.cancel()
                }
            }

        }, 0, 1000)
    }

    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        // login and go to next activity
        setInProgress(true)
        mAuth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val intent = Intent(this, LoginUserNameActivity::class.java)
                    startActivity(intent)
                }else{
                    AndroidUtil.showToast(applicationContext, "OTP verification failed.")
                }
            }
    }

    private fun setInProgress(inProgress:Boolean){
        if(inProgress){
            progressBar.visibility = View.VISIBLE
            nextBtn.visibility = View.GONE
        }else{
            progressBar.visibility = View.GONE
            nextBtn.visibility = View.VISIBLE
        }
    }
}