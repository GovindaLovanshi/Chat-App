package com.example.application.data

open class Event<out T>(val content:T)  {
    var hasBeenHandeled = false
    fun getContentOrNull():T?{
        return  if(hasBeenHandeled) null
        else{
            hasBeenHandeled = true
            content
        }
    }

}