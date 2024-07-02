package com.example.easychat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easychat.adapter.RecentChatRecyclerAdapter
import com.example.easychat.utils.FirebaseUtil
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.chat_fragment_recycler_view)

        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {

        // Query from firebase.
         val query = FirebaseUtil.allChatroomCollectionReference()
             .whereArrayContains("userIds", FirebaseUtil.currentUserId())
             .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)



        val adapter = RecentChatRecyclerAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        adapter.setData(emptyList())

    }


}