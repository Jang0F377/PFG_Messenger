package com.mattgarrett.pfg702_messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mattgarrett.pfg702_messenger.dataclass.ChatMessageData
import com.mattgarrett.pfg702_messenger.registerlogin.NewUserActivity
import com.mattgarrett.pfg702_messenger.dataclass.UserData
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_conversations.*
import kotlinx.android.synthetic.main.conversations_activity_row.view.*


private const val TAG = "ConversationsActivity"

class ConversationsActivity : AppCompatActivity() {

    companion object {
        var currentUser: UserData? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversations)
        supportActionBar?.title = "My Conversations"
        RV_main_conversations_activity.adapter = adapter
        RV_main_conversations_activity.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->

            Log.d(TAG,"")
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            val rowNumber = row.chatPartnerUser
            intent.putExtra(NewMessageActivity.USER_KEY, rowNumber)
            startActivity(intent)

        }


        listenForMessages()

        fetchUsers()
        checkIfUserIsLoggedIn()

    }
    val latestMessagesMap = HashMap<String, ChatMessageData>()

    private fun refreshConversationsActivityRView() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }

    }

    private fun listenForMessages(){
        val fromId = Firebase.auth.uid
        val dbRef = "LATEST-MESSAGES/$fromId"
        val fbDatabase = Firebase.database.getReference(dbRef)
        fbDatabase.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessageData::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshConversationsActivityRView()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessageData::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshConversationsActivityRView()


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    val adapter = GroupAdapter<GroupieViewHolder>()



    private fun fetchUsers() {
        val uid = Firebase.auth.uid
        val dbRef = "USERS/$uid"
        val database = Firebase.database.getReference(dbRef)
        database.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(UserData::class.java)
                Log.d(TAG,"Current User: ${currentUser?.lastName}, ${currentUser?.firstName}")
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }


//    Function to check if user is logged and return to login screen if not
    private fun checkIfUserIsLoggedIn(){
        val auth = Firebase.auth
        val uid = auth.uid
        if (uid == null) {
            val intent = Intent(this, NewUserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }



//    Nav Bar Functions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                Firebase.auth.signOut()
                val intent = Intent(this,NewUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                Log.d(TAG,"Success: Signed Out")
                startActivity(intent)
            }
            R.id.menu_new_message -> {
                val intent = Intent(this,NewMessageActivity::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}

