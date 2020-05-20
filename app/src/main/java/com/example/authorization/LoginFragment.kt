package com.example.authorization

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.authorization.databinding.FragmentLoginBinding
import com.example.authorization.helpers.Validator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        binding.userCredentials = userViewModel
        val view = binding.root

        view.login_button_sign_up.setOnClickListener(activity as View.OnClickListener)
        view.login_button_forget_password.setOnClickListener(activity as View.OnClickListener)

        setUpValidation(view)

        view.login_button_sign_in.setOnClickListener {
            view.fragment_login.requestFocus()
            var isFormValid =
                Validator.checkForm(
                    R.id.fragment_login
                )
            if (isFormValid) {
                if (userViewModel.checkPassword()) {
                    startActivity(
                        Intent(
                            context,
                            UserInfoActivity::class.java
                        )
                            .putExtra(getString(R.string.email), userViewModel.email))
                }
                else{
                    Snackbar.make(
                        view,
                        R.string.wrong_email_or_password,
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.errorBackground
                            )
                        )
                        .show()
                }
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        userViewModel.clearPassword()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val view = binding.root
        view.login_button_sign_up.setOnClickListener(null)
        view.login_button_forget_password.setOnClickListener(null)
        view.login_button_sign_in.setOnClickListener(null)

        Validator.objects.removeIf { it.fragmentId == R.id.fragment_login }
    }

    private fun setUpValidation(view: View){
        Validator(
            R.id.fragment_login,
            view.login_input_email,
            view.login_input_layout_email,
            mapOf(
                getString(R.string.field_is_empty) to fun() =
                    !view.login_input_email.text.isNullOrEmpty(),
                getString(R.string.bad_email) to fun() =
                    Patterns.EMAIL_ADDRESS.matcher(view.login_input_email.text.toString())
                        .matches())
        )

        Validator(
            R.id.fragment_login,
            view.login_input_password,
            view.login_input_layout_password,
            mapOf(
                getString(R.string.field_is_empty) to fun() =
                    !view.login_input_password.text.isNullOrEmpty())
        )
    }
}