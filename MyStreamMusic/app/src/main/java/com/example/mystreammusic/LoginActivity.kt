package com.example.mystreammusic

import android.content.Intent
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mystreammusic.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loginBinding.loginBtn.setOnClickListener {
            val email = loginBinding.loginEmailEdittext.text.toString().trim()
            val password = loginBinding.loginPasswordEdittext.text.toString().trim()

            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                loginBinding.loginEmailEdittext.error = "Invalid email"
                return@setOnClickListener
            }

            if (password.length < 6) {
                loginBinding.loginPasswordEdittext.error = "Length should be 6 char"
                return@setOnClickListener
            }

            loginWithFirebase(email, password)
        }

        loginBinding.loginGotoSignupBtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun loginWithFirebase(email: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setInProgress(false)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                setInProgress(false)
                Toast.makeText(applicationContext, "Login account failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().currentUser?.apply {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            loginBinding.loginBtn.visibility = View.GONE
            loginBinding.loginProgressBar.visibility = View.VISIBLE
        } else {
            loginBinding.loginBtn.visibility = View.VISIBLE
            loginBinding.loginProgressBar.visibility = View.GONE
        }
    }
}