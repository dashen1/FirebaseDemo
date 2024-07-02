package com.example.easychat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easychat.R
import com.example.easychat.model.ChatRoomModel
import com.example.easychat.model.UserModel
import com.example.easychat.utils.AndroidUtil
import com.example.easychat.utils.FirebaseUtil

class RecentChatRecyclerAdapter(private val ctx:Context):
    RecyclerView.Adapter<RecentChatRecyclerAdapter.RecentChatViewHolder>() {


        private var mData = emptyList<ChatRoomModel>()

    inner class RecentChatViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        fun bind(userModel: UserModel?){

            val username = itemView.findViewById<TextView>(R.id.user_name_text)
            val timestamp = itemView.findViewById<TextView>(R.id.last_message_time_text)
            val lastMessageText = itemView.findViewById<TextView>(R.id.last_message_text)
            val includeLayout = itemView.findViewById<RelativeLayout>(R.id.include_profile)
            val profilePic = includeLayout.findViewById<ImageView>(R.id.profile_pic_img)

            username.text = userModel?.username
            timestamp.text = userModel?.createdTimestamp.toString()

            FirebaseUtil.getOtherProfilePicStorageRef(userModel!!.userId).downloadUrl
                .addOnCompleteListener { t->
                    if(t.isSuccessful){
                        val uri = t.result
                        AndroidUtil.setProfilePic(ctx, uri, profilePic)
                    }
                }


        }
    }

    private val inflater:LayoutInflater = LayoutInflater.from(ctx)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewHolder {
        val view = inflater.inflate(R.layout.recent_chat_recycler_row, parent, false)

        return RecentChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecentChatViewHolder, position: Int) {
        FirebaseUtil.getOtherUserFromChatroom(mData[position].userIds)
            .get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val userModel = task.result.toObject(UserModel::class.java)
//                    val lastMessageSentByMe =
                    holder.bind(userModel)
                }
            }
    }

    fun setData(chatRoomModels: List<ChatRoomModel>?){
        if(chatRoomModels !=null){
            mData = chatRoomModels
            notifyDataSetChanged()
        }
    }
}