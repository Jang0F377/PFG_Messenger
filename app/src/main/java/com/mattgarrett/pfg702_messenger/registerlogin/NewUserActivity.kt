package com.mattgarrett.pfg702_messenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mattgarrett.pfg702_messenger.ConversationsActivity
import com.mattgarrett.pfg702_messenger.R
import com.mattgarrett.pfg702_messenger.dataclass.UserData
import kotlinx.android.synthetic.main.activity_new_user.*
import java.util.*

private const val TAG = "NewUserActivity"

class NewUserActivity : AppCompatActivity() {
    var selectedPhotoURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)
        supportActionBar?.title = "PFG-Welcome"

        btn_registration_page_create_my_account.setOnClickListener {
            performRegistrationFlow()
            Log.d(TAG,"Registration Flow")

        }

        btn_registration_page_add_your_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        tv_already_have_an_acct.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
                Log.d(TAG,"Photo Selected")
                selectedPhotoURI = data.data
                val image = ImageDecoder.createSource(contentResolver,selectedPhotoURI!!)
                val imageSource = ImageDecoder.decodeDrawable(image)

                circle_image_view_select_photo.background = imageSource
                btn_registration_page_add_your_photo.alpha = 0f
            }

        }
    }

    private fun performRegistrationFlow() {
        val firstName = et_registration_page_first_name.text.toString()
        val lastName = et_registration_page_last_name.text.toString()
        val email = et_registration_page_email.text.toString()
        val password = et_registration_page_password.text.toString()
        val passwordConfirmation = et_registration_page_confirm_password.text.toString()


        // Init Checks for blank fields or mismatched passwords

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email/Password Cannot be Empty", Toast.LENGTH_SHORT).show()
            return
        } else if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Name Cannot be empty", Toast.LENGTH_SHORT).show()
            return
        } else if (password != passwordConfirmation) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }


        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener


                Log.d(TAG,"Success: User Created")
                saveUserImageToFirebase()
            }
            .addOnFailureListener {
                val message = it.message.toString()
                Log.d(TAG,message)
            }
    }

    private fun saveUserImageToFirebase() {
        if (selectedPhotoURI == null) return

        val filename = UUID.randomUUID().toString()
        val storage = Firebase.storage
        val storagePath = "USERIMAGES/$filename"
        val storageRef = storage.getReference(storagePath)
        storageRef.putFile(selectedPhotoURI!!)
            .addOnSuccessListener { it ->
                Log.d(TAG,"Success: URL = ${it.metadata?.path}")
                storageRef.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                Log.d(TAG,"Failure: ${it.message}")

            }
    }

//    private fun coroutine(function: Any) = CoroutineScope(Dispatchers.IO).launch {
//        function
//        Log.d(TAG,"Launching function in Thread ${Thread.currentThread()}")
//    }

    private fun saveUserToFirebaseDatabase(profileImageURL: String?) {
        val uid = Firebase.auth.uid
        val firstName = et_registration_page_first_name.text.toString().capitalize()
        val lastName = et_registration_page_last_name.text.toString().capitalize()
        val email = et_registration_page_email.text.toString()
        val database = Firebase.database
        val databasePath = "USERS/$uid"
        val databaseRef = database.getReference(databasePath)
        val user = UserData(firstName, lastName, email, profileImageURL,uid)
        databaseRef.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG,"Success: Saved ${lastName.capitalize()}, ${firstName.capitalize()}")
                val intent = Intent(this, ConversationsActivity::class.java)
                startActivity(intent)
            }
    }


}