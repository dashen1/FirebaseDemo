package com.example.easychat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easychat.adapter.ChatRecyclerAdapter
import com.example.easychat.model.ChatMessageModel
import com.example.easychat.model.ChatRoomModel
import com.example.easychat.model.UserModel
import com.example.easychat.utils.AndroidUtil
import com.example.easychat.utils.FirebaseUtil
import com.google.firebase.Timestamp
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class ChatActivity : AppCompatActivity() {


    private lateinit var otherUser: UserModel
    private lateinit var sendMessageBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var otherUsername: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText

    private lateinit var chatroomId: String
    private var chatRoomModel: ChatRoomModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        otherUser = AndroidUtil.getUserModelFromIntent(intent)

//        chatroomId = FirebaseUtil.getChatroomId()

        sendMessageBtn = findViewById(R.id.message_send_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        recyclerView = findViewById(R.id.chat_recycler_view)
        messageInput = findViewById(R.id.message_input)

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        otherUsername.text = otherUser.username

        sendMessageBtn.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isEmpty()) return@setOnClickListener
            sendMessageToUser(message)
        }

        getOrCreateChatroomModel()

        setupChatRecyclerView()

    }

    private fun setupChatRecyclerView() {


        val adapter = ChatRecyclerAdapter(applicationContext)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true

        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        adapter.setData(emptyList())
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView.smoothScrollToPosition(0)
            }
        })
    }

    private fun sendMessageToUser(message: String) {
        chatRoomModel?.lastMessageTimestamp = Timestamp.now()
        chatRoomModel?.lastMessageSenderId = FirebaseUtil.currentUserId()
        chatRoomModel?.lastMessage = message
        FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel!!)
        val chatMessageModel =
            ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now())
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    messageInput.setText("")
                    sendNotification(message)
                }
            }
    }

    private fun sendNotification(message: String) {

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val currentUser = task.result.toObject(UserModel::class.java)

                try {
                    val jsonObject = JSONObject()
                    val notificationObj = JSONObject()
                    notificationObj.put("title", currentUser?.username)
                    notificationObj.put("body", message)

                    val dataObj = JSONObject()
                    dataObj.put("userId", currentUser?.userId)

                    jsonObject.put("notification", notificationObj)
                    jsonObject.put("data", dataObj)
                    jsonObject.put("to", otherUser.fcmToken)
                    callApi(jsonObject)
                }catch (e:Exception){

                }
            }

        }

    }

    private fun callApi(jsonObject: JSONObject) {
        val jsonType: MediaType = "application/json".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"

        val body: RequestBody = jsonObject.toString().toRequestBody(jsonType)
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization","")
            .build()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

            }

        })
    }

    private fun getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                chatRoomModel = task.result.toObject(ChatRoomModel::class.java)
                if (chatRoomModel == null) {
                    chatRoomModel = ChatRoomModel(
                        chatroomId,
                        listOf(FirebaseUtil.currentUserId(), otherUser.userId),
                        Timestamp.now(),
                        ""
                    )
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel!!)
                }
            }
        }
    }
}