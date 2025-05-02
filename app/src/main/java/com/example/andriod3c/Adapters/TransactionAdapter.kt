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

class TransactionAdapter(private var transactions: List<Transaction>) :
RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
        val transactionImage: ImageView = view.findViewById(R.id.transactionImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context

        if (transaction.amount >= 0) {
            holder.amount.text = "+ $%.2f".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.amount.text = "- $%.2f".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        holder.label.text = transaction.label

        if (transaction.image != null) {
            val bitmap = BitmapFactory.decodeByteArray(transaction.image, 0, transaction.image.size)
            holder.transactionImage.setImageBitmap(bitmap)
        } else {
            holder.transactionImage.setImageResource(R.drawable.ic_placeholder)
        }

    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setData(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }

    fun updateData(newTransactions: List<Transaction>) {
        setData(newTransactions)
    }
}
