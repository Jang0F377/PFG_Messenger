package com.mattgarrett.pfg702_messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mattgarrett.pfg702_messenger.dataclass.UserData
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*


private const val TAG = "New Message Activity"
class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"


        fetchUsers()
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val databasePath = "USERS"
        val userRef = Firebase.database.getReference(databasePath)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<com.xwray.groupie.GroupieViewHolder>()

                snapshot.children.forEach {
                    Log.d(TAG,it.toString())
                    val user = it.getValue(UserData::class.java)
                    if (user != null){
                        adapter.add(UserItem(user))
                    }
                }

                // intent to choose a person to send message too from RV
                adapter.setOnItemClickListener {item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                RV_new_message_activity.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

}

class UserItem(val user: UserData): Item<com.xwray.groupie.GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }


    override fun bind(viewHolder: com.xwray.groupie.GroupieViewHolder, position: Int) {
        val first = user.firstName
        val last = user.lastName
        val firstLast = "${first?.capitalize()} ${last?.capitalize()}"
        viewHolder.itemView.tv_user_name_new_message.text =  firstLast
        Picasso.get().load(user.profileImageURL).into(viewHolder.itemView.IV_user_image_new_message)
    }


}