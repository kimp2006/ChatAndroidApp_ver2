package com.example.chatandroidapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.chatandroidapp.databinding.ActivityRegistrationBinding
import com.example.chatandroidapp.models.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class RegistrationActivity : AppCompatActivity() {

    private var binding: ActivityRegistrationBinding? = null
    private var auth: FirebaseAuth = Firebase.auth
    private var fireStore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.button?.setOnClickListener {

            val email = binding?.editTextTextEmailAddress?.text.toString()
            val password = binding?.editTextTextPassword?.text.toString()
            val name = binding?.editTextTextPersonName?.text.toString()
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    try{
                        val user = auth.createUserWithEmailAndPassword(email, password).await().user!!
                        val tasks = listOf(updateProfile(name), createUserInfo(user.uid, email, name))
                        tasks.joinAll()
                    }
                    catch (e: Exception){
                        Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    }

                }
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }

        }

    }

    private suspend fun updateProfile(name: String) = coroutineScope {
        launch(Dispatchers.IO) {
            auth.currentUser!!.updateProfile(userProfileChangeRequest {
                displayName = name
                photoUri = Uri.EMPTY
            })
        }
    }

    private suspend fun createUserInfo(id: String, email: String, name: String) = coroutineScope {
        launch(Dispatchers.IO) {
            fireStore.collection("user-info")
                .document(id)
                .set(UserInfo(id, email, name))
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}