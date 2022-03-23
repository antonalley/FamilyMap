package com.example.familymap

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import com.example.familymap.model.Client
import com.example.familymap.model.DataCache
import request_result.*
import java.util.concurrent.Executors


private const val TAG_LOGIN_FRAGMENT = "LoginFragment"

private const val KEY_AUTHTOKEN = "com.example.familymap.authtoken"
private const val KEY_LOGIN_SUCCESS = "com.example.familymap.login_success"
private const val KEY_REGISTER_SUCCESS = "com.example.familymap.register_success"

class LoginFragment : Fragment() {
    private var rootView: View? = null
    private lateinit var serverHostText: EditText
    private lateinit var serverPortText: EditText
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var firstNameText: EditText
    private lateinit var lastNameText: EditText
    private lateinit var emailText: EditText
    private lateinit var isMale: RadioButton
    private lateinit var isFemale: RadioButton
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d(TAG_LOGIN_FRAGMENT, "Login Fragment onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        Log.d(TAG_LOGIN_FRAGMENT, "Login Fragment onCreateView")
        rootView =  inflater.inflate(R.layout.fragment_login, container, false)
        serverHostText = rootView!!.findViewById(R.id.serverhost)
        serverPortText = rootView!!.findViewById(R.id.serverport)
        usernameText = rootView!!.findViewById(R.id.username)
        passwordText = rootView!!.findViewById(R.id.password)
        firstNameText = rootView!!.findViewById(R.id.firstName)
        lastNameText = rootView!!.findViewById(R.id.lastName)
        emailText = rootView!!.findViewById(R.id.email)
        isMale = rootView!!.findViewById(R.id.radioMale)
        isFemale = rootView!!.findViewById(R.id.radioFemale)
        loginButton = rootView!!.findViewById(R.id.login)
        registerButton= rootView!!.findViewById(R.id.register)
        loadingProgressBar = rootView!!.findViewById(R.id.loading)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG_LOGIN_FRAGMENT, "starting onViewCreated")

        fun checkAllTextViews() : Boolean {
            if (serverHostText.text.toString() != "" &&
                serverPortText.text.toString() != "" &&
                usernameText.text.toString() != "" &&
                passwordText.text.toString() != "" &&
                firstNameText.text.toString() != "" &&
                lastNameText.text.toString() != "" &&
                emailText.text.toString() != "") {
                return true
            }
            return false
        }

        fun checkTextForLogin() : Boolean {
            if (serverHostText.text.toString() != "" &&
                serverPortText.text.toString() != "" &&
                usernameText.text.toString() != "" &&
                passwordText.text.toString() != ""){
                return true
            }
            return false
        }

        serverHostText.doAfterTextChanged {
            loginButton.isEnabled = checkTextForLogin()
            registerButton.isEnabled = checkAllTextViews()
        }
        serverPortText.doAfterTextChanged {
            loginButton.isEnabled = checkTextForLogin()
            registerButton.isEnabled = checkAllTextViews()
        }
        usernameText.doAfterTextChanged {
            loginButton.isEnabled = checkTextForLogin()
            registerButton.isEnabled = checkAllTextViews()
        }
        passwordText.doAfterTextChanged {
            loginButton.isEnabled = checkTextForLogin()
            registerButton.isEnabled = checkAllTextViews()
        }
        firstNameText.doAfterTextChanged { registerButton.isEnabled = checkAllTextViews() }
        lastNameText.doAfterTextChanged { registerButton.isEnabled = checkAllTextViews() }
        emailText.doAfterTextChanged { registerButton.isEnabled = checkAllTextViews() }


