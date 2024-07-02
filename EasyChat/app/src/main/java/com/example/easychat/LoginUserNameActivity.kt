package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.easychat.model.UserModel
import com.example.easychat.utils.FirebaseUtil
import com.google.firebase.Timestamp

class LoginUserNameActivity:AppCompatActivity() {

    private lateinit var usernameInput:EditText
    private lateinit var letMeInBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var phoneNumber: String

    private var userModel:UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_user_name)

        usernameInput = findViewById(R.id.login_username)
        letMeInBtn = findViewById(R.id.login_let_me_in_btn)
        progressBar = findViewById(R.id.login_user_progress_bar)

        progressBar.visibility = View.GONE

        phoneNumber = intent.extras?.getString("phone").toString() ?: ""
        //getUsername()

        letMeInBtn.setOnClickListener {
            setUsername()
        }
    }

    private fun setUsername() {

        val username = usernameInput.text.toString()
        if(username.isEmpty() || username.length < 3){
            usernameInput.error = "Username length should be at least 3 chars!"
            return
        }
        setInProgress(true)
        if(userModel !=null){
            userModel!!.username = username
        }else{
            userModel = UserModel(FirebaseUtil.currentUserId(), phoneNumber, username, Timestamp.now(), "")
        }

        FirebaseUtil.currentUserDetails().set(userModel!!).addOnCompleteListener { task ->
            setInProgress(false)
            Log.d("LoginUserNameActivity", "exception:${task.exception}")
            Log.d("LoginUserNameActivity", "isSuccessful:${task.isSuccessful}")
            if(task.isSuccessful){
                val intent = Intent(this@LoginUserNameActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun getUsername() {
        setInProgress(true)
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener{ task ->
            setInProgress(false)
            if(task.isSuccessful){
                userModel = task.result.toObject(UserModel::class.java)
                userModel?.let {
                    usernameInput.setText(it.username)
                }
            }
        }
    }

    private fun setInProgress(inProgress: Boolean){
        if(inProgress){
            progressBar.visibility = View.VISIBLE
            letMeInBtn.visibility = View.GONE
        }
    }
}
