package com.example.chatandroidapp.models

data class Chat(
    var id: String = "",
    var messages: HashMap<String, Message> = hashMapOf()
)
{

    fun lastMessage(): Message {
        if (messages.isEmpty()) {
            return Message("", "Create your first chat", "")
        }
        return messages.map {  it.value }.maxBy { m -> m.date}
    }
}


