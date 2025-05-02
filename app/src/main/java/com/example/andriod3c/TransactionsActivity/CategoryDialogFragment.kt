package com.example.andriod3c.TransactionsActivity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.andriod3c.Adapters.CategoryAdapter
import com.example.andriod3c.R
import com.example.andriod3c.CatogoryDAO.Category // Make sure this import is correct
import com.example.andriod3c.Databaseimport.CategoryDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CategoryDialogFragment(private val categorySelectionListener: CategorySelectionListener) : DialogFragment() {

    // Add a default constructor
    constructor() : this(object : CategorySelectionListener {
        override fun onCategorySelected(categoryName: String) {
            // Optional:  Add default implementation if needed, or leave empty.
        }
    })

    interface CategorySelectionListener {
        fun onCategorySelected(categoryName: String)
    }

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var addCategoryButton: ImageButton
    private lateinit var database: CategoryDatabase // Declare the database

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.fragment_category_list, null)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        addCategoryButton = view.findViewById(R.id.addCategoryButton)

        database = CategoryDatabase.getDatabase(requireContext()) // Initialize the database
        val categoryDao = database.category()

        CoroutineScope(Dispatchers.IO).launch {
            val categories = categoryDao.getAllCategories()
            withContext(Dispatchers.Main) {
                categoryAdapter = CategoryAdapter(categories,
                    object : CategoryAdapter.OnCategoryClickListener {
                        override fun onCategoryClick(category: Category) {
                            categorySelectionListener.onCategorySelected(category.categoryName)
                            dismiss()
                        }
                    })
                categoryRecyclerView.adapter = categoryAdapter
            }
        }
        addCategoryButton.setOnClickListener {
            val intent = Intent(requireContext(), AddCategoryActivity::class.java)
            startActivity(intent)

        }

        return androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Select Category")
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close() // Close the database when the fragment is destroyed.
    }
}