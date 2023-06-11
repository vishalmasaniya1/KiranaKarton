package com.vishal.kiranakarton.model

data class UserModel(
    val userName: String? = "",
    val userNumber: String? = "",
    val address: String? = "", // village ko address kar diya hai mene
    val state: String? ="",
    val city: String? ="",
    val pinCode: String? =""
)
