package com.ivan.work.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ivan.work.chatapp.adapters.FriendsRecyclerAdapter
import com.ivan.work.chatapp.models.Friend

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerViewFriends: RecyclerView
    private lateinit var btnFab: FloatingActionButton

    private lateinit var friendsRecyclerAdapter: FriendsRecyclerAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    private val friends = mutableListOf<Friend>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initialize()

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser!!


        btnFab.setOnClickListener {
            Intent(this@HomeActivity, UserSearchActivity::class.java).also {
                startActivity(it)
            }
        }

    }


    override fun onResume() {
        super.onResume()

        friendsRecyclerAdapter = FriendsRecyclerAdapter()
        recyclerViewFriends.layoutManager = LinearLayoutManager(this)
        recyclerViewFriends.adapter = friendsRecyclerAdapter

        //get friends
        db.collection("users").document(currentUser.uid)
            .collection("friends")
            .get()
            .addOnSuccessListener { result->
                if(result != null){
                    for(doc in result){
                        val friend = doc.toObject(Friend::class.java)
                        friend.uuid = doc.id
                        friends.add(friend)
                    }
                    friendsRecyclerAdapter.items = friends
                }
            }
            .addOnFailureListener {

            }

    }


    private fun initialize(){
        recyclerViewFriends = findViewById(R.id.recyclerView_friends)
        btnFab = findViewById(R.id.fab)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_logout-> {
                auth.signOut()
                Intent(this@HomeActivity,AuthenticationActivity::class.java).also {
                    startActivity(it)
                }
                finish()
            }
            R.id.item_settings -> {
                Intent(this@HomeActivity,SettingsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}