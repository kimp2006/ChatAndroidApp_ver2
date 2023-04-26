package com.example.chatandroidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatandroidapp.databinding.ActivityChatBinding
import com.example.chatandroidapp.models.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val firebaseRef =
        Firebase.database.getReference("chats")

    lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val id = intent?.getStringExtra("id")!!
        val messageRef = firebaseRef.child(id).child("messages")
        messageRef.addChildEventListener(childEventListener)

        binding.sendBtn.setOnClickListener {

            val key = messageRef.push().key!!
            messageRef.child(key).setValue(
                Message(
                    sender = auth.currentUser?.email!!,
                    date = Date().toString(),
                    message = binding.messageEditText.text.toString()
                )
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    binding.messageEditText.setText("")
                }
                else{
                    Toast.makeText(applicationContext, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backView.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun setInOutput(list: List<Message>) {
        var resultText = ""
        for (i in list) {
            resultText += "$i '\n"
        }
        binding.outputTextView.text = resultText
    }


    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            val message = dataSnapshot.getValue<Message>()
            val odlText = binding.outputTextView.text.toString()
            binding.outputTextView.text = "$odlText '\n'${message.toString()}"
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(
                applicationContext, "Failed to load comments.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRef.removeEventListener(childEventListener)
    }
}