package com.example.appause.ui.reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.R
import com.example.appause.UserProfile

class FriendSearchViewAdapter : RecyclerView.Adapter<FriendSearchViewAdapter.ViewHolder>() {

     private var friendsSearchResult : List<UserProfile> = emptyList()

    fun updateData(lst : List<UserProfile>) {
        friendsSearchResult = lst
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.friends_search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.displayName.text = friendsSearchResult[i].name
        holder.email.text = friendsSearchResult[i].email
    }

    override fun getItemCount(): Int {
        return friendsSearchResult.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var displayName: TextView
        var email: TextView

        init {
            displayName = itemView.findViewById(R.id.displayName)
            email = itemView.findViewById(R.id.email)
        }
    }
}
