package com.example.mystreammusic

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mystreammusic.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    private lateinit var _signupBinding: ActivitySignupBinding
    private val signupBinding get() = _signupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(_signupBinding.root)

        signupBinding.signupBtn.setOnClickListener {
            val email = signupBinding.signupEmailEdittext.text.toString().trim()
            val password = signupBinding.signupPasswordEdittext.text.toString().trim()
            val confirmPassword = signupBinding.signupPasswordConfirmEdittext.text.toString().trim()

            if(!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)){
                signupBinding.signupEmailEdittext.error = "Invalid email"
                return@setOnClickListener
            }

            if(password.length<6){
                signupBinding.signupPasswordEdittext.error = "Length should be 6 char."
                return@setOnClickListener
            }

            if(password != confirmPassword){
                signupBinding.signupPasswordConfirmEdittext.error = "Password not matched"
                return@setOnClickListener
            }

            createAccountWithFirebase(email, password)
        }

        signupBinding.signupGotoLoginTextView.setOnClickListener {
            finish()
        }
    }

    private fun createAccountWithFirebase(email: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setInProgress(false)
                Toast.makeText(applicationContext, "User created successfully.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                setInProgress(false)
                Toast.makeText(applicationContext, "Create account failed.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setInProgress(inProgress: Boolean) {
        if(inProgress){
            signupBinding.signupBtn.visibility = View.GONE
            signupBinding.signupProgressBar.visibility = View.VISIBLE
        }else{
            signupBinding.signupBtn.visibility = View.VISIBLE
            signupBinding.signupProgressBar.visibility = View.GONE
        }
    }
}