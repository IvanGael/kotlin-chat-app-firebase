package com.ivan.work.chatapp.models

data class Message(
    val sender: String,
    val receiver: String,
    val msg: String,
    val hour: Long,
    var isReceived: Boolean = true
){
    constructor(): this("","","",0)
}
