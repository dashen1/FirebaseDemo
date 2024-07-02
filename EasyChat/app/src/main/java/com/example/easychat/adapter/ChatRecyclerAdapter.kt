package com.example.easychat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easychat.R
import com.example.easychat.model.ChatMessageModel
import com.example.easychat.utils.FirebaseUtil

class ChatRecyclerAdapter(private val ctx:Context): RecyclerView.Adapter<ChatRecyclerAdapter.ChatModelViewHolder>() {

    private var mData = emptyList<ChatMessageModel>()

    inner class ChatModelViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun bind(messageModel: ChatMessageModel){
            val leftChatLayout = itemView.findViewById<LinearLayout>(R.id.left_chat_layout)
            val rightChatLayout = itemView.findViewById<LinearLayout>(R.id.right_chat_layout)
            val leftChatTextView = itemView.findViewById<TextView>(R.id.left_chat_text_view)
            val rightChatTextView = itemView.findViewById<TextView>(R.id.right_chat_text_view)

            if(messageModel.senderId.equals(FirebaseUtil.currentUserId())){
                leftChatLayout.visibility = View.GONE
                rightChatLayout.visibility = View.VISIBLE
                rightChatTextView.text = messageModel.message
            }else{
                leftChatLayout.visibility = View.VISIBLE
                rightChatLayout.visibility = View.GONE
                leftChatTextView.text = messageModel.message
            }
        }
    }

    private val inflater: LayoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val view = inflater.inflate(R.layout.chat_message_recycler_row, parent, false)

        return ChatModelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ChatModelViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    fun setData(data: List<ChatMessageModel>?){
        if(data !=null){
            mData = data
            notifyDataSetChanged()
        }
    }
}