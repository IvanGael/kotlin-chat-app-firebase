package com.ivan.work.chatapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivan.work.chatapp.R
import com.ivan.work.chatapp.models.Message
import java.text.SimpleDateFormat
import java.util.*


class ChatRecyclerAdapter(
): RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder>() {

    var items:MutableList<Message> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var txtMsg: TextView
        private var txtHour: TextView

        init {
            txtMsg = itemView.findViewById(R.id.txt_msg)
            txtHour = itemView.findViewById(R.id.txt_hour)
        }


        fun bind(message: Message){
            txtMsg.text = message.msg

            val outputFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = outputFormatTime.format(Date(message.hour))

            txtHour.text = formattedTime

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRecyclerAdapter.ChatViewHolder {
        val eltView = LayoutInflater.from(parent.context).inflate(viewType,parent, false)

        return ChatViewHolder(eltView)
    }

    override fun getItemViewType(position: Int): Int {
        val res = when(items[position].isReceived){
            true -> R.layout.item_chat_left
            false -> R.layout.item_chat_right
        }
        return res
    }

    override fun onBindViewHolder(holder: ChatRecyclerAdapter.ChatViewHolder, position: Int) {
        val message = items[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int  = items.size

}