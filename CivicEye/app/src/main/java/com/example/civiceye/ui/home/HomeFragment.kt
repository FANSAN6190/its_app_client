package com.example.civiceye.ui.home

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.civiceye.R
import com.example.civiceye.databinding.FragmentHomeBinding
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val categoryToSubcategories = mapOf(
        "Category 1" to listOf("Subcategory 1.1", "Subcategory 1.2", "Subcategory 1.3"),
        "Category 2" to listOf("Subcategory 2.1", "Subcategory 2.2", "Subcategory 2.3"),
        "Category 3" to listOf("Subcategory 3.1", "Subcategory 3.2", "Subcategory 3.3")
    )

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var imageView: ImageView
    private lateinit var cameraButton: Button

    private val takePictureResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        }
    }

    private lateinit var seekBar: SeekBar
    private lateinit var seekBarValue: TextView

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

        val subcategoryAutocomplete: AutoCompleteTextView = binding.autoCompleteTextView
        val spinner1: Spinner = binding.spinner1
        //val categories = arguments?.getStringArray("categories")?.toList() ?: listOf()
        val categories = categoryToSubcategories.keys.toList()
        spinner1.setSelection(0)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()

                // Update subcategories when a category is selected
                val subcategories = categoryToSubcategories[selectedCategory] ?: listOf()
                val subadapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subcategories)
                subcategoryAutocomplete.setAdapter(subadapter)
                subcategoryAutocomplete.isEnabled = true

                // Clear inputs on change in category
                subcategoryAutocomplete.text.clear()
                seekBar.progress = 0
                imageView.setImageDrawable(null)
                //this@HomeFragment.subcategories.clear()
                //this@HomeFragment.subcategories.addAll(subcategories)
                subadapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        val subcategories = arguments?.getStringArray("subcategories")?.toMutableList() ?: mutableListOf()
        val subadapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subcategories)
        subcategoryAutocomplete.setAdapter(subadapter)


        //image capture
        imageView = binding.imageView
        cameraButton = binding.cameraButton

        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureResult.launch(takePictureIntent)
            }
        }

        //slider
        seekBar = binding.seekBar
        seekBarValue = binding.seekBarValue
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current SeekBar value
                seekBarValue.text = "$progress"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts moving the SeekBar.
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops moving the SeekBar.
            }
        })

        val submitButton: Button = binding.submitButton
        submitButton.setOnClickListener {
            val newSubcategory = subcategoryAutocomplete.text.toString()
            if (newSubcategory.isNotEmpty() && !subcategories.contains(newSubcategory)) {
                subcategories.add(newSubcategory)
                Log.d("HomeFragment", "Subcategories after adding new one: $subcategories")
                subadapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Adapter count after notifyDataSetChanged: ${adapter.count}")
                
                Toast.makeText(requireContext(), "New subcategory added: $newSubcategory", Toast.LENGTH_SHORT).show()
            }

            //image to base64
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            /*
            can be converted back to normal image in java using-
            <
                String base64Image = "...";
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            >
            */

            //JSON Data
            val data = JSONObject().apply {
                put("category", spinner1.selectedItem)
                put("subcategory", newSubcategory)
                put("seekBarValue", seekBar.progress)
                put("image", base64Image) 
            }
            Log.d("HomeFragment", "Data: $data")
        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}