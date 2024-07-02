package com.example.easychat.model

import com.google.firebase.Timestamp

data class UserModel(
    var userId: String,
    var phone:String,
    var username:String,
    var createdTimestamp: Timestamp,
    var fcmToken: String
)
