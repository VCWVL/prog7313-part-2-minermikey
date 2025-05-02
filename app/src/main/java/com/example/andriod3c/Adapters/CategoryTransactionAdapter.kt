package com.example.andriod3c.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.andriod3c.R
import com.example.andriod3c.TranactionDAO.Transaction

/**
 * Adapter for displaying a list of CategorySummary items in a RecyclerView.
 * Each item shows the category name and the total amount associated with that category.
 *
 * @param summaries The list of CategorySummary objects to be displayed.
 */
class CategoryTransactionAdapter(private var summaries: List<CategorySummary>) :
    RecyclerView.Adapter<CategoryTransactionAdapter.TransactionHolder>() {

    /**
     * ViewHolder class for representing individual category summary items in the RecyclerView.
     * It holds references to the TextView for the category name and the TextView for the total amount.
     *
     * @param view The inflated layout view for a single category summary item.
     */
    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView = view.findViewById(R.id.categoryNameTextView)
        val amount: TextView = view.findViewById(R.id.amount)
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given view type to represent
     * an item. This method inflates the layout for a single category summary item and creates a new
     * TransactionHolder instance.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new TransactionHolder that holds a View of the layout for a category summary item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.budget_layout, parent, false) // Inflate the layout for displaying category and amount
        return TransactionHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method updates
     * the contents of the ViewHolder to reflect the CategorySummary item at the given position
     * in the `summaries` list.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val summary = summaries[position]
        holder.category.text = summary.category // Set the category name to the TextView
        holder.amount.text = "$%.2f".format(summary.totalAmount) // Format and set the total amount to the TextView
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The size of the `summaries` list.
     */
    override fun getItemCount(): Int = summaries.size

    /**
     * Updates the data displayed by the adapter with a new list of CategorySummary items
     * and refreshes the RecyclerView to reflect the changes.
     *
     * @param newSummaries The new list of CategorySummary items to be displayed.
     */
    fun updateData(newSummaries: List<CategorySummary>) {
        summaries = newSummaries
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }

}

/**
 * Data class representing a summary of transactions for a specific category.
 *
 * @property category The name of the category.
 * @property totalAmount The total amount of transactions within that category.
 */
data class CategorySummary(
    val category: String,
    val totalAmount: Double
)

/**
 * Function to summarize a list of Transaction objects by their category, calculating the
 * total amount for each category.
 *
 * @param transactions The list of Transaction objects to summarize.
 * @return A list of CategorySummary objects, where each object contains a category name and its total amount.
 */
fun summarizeTransactionsByCategory(transactions: List<Transaction>): List<CategorySummary> {
    return transactions
        .groupBy { it.category } // Group the transactions by their category
        .map { entry ->
            val total = entry.value.sumOf { it.amount } // Calculate the sum of amounts for each category
            CategorySummary(entry.key, total) // Create a CategorySummary object with the category and total amount
        }
}