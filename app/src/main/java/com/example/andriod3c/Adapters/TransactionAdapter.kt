package com.example.andriod3c.Adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.andriod3c.R
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.andriod3c.TranactionDAO.Transaction

/**
 * Adapter for displaying a list of Transaction items in a RecyclerView.
 * Each item shows the transaction label, amount (with color coding for income/expense), and an associated image.
 *
 * @param transactions The list of Transaction objects to be displayed.
 */
class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    /**
     * ViewHolder class for representing individual transaction items in the RecyclerView.
     * It holds references to the TextView for the label, TextView for the amount, and ImageView for the transaction image.
     *
     * @param view The inflated layout view for a single transaction item.
     */
    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
        val transactionImage: ImageView = view.findViewById(R.id.transactionImage)
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given view type to represent
     * an item. This method inflates the layout for a single transaction item and creates a new
     * TransactionHolder instance.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new TransactionHolder that holds a View of the layout for a transaction item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false) // Inflates the layout for a transaction item
        return TransactionHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method updates
     * the contents of the ViewHolder to reflect the Transaction item at the given position
     * in the `transactions` list.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context // Get the context for accessing resources

        // Format and set the transaction amount with color coding for positive (income) and negative (expense) amounts
        if (transaction.amount >= 0) {
            holder.amount.text = "+ $%.2f".format(transaction.amount) // Format as positive with a "+" sign
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green)) // Set text color to green for income
        } else {
            holder.amount.text = "- $%.2f".format(Math.abs(transaction.amount)) // Format as negative with a "-" sign and absolute value
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red)) // Set text color to red for expense
        }

        // Set the transaction label
        holder.label.text = transaction.label

        // Load and display the transaction image if it exists
        if (transaction.image != null) {
            val bitmap = BitmapFactory.decodeByteArray(transaction.image, 0, transaction.image.size) // Decode the byte array into a Bitmap
            holder.transactionImage.setImageBitmap(bitmap) // Set the Bitmap to the ImageView
        } else {
            holder.transactionImage.setImageResource(R.drawable.ic_placeholder) // Set a placeholder image if no image is available
        }

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The size of the `transactions` list.
     */
    override fun getItemCount(): Int {
        return transactions.size
    }

    /**
     * Sets the data for the adapter with a new list of Transaction items and refreshes the RecyclerView.
     *
     * @param transactions The new list of Transaction items to be displayed.
     */
    fun setData(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }

    /**
     * Updates the data displayed by the adapter with a new list of Transaction items
     * and refreshes the RecyclerView to reflect the changes. This is an alias for the `setData` method.
     *
     * @param newTransactions The new list of Transaction items to be displayed.
     */
    fun updateData(newTransactions: List<Transaction>) {
        setData(newTransactions)
    }
}
