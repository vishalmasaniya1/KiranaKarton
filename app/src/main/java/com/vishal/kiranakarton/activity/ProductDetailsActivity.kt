package com.vishal.kiranakarton.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.transition.SlideDistanceProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vishal.kiranakarton.MainActivity
import com.vishal.kiranakarton.R
import com.vishal.kiranakarton.databinding.ActivityProductDetailsBinding
import com.vishal.kiranakarton.databinding.LayoutProductItemBinding
import com.vishal.kiranakarton.roomdb.AppDatabase
import com.vishal.kiranakarton.roomdb.ProductDao
import com.vishal.kiranakarton.roomdb.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.Length

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var quantity: TextView
    private lateinit var addItems: ImageView
    private lateinit var removeItems: ImageView
    var totalQuantity = 1
    var totalPrice = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailsBinding.inflate(layoutInflater)


        setContentView(binding.root)
        quantity = findViewById<TextView>(R.id.quantity)
        addItems = findViewById<ImageView>(R.id.addItem)
        removeItems = findViewById<ImageView>(R.id.removeItem)
       /* addItems.setOnClickListener{
            totalQuantity++
            quantity.text = totalQuantity.toString()
        } */
        removeItems.setOnClickListener{
            if(totalQuantity>1){
                totalQuantity--
                quantity.text = totalQuantity.toString()
            }
        }

        getProductDetails(intent.getStringExtra("id"))
    }



    private fun getProductDetails(proId: String?) {

        Firebase.firestore.collection("products")
            .document(proId!!).get().addOnSuccessListener {
                val list = it.get("productImages") as ArrayList<String>
                val name = it.getString("productName")
                val productSp = it.getString("productSp")

                totalPrice = productSp!!.toInt() * totalQuantity
                val productDesc = it.getString("productDescription")
                binding.textView7.text = name
                binding.textView8.text = "Rs." + totalPrice.toString()
                addItems.setOnClickListener{
                    totalQuantity++
                    quantity.text = totalQuantity.toString()
                }
                binding.textView9.text = productDesc
                val slideList = ArrayList<SlideModel>()
                for (data in list){
                    slideList.add(SlideModel(data,ScaleTypes.CENTER_CROP))
                }

                cartAction(proId, name, productSp, it.getString("productCoverImg"))

                binding.imageSlider.setImageList(slideList)

            } .addOnFailureListener {
                Toast.makeText(this,"something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


    private fun cartAction(proId: String, name: String?, productSp: String?, coverImg: String?) {
        val productDao = AppDatabase.getInstance(this).productDao()
        if (productDao.isExit(proId)!= null){
            binding.textView10.text = "Go to Cart"
        } else{
            binding.textView10.text = "Add to Cart"
        }
        binding.textView10.setOnClickListener{
            if (productDao.isExit(proId) != null){
                openCart()
            }
            else{
                addToCart(productDao, proId, name, productSp, coverImg)
            }
        }
    }

    private fun addToCart(
        productDao: ProductDao,
        proId: String,
        name: String?,
        productSp: String?,
        coverImg: String?
    ) {
        val data = ProductModel(proId, name, coverImg, productSp)
        lifecycleScope.launch(Dispatchers.IO){
            productDao.insertProduct(data)
            binding.textView10.text = "Go to Cart"
        }

    }

    private fun openCart() {
        val preference = this.getSharedPreferences("info", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", true)
        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}