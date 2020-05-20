package com.example.authorization.helpers

import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Validator(
    val fragmentId : Int,
    private val input: TextInputEditText,
    private val inputLayout: TextInputLayout,
    private val validationList: Map<String, () -> Boolean>)
{
    companion object{
        val objects = mutableListOf<Validator>()

        fun checkForm(fragmentId: Int) : Boolean{
            return !objects.filter { it.fragmentId == fragmentId && !it.validate() }.any()
        }
    }

    init{
        objects.add(this)
        input.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus -> validate(hasFocus) }
    }

    private fun validate() : Boolean{
        for((errorText, validationFunction) in validationList)
        {
            if(!validationFunction()){
                inputLayout.error = errorText
                return false
            }
        }
        inputLayout.error = null
        return true
    }

    private fun validate(hasFocus: Boolean) : Boolean{
        if (hasFocus) {
            inputLayout.error = null
            return true
        }
        return validate()
    }
}

