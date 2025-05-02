package com.example.andriod3c.Adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.andriod3c.CatogoryDAO.Category
import com.example.andriod3c.R

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClickListener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImageView: ImageView = itemView.findViewById(R.id.categoryImageView)
        val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClickListener.onCategoryClick(categories[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_button, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentCategory = categories[position]
        holder.categoryNameTextView.text = currentCategory.categoryName

        // Load image from byte array
        currentCategory.imageBytes?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.categoryImageView.setImageBitmap(bitmap)
        } ?: run {
            holder.categoryImageView.setImageResource(R.drawable.ic_placeholder) // Set a default placeholder
        }
    }

    override fun getItemCount() = categories.size
}