package com.example.application.Screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.ChatApplicationViewModel
import com.example.application.DestinationScreen
import com.example.application.commonRow
import com.example.application.commonprogress
import com.example.application.navigateTo
import com.example.application.titleText

@Composable
fun ChatListScreen(navController: NavController, ViewModel: ChatApplicationViewModel) {

    val inProgress = ViewModel.inProcessChats
    if (inProgress.value) {
        commonprogress()
    } else {

        val chats = ViewModel.chats.value
        val userdata = ViewModel.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick: () -> Unit = { showDialog.value = true }
        val onDismiss: () -> Unit = { showDialog.value = false }
        val onAddChat: (String) -> Unit = {
            ViewModel.onAddChat(it)
            showDialog.value = false
        }

        Scaffold(
            floatingActionButton = {
                FAB(
                    showDialog = showDialog.value,
                    onFabClick = onFabClick,
                    onDismiss = onDismiss,
                    onAddChat = onAddChat
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    titleText(txt = "Chats")

                    if (chats.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "No Chats Available")
                        }
                    }else{
                        LazyColumn(modifier = Modifier.weight(1f)){
                            items(chats){
                                chat->
                                val chatUser = if(chat.user1.userId ==userdata?.userId ){
                                    chat.user2
                                }else{
                                    chat.user1
                                }
                                
                                commonRow(imageurl = chatUser.imageUrl, name = chatUser.name) {

                                    chat.chatId?.let{
                                        navigateTo(navController,DestinationScreen.SingleChat.createRoute(id = chatUser.userId))


                                    }


                                    
                                }
                            }
                        }
                    }
                    BottomnavigationMenu(
                        selectedItem = BottomnavigationItem.CHATLIST,
                        navController = navController
                    )
                }
            }
        )

//        FAB(showDialog = true, onFabClick = { null }, onDismiss = { null}, onAddChat = " " )

    }

    Text(text = "Chat Screen ")

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAB(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value = " "
        },
            confirmButton = {
                Button(onClick = { onAddChat(addChatNumber.value) }) {
                    Text(text = "Add Chat")

                }
            },
            title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber, onValueChange = { addChatNumber.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )

        FloatingActionButton(
            onClick = { onFabClick },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = CircleShape,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
        }
    }

}