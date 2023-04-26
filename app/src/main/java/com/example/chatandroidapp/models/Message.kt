package com.example.chatandroidapp.models

data class Message(
    var date: String = "",
    var message: String = "",
    var sender: String = ""
)

{

    override fun toString(): String {
        return "$date : ($sender)  $message"
    }
}
