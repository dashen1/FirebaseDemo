package com.example.easychat.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.easychat.model.UserModel
import com.google.firebase.Timestamp

object AndroidUtil {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun parseUserModelAsIntent(intent: Intent, model: UserModel?) {
        intent.putExtra("username", model?.username)
        intent.putExtra("phone", model?.phone)
        intent.putExtra("userId", model?.userId)
        intent.putExtra("fcmToken", model?.fcmToken)
    }

    fun getUserModelFromIntent(intent: Intent): UserModel {

        val userId = intent.getStringExtra("userId")
        val username = intent.getStringExtra("username")
        val phone = intent.getStringExtra("phone")
        val fcmToken = intent.getStringExtra("fcmToken")
        val userModel = UserModel(userId!!, username!!, phone!!, Timestamp.now(), fcmToken!!)
        return userModel
    }

    fun setProfilePic(context: Context, imageUri: Uri, imageView: ImageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }
}