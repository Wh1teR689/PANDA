package com.example.authorization

import androidx.lifecycle.ViewModel

class UserViewModel(
    var email: String = "",
    var password: String = ""
) : ViewModel() {
    var existingUsers: MutableMap<String, String> = mutableMapOf()

    fun isUserExists() : Boolean{
        return existingUsers.containsKey(email)
    }

    fun createOrUpdateCredentials(){
        existingUsers.plusAssign(email to password)
    }

    fun clearPassword(){
        password = ""
    }

    fun checkPassword(): Boolean{
        return existingUsers[email] == password
    }
}

