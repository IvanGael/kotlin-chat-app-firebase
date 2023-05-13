package com.ivan.work.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ivan.work.chatapp.adapters.UsersRecyclerAdapter
import com.ivan.work.chatapp.models.User

class UserSearchActivity : AppCompatActivity() {

    private lateinit var userSearch: EditText
    private lateinit var recyclerViewUsers: RecyclerView

    private lateinit var usersRecyclerAdapter: UsersRecyclerAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)

        initialize()

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser!!

        val users = mutableListOf<User>()

        usersRecyclerAdapter = UsersRecyclerAdapter()
        recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        recyclerViewUsers.adapter = usersRecyclerAdapter

        db.collection("users")
            .whereNotEqualTo("email",currentUser.email)
            .get()
            .addOnSuccessListener { result->
                if(result != null){
                    for (doc in result){
                        val uuid = doc.id
                        val email = doc.getString("email")
                        val nom = doc.getString("nom")
                        val prenom = doc.getString("prenom")
                        users.add(User(uuid,nom ?: "",prenom ?: "","",email ?: "",""))
                    }
                    usersRecyclerAdapter.items = users
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Oups!Users can not be loaded", Toast.LENGTH_LONG).show()
            }


        userSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne rien faire avant le changement
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Action à effectuer à chaque changement de texte dans l'EditText
                usersRecyclerAdapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Ne rien faire après le changement
            }
        })
    }

    private fun initialize(){
        userSearch = findViewById(R.id.user_search)
        recyclerViewUsers = findViewById(R.id.recyclerView_users)
    }
}