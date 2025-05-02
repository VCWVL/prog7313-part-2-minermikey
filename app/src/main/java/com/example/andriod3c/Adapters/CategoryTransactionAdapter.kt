package com.example.andriod3c.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.andriod3c.R
import com.example.andriod3c.TranactionDAO.Transaction


class CategoryTransactionAdapter(private var summaries: List<CategorySummary>) :
    RecyclerView.Adapter<CategoryTransactionAdapter.TransactionHolder>() {

    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView = view.findViewById(R.id.categoryNameTextView)
        val amount: TextView = view.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.budget_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val summary = summaries[position]
        holder.category.text = summary.category
        holder.amount.text = "$%.2f".format(summary.totalAmount)
    }

    override fun getItemCount(): Int = summaries.size

    fun updateData(newSummaries: List<CategorySummary>) {
        summaries = newSummaries
        notifyDataSetChanged()
    }
    // storing data for the sorting of the category

}
data class CategorySummary(
    val category: String,
    val totalAmount: Double
)
// gathering information on the categories for the displaying
fun summarizeTransactionsByCategory(transactions: List<Transaction>): List<CategorySummary> {
    return transactions
        .groupBy { it.category }
        .map { entry ->
            val total = entry.value.sumOf { it.amount }
            CategorySummary(entry.key, total)
        }
}


