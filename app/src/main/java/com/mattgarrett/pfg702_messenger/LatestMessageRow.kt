package com.mattgarrett.pfg702_messenger

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mattgarrett.pfg702_messenger.dataclass.ChatMessageData
import com.mattgarrett.pfg702_messenger.dataclass.UserData
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.conversations_activity_row.view.*

class LatestMessageRow(val chatMessageData: ChatMessageData): Item<GroupieViewHolder>() {
    var chatPartnerUser: UserData? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_latest_message_conversations_activity.text = chatMessageData.text
        val authRef = Firebase.auth.uid
        val chatPartnerId = if (chatMessageData.fromId == authRef)
            chatMessageData.toId
        else
            chatMessageData.fromId
        val database = Firebase.database.getReference("USERS/$chatPartnerId")
        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(UserData::class.java)
                val username = "${chatPartnerUser!!.firstName} ${chatPartnerUser!!.lastName}"
                val targetImageView = viewHolder.itemView.iv_conversations_activity_rows
                viewHolder.itemView.tv_username_conversations_activity.text = username
                Picasso.get().load(chatPartnerUser!!.profileImageURL).into(targetImageView)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("latestMessagesRow", error.message)
            }

        })
    }

    override fun getLayout(): Int {
        return R.layout.conversations_activity_row
    }

}