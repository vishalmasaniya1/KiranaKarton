package com.vishal.kiranakarton.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vishal.kiranakarton.R
import com.vishal.kiranakarton.adapter.AllOrderAdapter
import com.vishal.kiranakarton.databinding.FragmentHomeBinding
import com.vishal.kiranakarton.databinding.FragmentMoreBinding
import com.vishal.kiranakarton.model.AllOrderModel


class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    private lateinit var list: ArrayList<AllOrderModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(layoutInflater)
        list = ArrayList()



        //val recyclerView2 = findViewById<RecyclerView>(R.id.recyclerView2)
        val preferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        Firebase.firestore.collection("allOrders")
            .whereEqualTo("userId", preferences.getString("number", "")!!)
            .get().addOnSuccessListener {
            list.clear()
            for (doc in it){
                val data = doc.toObject(AllOrderModel::class.java)
                list.add(data)

            }
            binding.recyclerView.adapter = AllOrderAdapter(list,requireContext())

        }
        return binding.root
    }


}