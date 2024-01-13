package com.example.application.Screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.ChatApplicationViewModel
import com.example.application.CommonDivider
import com.example.application.DestinationScreen
import com.example.application.commonImage
import com.example.application.commonprogress
import com.example.application.navigateTo

@Composable
fun ProfileScreen(navController: NavController,ViewModel : ChatApplicationViewModel) {
    val inProgress = ViewModel.inProgress.value
    if(inProgress){
        commonprogress()

    }else{
        val userData = ViewModel.userData.value
        var name by rememberSaveable {
            mutableStateOf(userData?.name?:"")
        }

        var number by rememberSaveable {
            mutableStateOf(userData?.number?:" ")
        }
        Column {

           profileContent(
               modifier = Modifier
                   .weight(1f)
                   .verticalScroll(rememberScrollState())
                   .padding(8.dp),
               onBack = {
                        navigateTo(navController =navController, route = DestinationScreen.ChatList.route)
               },
               onSave = {
                        ViewModel.createUpdateprofile(name=name, number=number)
               },
               ViewModel = ViewModel,
               name = "",
               number = "",
               onNameChange = {name=it},
               onNumberChange ={number= it },
               onLogout = {
                   ViewModel.logout()
                   navigateTo(navController =navController, route = DestinationScreen.Login.route)
               }
           )
               


            BottomnavigationMenu(selectedItem = BottomnavigationItem.PROFILE, navController = navController)
        }

    }
    BottomnavigationMenu(selectedItem = BottomnavigationItem.PROFILE, navController = navController)

}

@Composable
fun profileContent(modifier: Modifier,onBack:()->Unit,onSave:()->Unit,ViewModel: ChatApplicationViewModel,name:String,number :String,onNameChange:(String)->Unit,
                   onNumberChange:()->Unit,onLogout:()->Unit){
    val imageUrl = ViewModel.userData.value?.imageUrl

    Column {
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Back",Modifier.clickable {
                 onSave.invoke()
            })
            Text(text = "Save")
            CommonDivider()
            ProfileImage(imageUrl = imageUrl, ViewModel = ViewModel )
            CommonDivider()
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), verticalAlignment = Alignment.CenterVertically){
                Text(text = "Name",modifier = Modifier.width(100.dp))
                TextField(value = name, onValueChange = onNameChange,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ))
                
            }

            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), verticalAlignment = Alignment.CenterVertically){
                Text(text = "Number",modifier = Modifier.width(100.dp))
                TextField(value = number, onValueChange = onNumberChange,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                )

            }

            CommonDivider()
            Row (modifier= Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.Center){
                Text(text = "LogOut", modifier = Modifier.clickable { onLogout.invoke() })

            }
        }
    }

}

@Composable
fun ProfileImage(imageUrl:String?,ViewModel : ChatApplicationViewModel){
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent() ){
        uri ->
        uri?.let {
            ViewModel.uploadProfile(uri)
        }
    }

    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)){
        Column(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                launcher.launch("image/*")
            },
            horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = CircleShape,modifier = Modifier
                .padding(8.dp)
                .size(100.dp)) {
                commonImage(data = imageUrl)
            }
            
            Text(text = "Change Profile Pictures")

        }

        if(ViewModel.inProgress.value){
            commonprogress()
        }
    }



}




