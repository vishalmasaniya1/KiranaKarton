package com.vishal.kiranakarton.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.vishal.kiranakarton.R
import com.vishal.kiranakarton.activity.AddressActivity
import com.vishal.kiranakarton.activity.CategoryActivity
import com.vishal.kiranakarton.adapter.CartAdapter
import com.vishal.kiranakarton.databinding.ActivityProductDetailsBinding
import com.vishal.kiranakarton.databinding.FragmentCartBinding
import com.vishal.kiranakarton.roomdb.AppDatabase
import com.vishal.kiranakarton.roomdb.ProductModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var list: ArrayList<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val dao = AppDatabase.getInstance(requireContext()).productDao()

        list = ArrayList()

        dao.getAllProducts().observe(requireActivity()){
            binding.cartRecycler.adapter = CartAdapter(requireContext(), it)

            list.clear()
            for (data in it){
                list.add(data.productId)
            }
            totalCost(it)
        }

        return binding.root
    }

    private fun totalCost(data: List<ProductModel>?) {
        var total =0
        for (item in data!!) {
            total += item.productSp!!.toInt()
        }
        binding.textView13.text = "Total item in cart is ${data.size}"
        binding.textView14.text = "Total cost : ₹$total"

        binding.checkout.setOnClickListener {
            val intent = Intent(context, AddressActivity::class.java)
            val b = Bundle()
            b.putStringArrayList("productIds",list)
            b.putString("totalCost", total.toString())
            intent.putExtras(b)
            startActivity(intent)
        }
    }


}