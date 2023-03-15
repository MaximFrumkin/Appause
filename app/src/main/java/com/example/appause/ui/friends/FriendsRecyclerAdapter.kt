package com.example.appause.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.R

class FriendsRecyclerAdapter : RecyclerView.Adapter<FriendsRecyclerAdapter.ViewHolder>() {
    private val description = arrayOf("Total Screen Time", "Social", "Productivity", "Video", "Entertainment", "Movies")

    private val usageTime = arrayOf("1 / 4 h", "1 / 0.5 h", "0 / 3 h", "0.5 / 0.5 h", "4 / 2 h", " 5 / 2 h")

    private val time = arrayOf(10, 5, 4, 1, 5, 9)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.reports_list_item, parent, false)
            return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: FriendsRecyclerAdapter.ViewHolder, i: Int) {
        val image : Int = if (time[i] > 5) {
            R.drawable.ic_hourglass_bottom
        } else if (time[i] == 5) {
            R.drawable.ic_hourglass_full
        } else {
            R.drawable.ic_hourglass_top
        }

        holder.hourglass.setImageResource(image)
        holder.description.text = description[i]
        holder.usageTime.text = usageTime[i]
    }

    override fun getItemCount(): Int {
        return description.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var hourglass: ImageView
        var description: TextView
        var usageTime: TextView

        init {
            hourglass = itemView.findViewById(R.id.hourglass)
            description = itemView.findViewById(R.id.description)
            usageTime = itemView.findViewById(R.id.usageTime)
        }
    }
}