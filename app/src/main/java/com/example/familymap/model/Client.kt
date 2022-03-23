package com.example.familymap.model

import android.util.Log
import com.google.gson.GsonBuilder
import request_result.*
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.Throws

private const val TAG_CLIENT = "Client"

/*
	The Client class shows how to call a web API operation from
	a Java program.  This is typical of how your Android client
	app will call the web API operations of your server.
*/
class Client(private var serverHost: String?, private var serverPort: String?) {
    init {
        var serverHost = serverHost
        var serverPort = serverPort
    }

    // The login method calls the server's "/user/login" operation to
    public fun login(request: loginRequest?): loginResult? {
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val url = URL("http://$serverHost:$serverPort/user/login")
            // Start constructing our HTTP request
            val http = url.openConnection() as HttpURLConnection
            http.requestMethod = "POST"
            http.doOutput = true // There is a request body

            http.connect()

            val reqData = gson.toJson(request)
            val reqBody = http.outputStream
            writeString(reqData, reqBody)
            reqBody.close()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                // println("Login Success")
                Log.d(TAG_CLIENT, "Login Success")
                val resp = http.inputStream
                val respData = readString(resp)

                return gson.fromJson(respData, loginResult::class.java) as loginResult

            } else {

                Log.d(TAG_CLIENT, "Failed to Login:" + http.responseMessage)
                // Get the error stream containing the HTTP response body (if any)
                val respBody = http.errorStream

                // Extract data from the HTTP response body
                val respData =
                    readString(respBody)

                // Display the data returned from the server
                // println(respData)
                Log.d(TAG_CLIENT, respData)
                return gson.fromJson(respData, loginResult::class.java) as loginResult
            }
        } catch (e: IOException) {
            // An exception was thrown, so display the exception's stack trace
            Log.e(TAG_CLIENT, "Error when trying to login", e)
        }
        return null
    }

    fun register(request: registerRequest): registerResult? {
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val url = URL("http://$serverHost:$serverPort/user/register")
            // Start constructing our HTTP request
            val http = url.openConnection() as HttpURLConnection
            http.requestMethod = "POST"
            http.doOutput = true // There is a request body

            http.connect()

            val reqData = gson.toJson(request)
            val reqBody = http.outputStream
            writeString(reqData, reqBody)
            reqBody.close()

            if (http.responseCode == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                // println("Login Success")
                Log.d(TAG_CLIENT, "Register Success")
                val resp = http.inputStream
                val respData = readString(resp)

                return gson.fromJson(respData, registerResult::class.java) as registerResult

            } else {

                Log.d(TAG_CLIENT, "Failed to Register:" + http.responseMessage)
                // Get the error stream containing the HTTP response body (if any)
                val respBody = http.errorStream

                // Extract data from the HTTP response body
                val respData =
                    readString(respBody)

                // Display the data returned from the server
                // println(respData)
                Log.d(TAG_CLIENT, respData)
                return gson.fromJson(respData, registerResult::class.java) as registerResult
            }
        } catch (e: IOException) {
            // An exception was thrown, so display the exception's stack trace
            Log.e(TAG_CLIENT, "Error when trying to register", e)
        }
        return null

    }

    fun getPeople(authtoken: String): allPersonResult? {
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val url = URL("http://$serverHost:$serverPort/person")
            // Start constructing our HTTP request
            val http = url.openConnection() as HttpURLConnection
            http.requestMethod = "GET"
            http.doOutput = false // There is a request body

            http.setRequestProperty("Authorization", authtoken)

            http.connect()


            if (http.responseCode == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                // println("Login Success")
                Log.d(TAG_CLIENT, "getPeople Success")
                val resp = http.inputStream
                val respData = readString(resp)

                return gson.fromJson(respData, allPersonResult::class.java) as allPersonResult

            } else {

                Log.d(TAG_CLIENT, "Failed to Register:" + http.responseMessage)
                // Get the error stream containing the HTTP response body (if any)
                val respBody = http.errorStream

                // Extract data from the HTTP response body
                val respData =
                    readString(respBody)

                // Display the data returned from the server
                // println(respData)
                Log.d(TAG_CLIENT, respData)
                return gson.fromJson(respData, allPersonResult::class.java) as allPersonResult
            }
        } catch (e: IOException) {
            // An exception was thrown, so display the exception's stack trace
            Log.e(TAG_CLIENT, "Error when trying get all persons", e)
        }
        return null

    }

    fun getEvents(authtoken: String): allEventResult? {
        val builder = GsonBuilder()
        builder.setPrettyPrinting()
        val gson = builder.create()
        try {
            val url = URL("http://$serverHost:$serverPort/event")
            // Start constructing our HTTP request
            val http = url.openConnection() as HttpURLConnection
            http.requestMethod = "GET"
            http.doOutput = false // There is a request body
            http.setRequestProperty("Authorization", authtoken)
            http.connect()


            if (http.responseCode == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                // println("Login Success")
                Log.d(TAG_CLIENT, "getEvents Success")
                val resp = http.inputStream
                val respData = readString(resp)

                return gson.fromJson(respData, allEventResult::class.java) as allEventResult

            } else {

                Log.d(TAG_CLIENT, "Failed to get events:" + http.responseMessage)
                // Get the error stream containing the HTTP response body (if any)
                val respBody = http.errorStream

                // Extract data from the HTTP response body
                val respData =
                    readString(respBody)

                // Display the data returned from the server
                // println(respData)
                Log.d(TAG_CLIENT, respData)
                return gson.fromJson(respData, allEventResult::class.java) as allEventResult
            }
        } catch (e: IOException) {
            // An exception was thrown, so display the exception's stack trace
            Log.e(TAG_CLIENT, "Error when trying get all events", e)
        }
        return null

    }

    companion object {
        /*
        The readString method shows how to read a String from an InputStream.
    */
        @Throws(IOException::class)
        private fun readString(`is`: InputStream): String {
            val sb = StringBuilder()
            val sr = InputStreamReader(`is`)
            val buf = CharArray(1024)
            var len: Int
            while (sr.read(buf).also { len = it } > 0) {
                sb.append(buf, 0, len)
            }
            return sb.toString()
        }

        /*
        The writeString method shows how to write a String to an OutputStream.
    */
        @Throws(IOException::class)
        private fun writeString(str: String, os: OutputStream) {

            val sw = OutputStreamWriter(os)
            sw.write(str)
            sw.flush()
        }
    }
}