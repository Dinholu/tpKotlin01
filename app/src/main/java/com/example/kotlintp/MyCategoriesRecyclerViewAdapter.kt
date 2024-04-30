package com.example.kotlintp

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.kotlintp.data.Categories
import com.example.kotlintp.databinding.FragmentItemBinding

class MyCategoriesRecyclerViewAdapter(
    private val values: List<Categories>
) : RecyclerView.Adapter<MyCategoriesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id.toString()
        holder.contentView.text = item.name
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val imageViewCategory: ImageView = binding.imageViewCategory
        val buttonParticipate: Button = binding.buttonParticipate

        fun bind(category: Categories) {
            idView.text = category.id.toString()
            contentView.text = category.name
            // Charger l'image de l'URL en utilisant Glide
            Glide.with(itemView.context)
                .load(category.imageUrl)
                .into(imageViewCategory)

            // Gérer le clic sur le bouton Participer
            buttonParticipate.setOnClickListener {
                // Ajoutez votre logique pour participer au quiz du thème sélectionné
            }
        }
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }


    }

}