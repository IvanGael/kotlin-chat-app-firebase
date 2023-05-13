package com.ivan.work.chatapp.models


data class Friend(
    var uuid: String,
    val nom:String,
    val prenom:String,
    val pseudo:String,
    val avatar: String,
    val lastMsg: String,
    val timestamp: Long
){
    constructor():this("","","","","","",0)
}
