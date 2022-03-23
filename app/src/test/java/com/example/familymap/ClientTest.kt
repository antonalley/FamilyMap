package com.example.familymap

import com.example.familymap.model.Client
import org.junit.Assert
import org.junit.Test
import request_result.loginRequest

class ClientTest {
    @Test
    fun passTest() {
        val client = Client("127.0.0.1", "8080")
        val request = loginRequest()
        request.password = "password"
        request.username = "username"
        val result = client.login(request)
        Assert.assertTrue(result!!.success)
        println(result.message)
    }
}