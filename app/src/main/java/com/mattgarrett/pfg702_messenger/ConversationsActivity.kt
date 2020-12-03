package com.mattgarrett.pfg702_messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mattgarrett.pfg702_messenger.registerlogin.NewUserActivity

private const val TAG = "ConversationsActivity"

class ConversationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "My Conversations"

        checkIfUserIsLoggedIn()

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