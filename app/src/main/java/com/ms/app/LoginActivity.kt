package com.ms.app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ms.app.Home.MainActivity
import com.ms.app.api.Status
import com.ms.app.api.request.LoginRequest

class LoginActivity : AppCompatActivity() {

    private lateinit var etUserName: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var loginViewModel: LoginViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etUserName = findViewById(R.id.etUserName)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        if (getPrefStringData(this, "token") == null) {
            btnLogin.setOnClickListener {
                val email = etUserName.text.toString()
                val pass = etPassword.text.toString()

                val loginRequest = LoginRequest()
                loginRequest.email = email
                loginRequest.password = pass

                if (email == "") {
                    etUserName.error = "enter the email"
                } else {
                    if (pass == "") {
                        etPassword.error = "enter the password"
                    } else {
                        showLoader(this, "Login....")
                        loginViewModel.loginApi(loginRequest).observe(this) { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val apiResponse = resource.data?.body()
                                    if (apiResponse != null && apiResponse.code == 1) { // Assuming 1 indicates a successful login
                                        val data = apiResponse.data
                                        if (data != null) {
                                            setPrefStringData(this, "token", data.token)

                                            val userName = data.user?.name
                                            if (userName != null) {
                                                setPrefStringData(this, "userName", userName)
                                            }

                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Handle case where ApiResponse data is null
                                            Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        // Handle unsuccessful login
                                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                Status.ERROR -> {
                                    Toast.makeText(
                                        this,
                                        "Error: ${resource.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                Status.LOADING -> {
                                    hideLoader()
                                }
                            }
                        }
                    }
                }
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}