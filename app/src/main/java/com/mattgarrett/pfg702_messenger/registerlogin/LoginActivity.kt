package com.mattgarrett.pfg702_messenger.registerlogin

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.animation.doOnEnd
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mattgarrett.pfg702_messenger.ConversationsActivity
import com.mattgarrett.pfg702_messenger.R
import kotlinx.android.synthetic.main.activity_login.*
import java.sql.Time

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {
    lateinit var welcomeBack: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "PFG-Login"
        animationStart()

        btn_login_page_button.setOnClickListener{
            Log.d(TAG,"Launching Login Flow")
            loginFlow()
        }

        tv_login_page_i_need_to_create_account.setOnClickListener{
            Log.d(TAG,"Returning to Registration Page")
            val intent = Intent(this,NewUserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }

    fun loginFlow(){
        //grab entered input and run checks and process login
        val email = et_login_page_email.text.toString()
        val password = et_login_page_password.text.toString()
        //Safety Checks
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Fields Cannot be Blank",Toast.LENGTH_SHORT).show()
            return
        }
        //Firebase method to launch authenticator
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG,"Success: Login Verified")
                val intent = Intent(this, ConversationsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(TAG,"Failure: ${it.message}")
                Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
            }

    }



//    private fun fader(){
//        welcomeBack = LL_activity_login_welcome_back
//        val animator = ObjectAnimator.ofFloat(welcomeBack, View.ALPHA,0f)
//        animator.startDelay = 1500
//        animator.repeatCount = 0
//        animator.duration = 2200
//        animator.start()
//    }

    private fun animationStart(){
        val lLAnimation = AnimationUtils.loadAnimation(this,R.anim.slide_down)
        LL_activity_login.startAnimation(lLAnimation)


    }


}