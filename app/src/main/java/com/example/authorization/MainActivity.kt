package com.example.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragmentOn(LoginFragment())
    }

    fun replaceFragmentOn(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.register_button_sign_in -> replaceFragmentOn(LoginFragment())
            R.id.login_button_sign_up,
            R.id.reset_button_sign_up -> replaceFragmentOn(RegisterFragment())
            R.id.login_button_forget_password -> replaceFragmentOn(ResetFragment())
        }
    }

}
