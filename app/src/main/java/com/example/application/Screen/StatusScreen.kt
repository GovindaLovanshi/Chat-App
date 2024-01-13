package com.example.application.Screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.application.ChatApplicationViewModel
import com.example.application.CommonDivider
import com.example.application.DestinationScreen
import com.example.application.commonRow
import com.example.application.commonprogress
import com.example.application.navigateTo


@Composable
fun StatusScreen(navController: NavController, ViewModel: ChatApplicationViewModel) {
    val inprocess = ViewModel.inProgressstatus.value
    if (inprocess) {
        commonprogress()
    } else {
        val statuses = ViewModel.status.value
        val userData = ViewModel.userData.value

        val myStatus = statuses.filter {
            it.user.userId == userData?.userId
        }

        val otherStatus = statuses.filter {
            it.user.userId != userData?.userId
        }

        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    ViewModel.UploadStatus(uri)
                }
            }


        Scaffold(
            floatingActionButton = {
                FAB {
                    launcher.launch("image/*")
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    Text(text = "Status")
                    if (statuses.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No Status Available")
                        }
                    } else {
                        if (myStatus.isNotEmpty()) {
                            myStatus[0].user.name?.let { it1 ->
                                commonRow(imageurl = myStatus[0].user.imageUrl, name = it1) {
                                    navigateTo(
                                        navController = navController,
                                        DestinationScreen.SingleStatus.createRote(myStatus[0].user.userId!!)
                                    )
                                }

                                CommonDivider()
                                val uniqueUser = otherStatus.map { it.user }.toSet().toList()
                                LazyColumn(modifier = Modifier.weight(1f)) {
                                    items(uniqueUser) { user ->
                                        user.name?.let { it2 ->
                                            commonRow(imageurl = user.imageUrl, name = it2) {
                                                navigateTo(
                                                    navController = navController,
                                                    DestinationScreen.SingleStatus.createRote(user.userId!!)
                                                )
                                            }
                                        }
                                    }

                                }
                            }

                        }

                    }
                    BottomnavigationMenu(
                        selectedItem = BottomnavigationItem.STATUSLIST,
                        navController = navController
                    )
                }
            }

        )
    }

}






@Composable
fun FAB(
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {

        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )

    }
}