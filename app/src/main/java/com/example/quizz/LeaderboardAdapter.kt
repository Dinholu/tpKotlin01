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
        val score1: TextView = view.findViewById(R.id.score1)
        val score2: TextView = view.findViewById(R.id.score2)
        val score3: TextView = view.findViewById(R.id.score3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.categoryName.text = item.categoryName
        holder.score1.text = item.scores.getOrNull(0)?.let { "${it.player}: ${it.score}" } ?: "No score"
        holder.score2.text = item.scores.getOrNull(1)?.let { "${it.player}: ${it.score}" } ?: "No score"
        holder.score3.text = item.scores.getOrNull(2)?.let { "${it.player}: ${it.score}" } ?: "No score"
    }

    override fun getItemCount(): Int = items.size
}
