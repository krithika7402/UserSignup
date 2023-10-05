package com.example.cruddemo

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cruddemo.ui.theme.CRUDDemoTheme
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CRUDDemoTheme {
                val signUpViewModel: SignUpViewModel by viewModels()
                val signInViewModel: SignInViewModel by viewModels()

                // Get the NavController
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "signup" // Set the start destination
                ) {
                    composable("signup") {
                        SignUpScreen(
                            viewModel = signUpViewModel,
                            onSignUpSuccess = {

                                navController.navigate("login")
                            }
                        )
                    }
                    composable("login") {
                        SignInScreen(
                            viewModel = signInViewModel,
                            onSignInSuccess = {
                                // Handle navigation here if needed
                            }
                        )
                    }
                    composable("name") { backStackEntry ->
                        val name = backStackEntry.arguments?.getString("name") ?: ""
                        NameScreen(name = name)
                    }
                }
            }
        }
    }
}

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao: UserDao
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    var emailValue by mutableStateOf("")
    var nameValue by mutableStateOf("")

    init {
        userDao = database.userDao()
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun getEmail(): String {
        return emailValue
    }

    fun getName(): String {
        return nameValue
    }

    fun setEmail(email: String) {
        this.emailValue = email
    }

    fun setName(name: String) {
        this.nameValue = name
    }
}

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao: UserDao
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    var emailValue by mutableStateOf("")
    var nameValue by mutableStateOf("")
    var password by mutableStateOf("")

    init {
        userDao = database.userDao()
    }

    suspend fun signIn(email: String, password: String): String? {
        val user = userDao.getUserByEmailAndPassword(email, password)
        if (user != null && user.password == password) {
            return user.name
        }
        return null
    }

    fun setEmail(email: String) {
        this.emailValue = email // Update the property here
    }

    fun setName(name: String) {
        this.nameValue = name // Update the property here
    }

    fun getName(): String {
        return emailValue // Update the property reference here
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onSignUpSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                val newUser = User(0, email, name, password)
                coroutineScope.launch {
                    viewModel.insertUser(newUser)
                    viewModel.setEmail(email)
                    viewModel.setName(name)
                    onSignUpSuccess()
                }
            }
        ) {
            Text(text = "Sign Up")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onSignInSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    val name = viewModel.signIn(email, password)
                    if (name != null) {
                        viewModel.setName(name)
                        onSignInSuccess()
                    } else {
                    }
                }
            }
        ) {
            Text(text = "Sign In")
        }
    }
}

@Composable
fun NameScreen(name: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome, $name!")
    }
}
