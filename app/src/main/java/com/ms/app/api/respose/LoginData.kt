package com.ms.app.api.respose

data class LoginData(
    val code: Int,
    val message: String,
    val data: LoginResponse
)