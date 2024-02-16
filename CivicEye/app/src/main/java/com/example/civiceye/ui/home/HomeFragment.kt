package com.example.civiceye.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.civiceye.R
import com.example.civiceye.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val spinner1: Spinner = binding.spinner1
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dropdown_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner1.adapter = adapter
        }

        
        // Initialize the subcategories list with the values from strings.xml
        val subcategories = resources.getStringArray(R.array.subcategory_dropdown_options).toMutableList()

        val subcategoryAutocomplete: AutoCompleteTextView = binding.autoCompleteTextView
    
        // Create the ArrayAdapter with the subcategories list
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subcategories)
        subcategoryAutocomplete.setAdapter(adapter)

        // Get the submit button
        val submitButton: Button = binding.submitButton

        // Set a click listener on the submit button
                submitButton.setOnClickListener {
            val newSubcategory = subcategoryAutocomplete.text.toString()
            if (newSubcategory.isNotEmpty() && !subcategories.contains(newSubcategory)) {
                // Add the new subcategory to the list and update the adapter
                subcategories.add(newSubcategory)
                Log.d("HomeFragment", "Subcategories after adding new one: $subcategories")
                adapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Adapter count after notifyDataSetChanged: ${adapter.count}")
                
                // Show a toast message
                Toast.makeText(requireContext(), "New subcategory added: $newSubcategory", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}