package com.example.chatandroidapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatandroidapp.databinding.ChatItemBinding
import com.example.chatandroidapp.models.Chat

class ChatListAdapter : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    var items: List<Chat> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var itemClick: (Chat) -> Unit = {}
    fun itemClick(listener: (Chat) -> Unit) {
        itemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.chat = items[position]
        holder.itemView.setOnClickListener {
            itemClick(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding = ChatItemBinding.bind(view)

    }
}