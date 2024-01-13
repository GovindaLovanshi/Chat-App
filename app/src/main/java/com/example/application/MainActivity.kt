package com.example.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.application.Screen.ChatListScreen
import com.example.application.Screen.LoginScreen
import com.example.application.Screen.ProfileScreen
import com.example.application.Screen.SignUpScreen
import com.example.application.Screen.SingleChatScreen
import com.example.application.Screen.SingleStatusScreen
import com.example.application.Screen.StatusScreen
import com.example.application.ui.theme.ApplicationTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreen(var route :String){
    object SignUp : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatList")
    object SingleChat : DestinationScreen("singleChat/{chatId}"){
        fun  createRoute(id:String){}

    }

    object StatusList:DestinationScreen("StatusList")
    object SingleStatus:DestinationScreen("singleStatus/{userId}"){
        fun createRote(userId:String)= "singleStatus/$userId"

    }
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    chatAppNavigation()
                }
            }
        }
    }

    @Composable
    fun ChatAppNavigation(){

        val navController = rememberNavController()
        var viewModel = hiltViewModel<ChatApplicationViewModel>()
        NavHost(navController = navController,stratDestination = DestinationScreen.SignUp.route){

            composable(DestinationScreen.SignUp.route){
                SignUpScreen(navController,viewModel)
            }

            composable(DestinationScreen.Login.route){
                LoginScreen(navController = navController, ViewModel = viewModel)
            }
            composable(DestinationScreen.ChatList.route){
                ChatListScreen(navController = navController, ViewModel = viewModel)
            }

            composable(DestinationScreen.SingleChat.route){
                val chatId = it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(navController=navController,viewModel=viewModel,chatId=chatId)
                }
            }

            composable(DestinationScreen.ChatList.route){
                StatusScreen(navController = navController, ViewModel = viewModel)
            }

            composable(DestinationScreen.Profile.route){
                ProfileScreen(navController = navController, ViewModel = viewModel )
            }

            composable(DestinationScreen.SingleStatus.route){
                val userId = it.arguments?.getString("userId")
                userId?.let {
                    SingleStatusScreen(navController=navController,viewModel=viewModel,userId = it)
                }

            }
        }


    }


}
