package com.example.chatandroidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.chatandroidapp.databinding.ActivityCreateChatBinding
import com.example.chatandroidapp.models.Chat
import com.example.chatandroidapp.models.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class CreateChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateChatBinding
    private val auth = Firebase.auth
    private val fireStore = Firebase.firestore
    private val firebaseRef =
        Firebase.database.getReference("chats")
    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.button3.setOnClickListener {
            val chat = Chat()
            val email = binding.editTextTextEmailAddress2.text.toString()
            val collections = fireStore.collection("user-info")
            lifecycleScope.launch {
                var chatId = ""
                withContext(Dispatchers.IO) {
                    try {
                        val task = collections.whereEqualTo("email", email).get().await()
                        if (task.isEmpty) {
                           showToast("Uer not found!!!")
                        } else {
                            val companion = task.documents.map {
                                it.toObject(UserInfo::class.java)!!
                            }.first()
                            val user = collections.document(auth.currentUser?.uid!!).get().await()
                                .toObject(UserInfo::class.java)!!

                            if (checkChatExist(companion.chatsId, user.chatsId)) {
                               showToast("Chat with this user already exist!!!")
                            } else {
                                chatId = firebaseRef.push().key!!
                                chat.id = chatId
                                firebaseRef.child(chatId).setValue(chat).await()
                                firebaseRef.child(chatId).child("messages").setValue(chat.messages)
                                    .await()
                                companion.chatsId.add(chatId)
                                user.chatsId.add(chatId)
                                collections.document(companion.id).set(companion).await()
                                collections.document(user.id).set(user).await()
                                goToChat(chatId)
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

        }
    }

    private suspend fun showToast(message: String) = withContext(Dispatchers.Main){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkChatExist(
        chatsId: MutableList<String>,
        chatsId2: MutableList<String>
    ): Boolean {
        for (i in chatsId) {
            for (j in chatsId2) {
                if (i == j) {
                    return true
                }
            }
        }
        return false
    }

    private suspend fun goToChat(id: String) = withContext(Dispatchers.Main) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    private suspend fun getUserInfoByEmail(email: String) = coroutineScope {

    }
}