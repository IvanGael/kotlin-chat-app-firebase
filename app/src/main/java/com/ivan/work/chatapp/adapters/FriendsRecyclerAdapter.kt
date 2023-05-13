package com.ivan.work.chatapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.ivan.work.chatapp.ChatActivity
import com.ivan.work.chatapp.R
import com.ivan.work.chatapp.models.Friend
import java.text.SimpleDateFormat
import java.util.*

class FriendsRecyclerAdapter(
): RecyclerView.Adapter<FriendsRecyclerAdapter.FriendsViewHolder>() {

    var items:MutableList<Friend> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var friendAvatar: ShapeableImageView
        private var friendPseudo: TextView
        private var friendMsg: TextView
        private var msgHour: TextView

        init {
            friendAvatar = itemView.findViewById(R.id.img_view_friend_avatar)
            friendPseudo = itemView.findViewById(R.id.txt_friend_pseudo)
            friendMsg = itemView.findViewById(R.id.txt_friend_msg)
            msgHour = itemView.findViewById(R.id.txt_chat_hour)
        }


        fun bind(friend: Friend){
            friendPseudo.text = friend.pseudo
            friendMsg.text = friend.lastMsg
            Glide.with(itemView.context)
                .load(friend.avatar)
                .into(friendAvatar)

            val outputFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = outputFormatTime.format(Date(friend.timestamp))
            msgHour.text = formattedTime

            itemView.setOnClickListener {
                Intent(itemView.context, ChatActivity::class.java).also {
                    it.putExtra("friend", friend.uuid)
                    itemView.context.startActivity(it)
                }

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val eltView = LayoutInflater.from(parent.context).inflate(R.layout.item_friends,parent, false)

        return FriendsViewHolder(eltView)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = items[position]
        holder.bind(friend)
    }

    override fun getItemCount(): Int  = items.size

}