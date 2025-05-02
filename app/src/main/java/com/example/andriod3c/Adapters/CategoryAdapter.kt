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

/**
 * Adapter for displaying a list of Category items in a RecyclerView.
 *
 * @param categories The list of Category objects to be displayed.
 * @param onCategoryClickListener Listener interface for handling clicks on category items.
 */
class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClickListener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    /**
     * Interface definition for a callback to be invoked when a category item is clicked.
     */
    interface OnCategoryClickListener {
        /**
         * Called when a category item in the RecyclerView is clicked.
         *
         * @param category The clicked Category object.
         */
        fun onCategoryClick(category: Category)
    }

    /**
     * ViewHolder class for representing individual category items in the RecyclerView.
     * It holds references to the ImageView for the category image and the TextView for the category name.
     *
     * @param itemView The inflated layout view for a single category item.
     */
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImageView: ImageView = itemView.findViewById(R.id.categoryImageView)
        val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)

        /**
         * Initializes the ViewHolder and sets an OnClickListener for the item view.
         * When an item is clicked, it retrieves the position and calls the onCategoryClick method
         * of the provided OnCategoryClickListener with the corresponding Category object.
         */
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClickListener.onCategoryClick(categories[position])
                }
            }
        }
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given view type to represent
     * an item. This method inflates the layout for a single category item and creates a new
     * CategoryViewHolder instance.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new CategoryViewHolder that holds a View of the layout for a category item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_button, parent, false) // Inflate the layout for a category button
        return CategoryViewHolder(itemView)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method updates
     * the contents of the ViewHolder to reflect the category item at the given position
     * in the `categories` list.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentCategory = categories[position]
        holder.categoryNameTextView.text = currentCategory.categoryName // Set the category name to the TextView

        // Load image from byte array if it exists
        currentCategory.imageBytes?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size) // Decode the byte array into a Bitmap
            holder.categoryImageView.setImageBitmap(bitmap) // Set the Bitmap to the ImageView
        } ?: run {
            holder.categoryImageView.setImageResource(R.drawable.ic_placeholder) // Set a placeholder image if imageBytes is null
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The size of the `categories` list.
     */
    override fun getItemCount() = categories.size
}