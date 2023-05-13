package com.ivan.work.chatapp.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivan.work.chatapp.ChatActivity
import com.ivan.work.chatapp.R
import com.ivan.work.chatapp.models.User


class UsersRecyclerAdapter(
): RecyclerView.Adapter<UsersRecyclerAdapter.UsersViewHolder>(), Filterable {

    var items:MutableList<User> = mutableListOf()
        set(value) {
            field = value
            filteredList = value
            notifyDataSetChanged()
        }

    private var filteredList = mutableListOf<User>()


    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var txtUserFirstLetter: TextView
        private var txtUserName: TextView


        init {
            txtUserFirstLetter = itemView.findViewById(R.id.txt_user_first_letter)
            txtUserName = itemView.findViewById(R.id.txt_user_name)
        }


        @SuppressLint("SetTextI18n")
        fun bind(user: User){
            txtUserFirstLetter.text = user.nom[0].toString()
            txtUserName.text = "${user.nom} ${user.prenom}"

            itemView.setOnClickListener {
                Intent(itemView.context,ChatActivity::class.java).also {
                    it.putExtra("friend",user.uuid)
                    itemView.context.startActivity(it)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersRecyclerAdapter.UsersViewHolder {
        val eltView = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent, false)

        return UsersViewHolder(eltView)
    }


    override fun onBindViewHolder(holder: UsersRecyclerAdapter.UsersViewHolder, position: Int) {
        val user = filteredList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int  = filteredList.size


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if(charSearch.isEmpty()){
                    filteredList = items
                } else {
                    val resultList = items.filter {
                        val fullname = "${it.nom} ${it.prenom}"
                        fullname.lowercase().trim().contains(charSearch.lowercase().trim())
                    }
                    filteredList = resultList as MutableList<User>
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<User>
                notifyDataSetChanged()
            }
        }
    }
}