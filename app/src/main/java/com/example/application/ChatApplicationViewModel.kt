package com.example.application

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.application.data.CHATS
import com.example.application.data.ChatData
import com.example.application.data.ChatUser
import com.example.application.data.Event
import com.example.application.data.MESSAGE
import com.example.application.data.Message
import com.example.application.data.STATUS
import com.example.application.data.Status
import com.example.application.data.USER_NODE
import com.example.application.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel

class ChatApplicationViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {
    init {

        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }

    }

    var inProgress = mutableStateOf(false)
    var eventMutableStste = mutableStateOf<Event<String>?>(null)
    var inProcessChats = mutableStateOf(false)
    var signIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null
    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressstatus = mutableStateOf(false)

    fun populateChat() {
        inProcessChats.value = true
        db.collection(CHATS).where(
            Filter.equalTo("user1.userId", userData.value?.userId),
            Filter.equalTo("user2.userId", userData.value?.userId),

            ).addSnapshotListener { value, error ->
            if (error != null) {
                Expeptionhandling(error)

            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChats.value = false
            }
        }
    }

    fun populateMessages(chatID: String) {
        inProgressChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatID).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Expeptionhandling(error)
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull<DocumentSnapshot?, Message> {
                        it.toObject()<Message>()
                    }.sortedBy { it.timestamp }
                    inProgressChatMessage.value = false
                }
            }
    }

    fun depopulateMessage() {
        chatMessages.value = listOf()
        currentChatMessageListener = null

    }

    fun onSendReply(chatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message, time)
        db.collection(CHATS).document(chatID).collection(MESSAGE).document().set(message)
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgress.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            Expeptionhandling(customMsg = "Please Fill All Fields")
            return
        }

        inProgress.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        signIn.value = true
                        createUpdateprofile(name, number)

                    } else {
                        Expeptionhandling(it.exception, customMsg = "sign Up Failed")
                        inProgress.value = false
                    }
                }

            } else {
                Expeptionhandling(customMsg = "number Already Exists")
            }
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                signIn.value = true
                createUpdateprofile(name, number)

            } else {
                Expeptionhandling((it.exception, customMsg = "sign Up Failed"))
            }
        }
    }

    fun uploadProfile(uri: Uri) {
        uploadImage(uri) {
            createUpdateprofile(imageUrl = it.toString())
        }

    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("image/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener(onSuccess)
            inProgress.value = false
        }

            .addOnFailureListener {
                Expeptionhandling(it)
            }


    }


    fun createUpdateprofile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {

        val uid = auth.currentUser?.uid
        val userdata = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageurl
        )

        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {

                } else {
                    db.collection(USER_NODE).document(uid).set(userdata)
                    inProgress.value = false
                    getUserData(it)
                }


            }
                .addOnSuccessListener {
                    Expeptionhandling(it, "Cannot Retrieve User")
                }


        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            Expeptionhandling(customMsg = "Please Fill the all Fields")
            return
        } else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    if (it.isSuccessful) {
                        signIn.value = true
                        inProgress.value = false
                        auth.currentUser?.uid.let {
                            getUserData(it)
                        }

                    } else {
                        Expeptionhandling(exception = it.exception, customMsg = "LOGIn Faild")
                    }
                }
        }
    }


    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                Expeptionhandling(error, "Can not Retrieve User")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                populateChat()
                populateStatus()
            }
        }

    }


    fun Expeptionhandling(exception: Exception? = null, customMsg: String = "") {
        Log.e("ChatApplication", "Chat Exception", exception)
        exception?.printStackTrace()
        val errMsg = exception?.localizedMessage ?: ""
        val message = if (customMsg.isNullOrEmpty()) errMsg else customMsg


        eventMutableStste.value = Event(message)
        inProgress.value = false
    }

    fun logout() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateMessage()
        currentChatMessageListener = null
        eventMutableStste.value = Event("Logged Out")
    }

    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            Expeptionhandling(customMsg = "Number must be contain digit only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        db.collection(USER_NODE).whereEqualTo("number", number).get()
                            .addOnSuccessListener {
                                if (it.isEmpty) {
                                    Expeptionhandling(customMsg = "Number Not found")
                                } else {
                                    val ChatPartner = it.toObjects<UserData>()[0]
                                    val id = db.collection(CHATS).document().id
                                    val chat = ChatData(
                                        chatId = id,
                                        ChatUser(
                                            userData.value?.userId,
                                            userData.value?.name,
                                            userData.value?.imageUrl,
                                            userData.value?.number
                                        ),
                                        ChatUser(
                                            ChatPartner.name,
                                            ChatPartner.imageurl,
                                            ChatPartner.number
                                        )
                                    )

                                    db.collection(CHATS).document(id).set(chat)

                                }
                            }

                            .addOnFailureListener {
                                Expeptionhandling(it)
                            }
                    } else {
                        Expeptionhandling(customMsg = "Chat is Already Exists")
                    }
                }
        }
    }

    fun uploadStatus(uri: Uri) {
        uploadImage(uri) {
            createStatus(it.toString())

        }
    }

    fun createStatus(imageUrl: String) {
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number,
            ), imageUrl,
            System.currentTimeMillis()

        )

        db.collection(STATUS).document()
            .set(newStatus)
    }

    fun populateStatus() {
        val timeDelta = 24L * 60 *60 *1000
        val timeCutOff = System.currentTimeMillis() - timeDelta
        inProgressstatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null)
                Expeptionhandling(error)
            if (value != null) {
                val currentConnection = arrayListOf(userData.value?.userId)

                val chats = value.toObjects<ChatData>()
                chats.forEach { chat ->
                    if (chat.user1.userId == userData.value?.userId) {
                        currentConnection.add(chat.user2.userId)
                    } else {
                        currentConnection.add(chat.user1.userId)
                    }
                }
                db.collection(STATUS).whereGreaterThan("timestamp",timeCutOff).whereIn("user.userId", currentConnection)
                    .addSnapshotListener { value, error ->
635
                        if (error != null) {
                            Expeptionhandling(error)
                        }
                        if (value != null) {
                            status.value = value.toObjects()
                            inProgressstatus.value = false
                        }
                    }
            }
        }
    }


}




