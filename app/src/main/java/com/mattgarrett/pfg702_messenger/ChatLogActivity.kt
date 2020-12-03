package com.mattgarrett.pfg702_messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mattgarrett.pfg702_messenger.dataclass.ChatMessageData
import com.mattgarrett.pfg702_messenger.dataclass.UserData
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

private const val TAG = "ChatLogActivity"

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<UserData>(NewMessageActivity.USER_KEY)
        val first = user.firstName
        val last = user.lastName
        if (first != null && last != null) {
            val username = "${first!!.capitalize()} ${last!!.capitalize()}"
            supportActionBar?.title = username
        } else {
            supportActionBar?.title = "Chat Log"
        }

        getDummyData()

        btn_send_message_chat_log.setOnClickListener {
            Log.d(TAG,"Attempting to send message..")
            postMessageFlow()
        }
    }

    private fun postMessageFlow() {
        val toUserInfo = intent.getParcelableExtra<UserData>(NewMessageActivity.USER_KEY)
        val fromUser = Firebase.auth.uid
        val toUser = toUserInfo.uid
        val dbRef = "MESSAGES"
        val database = Firebase.database.getReference(dbRef).push()
        val messageUid = database.key
        val text = et_enter_message_chat_log.text.toString()

        val chatMessage = ChatMessageData(messageUid,text,fromUser,toUser,System.currentTimeMillis())
        database.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Success: Saved our chat message ${database.key}")
            }
            .addOnFailureListener {
                Log.d(TAG,"Failure: ${it.message}")
            }



    }

    private fun getDummyData() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(ChatFromItem("From Message ....."))
        adapter.add(ChatToItem("To message\nto message"))
        adapter.add(ChatFromItem("From Message ....."))
        adapter.add(ChatToItem("To message\nto message"))
        adapter.add(ChatFromItem("From Message ....."))
        adapter.add(ChatToItem("To message\nto message"))
        adapter.add(ChatFromItem("From Message ....."))
        adapter.add(ChatToItem("To message\nto message"))
        adapter.add(ChatFromItem("From Message ....."))
        adapter.add(ChatToItem("To message\nto message"))

        RV_message_chat_log.adapter = adapter

    }
}

class ChatFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_chat_from_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }


}
class ChatToItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_chat_to_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }


}
