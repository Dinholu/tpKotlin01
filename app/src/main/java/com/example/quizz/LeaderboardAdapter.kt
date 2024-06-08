package com.example.quizz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizz.data.Score

data class LeaderboardItem(val categoryName: String, val scores: List<Score>)

class LeaderboardAdapter(private val items: List<LeaderboardItem>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val scores: TextView = view.findViewById(R.id.scores)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.categoryName.text = item.categoryName
        holder.scores.text = if (item.scores.isEmpty()) {
            "No scores recorded"
        } else {
            item.scores.joinToString("\n") { "${it.player}: ${it.score}" }
        }
    }

    override fun getItemCount(): Int = items.size
}
