package com.ivan.work.chatapp

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ivan.work.chatapp.adapters.ChatRecyclerAdapter
import com.ivan.work.chatapp.models.Friend
import com.ivan.work.chatapp.models.Message
import com.ivan.work.chatapp.models.User

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerViewChatList: RecyclerView
    private lateinit var sentMessage: EditText
    private lateinit var fabSendMsg: FloatingActionButton

    private lateinit var chatRecyclerAdapter: ChatRecyclerAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    private val messages = mutableListOf<Message>()

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initialize()

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser!!


        val userUid = intent.getStringExtra("friend")!!
        db.collection("users").document(userUid).get()
            .addOnSuccessListener { result->
                if(result != null){
                    var receiver = result.toObject(User::class.java)!!
                    receiver?.let {
                        it.uuid = userUid
                        setUserData(it)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("ChatActivity","error getting user",it)
            }





    }

    private fun setUserData(user: User){
        supportActionBar?.title = user.pseudo

        chatRecyclerAdapter = ChatRecyclerAdapter()
        recyclerViewChatList.layoutManager = LinearLayoutManager(this)
        recyclerViewChatList.adapter = chatRecyclerAdapter

        loadSenderMessages(user)
        loadReceiverMessages(user)

        fabSendMsg.setOnClickListener {
            val msg = sentMessage.text.toString()
            if(msg.isNotEmpty()){
                val message = Message(
                    currentUser.uid,
                    user.uuid,
                    msg,
                    System.currentTimeMillis(),
                    false
                )

                //chatRecyclerAdapter.notifyDataSetChanged()
                sentMessage.text = null

                //hide keyboard
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(sentMessage.windowToken, 0)

                db.collection("messages").add(message)
                    .addOnSuccessListener {
                        recyclerViewChatList.scrollToPosition(messages.size - 1)
                    }
                    .addOnFailureListener {

                    }

                //add as friend
                val friend = Friend("",user.nom,user.prenom,user.pseudo,
                    user.picture ?:"", msg,System.currentTimeMillis())
                db.collection("users").document(currentUser.uid)
                    .collection("friends")
                    .document(user.uuid)
                    .set(friend)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

    private fun loadSenderMessages(user: User){
        db.collection("messages")
            .whereEqualTo("sender",currentUser.uid)
            .whereEqualTo("receiver",user.uuid)
            .orderBy("hour",Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if(error != null){
                    Log.e("ChatActivity","error getting messages",error)
                    return@addSnapshotListener
                }

                for(doc in value!!.documents){
                    var message = doc.toObject(Message::class.java)
                    message?.let {
                        it.isReceived = false
                        if(!messages.contains(it)){
                            messages.add(it)
                        }
                    }
                }

                if(messages.isNotEmpty()){
                    chatRecyclerAdapter.items = messages.sortedBy { it.hour } as MutableList<Message>
                    recyclerViewChatList.scrollToPosition(messages.size - 1)
                }
            }
    }


    private fun loadReceiverMessages(user: User){
        db.collection("messages")
            .whereEqualTo("sender",user.uuid)
            .whereEqualTo("receiver",currentUser.uid)
            .orderBy("hour",Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if(error != null){
                    Log.e("ChatActivity","error getting messages",error)
                    return@addSnapshotListener
                }

                for(doc in value!!.documents){
                    var message = doc.toObject(Message::class.java)
                    message?.let {
                        it.isReceived = true
                        if(!messages.contains(it)){
                            messages.add(it)
                        }
                    }
                }

                if(messages.isNotEmpty()){
                    chatRecyclerAdapter.items = messages.sortedBy { it.hour } as MutableList<Message>
                    recyclerViewChatList.scrollToPosition(messages.size - 1)
                }
            }
    }



    private fun initialize(){
        recyclerViewChatList = findViewById(R.id.recyclerView_chat_list)
        sentMessage = findViewById(R.id.sent_message)
        fabSendMsg = findViewById(R.id.fab_send_msg)
    }
}