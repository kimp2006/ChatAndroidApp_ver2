package com.example.chatandroidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.chatandroidapp.databinding.ActivityMainBinding
import com.example.chatandroidapp.models.Chat
import com.example.chatandroidapp.models.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = Firebase.auth
    private val fireStore = Firebase.firestore
    private val firebaseRef = Firebase.database.getReference("chats")
    private lateinit var adapter: ChatListAdapter

    private lateinit var chats: List<Chat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton.visibility = View.INVISIBLE
        initFloatingButton()
        adapter = ChatListAdapter()

        adapter.itemClick {
            val intent = Intent(applicationContext, ChatActivity::class.java)
            intent.putExtra("id", it.id)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter


        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val user = fireStore
                        .collection("user-info")
                        .document(auth.currentUser?.uid!!)
                        .get()
                        .await()
                        .toObject(UserInfo::class.java)!!

                    chats = user.chatsId.map {
                        firebaseRef.child(it)
                            .get()
                            .await()
                            .getValue(Chat::class.java) ?: Chat()
                    }
                }
                catch (e:Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            adapter.items = chats

            binding.progressBar.visibility = View.INVISIBLE
            binding.floatingActionButton.visibility = View.VISIBLE

        }



        binding.logoutContainer.setOnClickListener {
            auth.signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }

    }

    private fun initFloatingButton() {
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(applicationContext, CreateChatActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}