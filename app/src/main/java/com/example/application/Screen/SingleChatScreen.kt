package com.example.application.Screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.ChatApplicationViewModel
import com.example.application.CommonDivider
import com.example.application.commonImage
import com.example.application.data.Message


@Composable
fun SingleChatScreen(
    navController: NavController,
    viewModel: ChatApplicationViewModel,
    chatId: String
) {

    var reply by rememberSaveable {
        mutableStateOf("")
    }

    val onSendReply = {
        viewModel.onSendReply(chatId, reply)
        reply = ""
    }
    var chatMessage = viewModel.chatMessages

    val myUser = viewModel.userData.value
    val currentChat = viewModel.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit) {
        viewModel.populateMessages(chatId)
    }

    BackHandler {
        viewModel.depopulateMessage()

    }

    Column {
        ChatHeader(name = chatUser.name ?: "", imageUrl = chatUser.imageUrl ?: "") {
            navController.popBackStack()
            viewModel.depopulateMessage()

        }

        Text(text = viewModel.chatMessages.value.toString())
        ReplyBox(reply = "", onReplyChane = { reply = it }, onSendReply = onSendReply)

    }


}

@Composable
fun ChatHeader(name: String, imageUrl: String, onBackclicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(Icons.Rounded.ArrowBack, contentDescripton = null, modifier = Modifier
            .clickable {
                onBackclicked.invoke()
            }
            .padding(8.dp))

        commonImage(
            data = imageUrl, modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessage = chatMessage.value,
            currentUserId = myUser?.userId ?: ""
        )
        Text(text = name, FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun MessageBox(modifier: Modifier, chatMessage: List<Message>, currentUserId: String) {
    LazyColumn(modifier = modifier) {
        items(chatMessage) { msg ->

            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) Color(0xFF68c400) else Color(0xFF0C0)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Text(
                    text = msg.message ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(12.dp), color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReplyBox(reply: String, onReplyChane: (String) -> Unit, onSendReply: () -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()) {

        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            TextField(value = reply, onValueChange = onReplyChane, maxLines = 3)
            Button(onClick = onSendReply) {
                Text(text = "Send")

            }

        }

    }

}