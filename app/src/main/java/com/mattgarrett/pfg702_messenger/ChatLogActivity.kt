package com.mattgarrett.pfg702_messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mattgarrett.pfg702_messenger.dataclass.ChatMessageData
import com.mattgarrett.pfg702_messenger.dataclass.UserData
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

private const val TAG = "ChatLogActivity"

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        RV_message_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)
        val first = toUser?.firstName
        val last = toUser?.lastName
        if (first != null && last != null) {
            val username = "${first!!.capitalize()} ${last!!.capitalize()}"
            supportActionBar?.title = username
        } else {
            supportActionBar?.title = "Message Log"
        }
        listenForMessages()

        btn_send_message_chat_log.setOnClickListener {
            Log.d(TAG,"Attempting to send message..")
            postMessageFlow()
        }
    }

    private fun listenForMessages() {
        val toUserId = toUser?.uid
        val fromUserId = Firebase.auth.uid
        val dbRef = "USER-MESSAGES/$fromUserId/$toUserId"
        val database = Firebase.database.getReference(dbRef)
        database.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessageData::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.fromId == fromUserId){
                        val currentUser = ConversationsActivity.currentUser ?: return
                        adapter.add(ChatToSomeoneItem(chatMessage.text,currentUser!!)) }
                    else{
                        adapter.add(ChatFromSomeoneItem(chatMessage.text,toUser!!))
                        }


                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    private fun postMessageFlow() {
        val fromUserId = Firebase.auth.uid
        val toUserId = toUser?.uid
        val fromDbRef = "USER-MESSAGES/$fromUserId/$toUserId"
        val toDbRef = "USER-MESSAGES/$toUserId/$fromUserId"
        val fromDatabase = Firebase.database.getReference(fromDbRef).push()
        val toDatabase = Firebase.database.getReference(toDbRef).push()
        val messageUid = toDatabase.key
        val text = et_enter_message_chat_log.text.toString()

        val chatMessage = ChatMessageData(messageUid,text,fromUserId,toUserId,System.currentTimeMillis())
        fromDatabase.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Success: Saved our chat message ${toDatabase.key}")
                et_enter_message_chat_log.text.clear()
                RV_message_chat_log.scrollToPosition(adapter.itemCount -1)
            }
            .addOnFailureListener {
                Log.d(TAG,"Failure: ${it.message}")
            }
        toDatabase.setValue(chatMessage)
    }

}

class ChatFromSomeoneItem(val text: String?, val userData: UserData): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_chat_from_row.text = text

        val pictureUri = userData.profileImageURL
        val targetImageView = viewHolder.itemView.iv_chat_from_row
        Picasso.get().load(pictureUri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }


}
class ChatToSomeoneItem(val text: String?, val userData: UserData): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_chat_to_row.text = text

        val pictureUri = userData.profileImageURL
        val targetImageView = viewHolder.itemView.iv_chat_to_row
        Picasso.get().load(pictureUri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }


}
