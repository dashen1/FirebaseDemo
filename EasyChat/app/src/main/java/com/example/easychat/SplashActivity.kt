package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.easychat.model.UserModel
import com.example.easychat.utils.AndroidUtil
import com.example.easychat.utils.FirebaseUtil

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (intent.extras != null) {
            // from notification
            val userId = intent?.extras?.getString("userId") ?: ""
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val model = task.result.toObject(UserModel::class.java)

                        val mainIntent = Intent(this, MainActivity::class.java)
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(mainIntent)

                        val intent = Intent(this, ChatActivity::class.java)
                        AndroidUtil.parseUserModelAsIntent(intent, model)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
        } else {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if (FirebaseUtil.isLoggedIn()) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, LoginPhoneNumberActivity::class.java))
                }
                finish()
            }, 1000)
        }
    }
}