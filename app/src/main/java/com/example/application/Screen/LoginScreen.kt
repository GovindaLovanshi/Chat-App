package com.example.application.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.ChatApplicationViewModel
import com.example.application.CheckSignedIn
import com.example.application.DestinationScreen
import com.example.application.R
import com.example.application.commonprogress
import com.example.application.navigateTo

@Composable
fun LoginScreen(ViewModel : ChatApplicationViewModel,navController :NavController) {


        CheckSignedIn(ViewModel =viewModel , navController = navController)
        Box(modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ), horizontalAlignment = Alignment.CenterHorizontally) {

                val emailState = remember{
                    mutableStateOf(TextFieldValue())
                }
                val passwordState = remember{
                    mutableStateOf(TextFieldValue())
                }

                val focus = LocalFocusManager.current

                Image(painter = painterResource(id = R.drawable.whatsapp), contentDescription = null, modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp))
                Text(text = "LOGIN",
                    fontSize = 30.dp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )(
                    label = { Text(text = "Enter Your Mobile Number") },
                    modifier = Modifier.padding(8.dp)
                )

                OutlinedTextField(value = emailState.value, onValueChange = {
                    emailState.value = it
                },
                    label = { Text(text = "Enter A Email") },
                    modifier = Modifier.padding(8.dp)
                )

                OutlinedTextField(value = passwordState.value, onValueChange = {
                    passwordState.value = it
                },
                    label = { Text(text = "Enter Password") },
                    modifier = Modifier.padding(8.dp)
                )

                Button(onClick = { ViewModel.login(emailState.value.text,passwordState.value.text) },  modifier=Modifier.padding(8.dp)) {
                }

                Text(
                    text = "Already a user  ? go to SignUp --> ",
                    color = Color.Black,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigateTo(navController, DestinationScreen.SignUp.route)
                        }
                )
            }
        }

        // loading bar
        if(viewModel.inProgress.value){
            commonprogress()
        }


}