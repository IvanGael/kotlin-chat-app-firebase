package com.ivan.work.chatapp.models


data class User(
    var uuid: String,
    var nom: String,
    var prenom: String,
    val pseudo: String,
    val email: String,
    var picture: String?
) {
    constructor() : this("","","","","","")
}
