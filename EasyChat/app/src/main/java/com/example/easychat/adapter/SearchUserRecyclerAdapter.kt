package com.example.easychat.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easychat.ChatActivity
import com.example.easychat.R
import com.example.easychat.model.UserModel
import com.example.easychat.utils.AndroidUtil

class SearchUserRecyclerAdapter(private val ctx:Context):
    RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder>() {

        private var mData = emptyList<UserModel>()

    class UserModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(userModel: UserModel) {
            val item = itemView.findViewById<LinearLayout>(R.id.search_user_item)
            item.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                AndroidUtil.parseUserModelAsIntent(intent, userModel)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                itemView.context.startActivity(intent)
            }
        }

        private val  usernameText:TextView = itemView.findViewById(R.id.user_name_text)
        private val phoneText:TextView = itemView.findViewById(R.id.phone_text)
        private val profileImg:ImageView = itemView.findViewById(R.id.profile_pic_img)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.search_user_recycler_row, parent, false)
        return UserModelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    fun setData(data: List<UserModel>?){
        data?.let {
            mData = data
            notifyDataSetChanged()
        }
    }
}