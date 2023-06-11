package com.vishal.kiranakarton.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vishal.kiranakarton.R
import com.vishal.kiranakarton.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var totalCost: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = this.getSharedPreferences("user", MODE_PRIVATE)

        totalCost = intent.getStringExtra("totalCost")!!

        loadUserInfo()

        binding.proceed.setOnClickListener {
            validateData(
                binding.userName.text.toString(),
                binding.userNumber.text.toString(),
                binding.userAddress.text.toString(),
                binding.userPincode.text.toString(),
                binding.userState.text.toString(),
                binding.userCity.text.toString()
            )
        }
    }

    private fun validateData(name: String, number: String, address: String, pinCode: String, state: String, city: String) {
        if (name.isEmpty() || number.isEmpty() || address.isEmpty() || pinCode.isEmpty() || state.isEmpty() || city.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
        else{
            storeData(pinCode, state, city, address)
        }
    }

    private fun storeData(pinCode: String, state: String, city: String, address: String) {
        val map = mutableMapOf<String, Any>()
        map["address"] = address
        map["state"] = state
        map["pinCode"] = pinCode
        map["city"] = city


        Firebase.firestore.collection("users")
            .document(preferences.getString("number", "")!!)
            .update(map).addOnSuccessListener {
                val b = Bundle()
                b.putStringArrayList("productIds",getIntent().getStringArrayListExtra("productIds"))
                b.putString("totalCost", totalCost)

                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtras(b)

                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }

    }

    private fun loadUserInfo() {

        Firebase.firestore.collection("users")
            .document(preferences.getString("number", "")!!)
            .get().addOnSuccessListener {
                binding.userName.setText(it.getString(("userName")))
                binding.userNumber.setText(it.getString(("userNumber")))
                binding.userAddress.setText(it.getString(("address")))
                binding.userCity.setText(it.getString(("city")))
                binding.userState.setText(it.getString(("state")))
                binding.userPincode.setText(it.getString(("pincode")))
            }


    }
}