        loginButton.setOnClickListener {
            Log.d(TAG_LOGIN_FRAGMENT, "Login Button Pressed : " + usernameText.text.toString())
            loadingProgressBar.visibility = View.VISIBLE

            // Create a Handler for Accessing the Login Task:
            var uiThreadMessageHandler = @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    val bundle: Bundle = msg.data
                    // DO anything that would require the UI
                    val success: Boolean = bundle.getBoolean(KEY_LOGIN_SUCCESS, false)

                    val datacache = DataCache

                    showLoginSuccess(success,
                        datacache.persons?.get(datacache.userPersonID)?.firstName,
                        datacache.persons?.get(datacache.userPersonID)?.lastName
                    ) // Will show the toast

                    loadingProgressBar.visibility = View.INVISIBLE
                }
            }
            val task = LoginTask(uiThreadMessageHandler,serverHostText.text.toString(), serverPortText.text.toString(),
            usernameText.text.toString(), passwordText.text.toString())
            val executor = Executors.newSingleThreadExecutor()
            executor.submit(task)
        }

        registerButton.setOnClickListener {
            Log.d(TAG_LOGIN_FRAGMENT, "Register Button Pressed : " + usernameText.text.toString())
            loadingProgressBar.visibility = View.VISIBLE
            // Create a Handler for Accessing the Register Task:
            var uiThreadMessageHandler = @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    val bundle: Bundle = msg.data
                    // DO anything that would require the UI
                    val success: Boolean = bundle.getBoolean(KEY_REGISTER_SUCCESS, false)

                    val datacache = DataCache
                    showRegisterSuccess(success,
                        datacache.persons?.get(datacache.userPersonID)?.firstName,
                        datacache.persons?.get(datacache.userPersonID)?.lastName
                    ) // Will show the toast

                    loadingProgressBar.visibility = View.INVISIBLE
                }
            }
            val gender : String = when {
                isFemale.isChecked -> "f"
                isMale.isChecked -> "m"
                else -> "m"
            }
            val task = RegisterTask(uiThreadMessageHandler,serverHostText.text.toString(),
                serverPortText.text.toString(),
                usernameText.text.toString(),
                passwordText.text.toString(),
                firstNameText.text.toString(),
                lastNameText.text.toString(),
                emailText.text.toString(),
                gender)
            val executor = Executors.newSingleThreadExecutor()
            executor.submit(task)

        }
    }


    class RegisterTask(private val messageHandler: Handler,
                        val ip : String,
                        val port : String,
                        val username: String,
                        val password: String,
                       val firstName: String,
                       val lastName: String,
                       val email: String,
                       val gender: String
    ) : Runnable {
        override fun run() {
            val client = Client(ip, port)
            val request = registerRequest()
            request.username = username
            request.password = password
            request.email = email
            request.firstName = firstName
            request.lastName = lastName
            request.gender = gender
            val result: registerResult? = client.register(request)
            Log.d(TAG_LOGIN_FRAGMENT, "Attempted to register: " + result?.success.toString())
            if (result != null) {
                // Create DataCache
                if(result.success){
                    DataCache.DataCache(ip, port, result.authtoken, result.username, result.personID)
                }
                sendMessage(result.success, result.authtoken)
            } else{
                sendMessage(false, null)
            }
        }

        private fun sendMessage(success: Boolean, authtoken: String?){
            val message = Message.obtain()
            val messageBundle = Bundle()
            messageBundle.putBoolean(KEY_REGISTER_SUCCESS, success)
            messageBundle.putString(KEY_AUTHTOKEN, authtoken)
            message.data = messageBundle
            messageHandler.sendMessage(message)
        }

    }

    class LoginTask(private val messageHandler: Handler,
                    val ip : String,
                    val port : String,
                    val username: String,
                    val password: String
                    ) : Runnable {
        override fun run() {
            val client = Client(ip, port)
            val request = loginRequest()
            request.username = username
            request.password = password
            val result: loginResult? = client.login(request)
            Log.d(TAG_LOGIN_FRAGMENT, "Attempted to login: " + result?.success.toString())
            if (result != null) {
                // Create DataCache
                if(result.success){
                    DataCache.DataCache(ip, port, result.authtoken, result.username, result.personID)
                }
                sendMessage(result.success, result.authtoken)
            } else{
                sendMessage(false, null)
            }
        }

        private fun sendMessage(success: Boolean, authtoken: String?){
            val message = Message.obtain()
            val messageBundle = Bundle()
            messageBundle.putBoolean(KEY_LOGIN_SUCCESS, success)
            messageBundle.putString(KEY_AUTHTOKEN, authtoken)
            message.data = messageBundle
            messageHandler.sendMessage(message)
        }

    }

    private fun showLoginSuccess(success: Boolean, firstName: String?, lastName: String?){
        var finalMessage: String
        if (success) {
            finalMessage = getString(R.string.login_success) + firstName + " " + lastName
        } else {
            finalMessage = getString(R.string.login_failed)
        }
        Toast.makeText(activity, finalMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showRegisterSuccess(success: Boolean, firstName: String?, lastName: String?){
        var finalMessage: String
        if (success) {
            finalMessage = getString(R.string.register_success) + firstName + " " + lastName
        } else {
            finalMessage = getString(R.string.register_failed)
        }
        Toast.makeText(activity, finalMessage, Toast.LENGTH_SHORT).show()
    }

}
