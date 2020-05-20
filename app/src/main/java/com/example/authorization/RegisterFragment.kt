package com.example.authorization

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.authorization.databinding.FragmentRegisterBinding
import com.example.authorization.helpers.Validator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_register.view.*

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onDestroyView() {
        super.onDestroyView()

        requireView().register_button_sign_in.setOnClickListener(null)
        requireView().register_button_create_account.setOnClickListener(null)

        Validator.objects.removeIf { it.fragmentId == R.id.fragment_register }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_register, container, false)
        binding.userCredentials = userViewModel
        val view = binding.root

        view.register_button_sign_in.setOnClickListener(activity as View.OnClickListener)

        view.register_button_create_account.setOnClickListener {
            view.fragment_register.requestFocus()
            var isFormValid = Validator.checkForm(R.id.fragment_register)
            if (isFormValid) {
                if (userViewModel.isUserExists()){
                    Snackbar
                        .make(
                            view,
                            R.string.current_account_already_exists,
                            Snackbar.LENGTH_LONG)
                        .setAction(R.string.login) {
                            (activity as MainActivity).replaceFragmentOn(
                                LoginFragment()
                            )
                        }
                        .setActionTextColor(ContextCompat.getColor(requireContext(),
                            R.color.buttonTextColorLight
                        ))
                        .show()
                }
                else {
                    userViewModel.createOrUpdateCredentials()
                    userViewModel.clearPassword()
                    (activity as MainActivity).replaceFragmentOn(
                        LoginFragment()
                    )
                    Snackbar
                        .make(
                            view,
                            R.string.account_has_been_created_successfully,
                            Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        setUpValidation(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        userViewModel.clearPassword()
    }

    private fun setUpValidation(view: View){

        Validator(
            R.id.fragment_register,
            view.register_input_email,
            view.register_input_layout_email,
            mapOf(
                getString(R.string.field_is_empty) to fun() =
                    !view.register_input_email.text.isNullOrEmpty(),
                getString(R.string.bad_email) to fun() =
                    Patterns.EMAIL_ADDRESS.matcher(view.register_input_email.text.toString())
                        .matches())
        )

        Validator(
            R.id.fragment_register,
            view.register_input_password,
            view.register_input_layout_password,
            mapOf(
                getString(R.string.field_is_empty) to fun() =
                    !view.register_input_password.text.isNullOrEmpty(),
                getString(R.string.length_must_be_more) to fun() =
                    view.register_input_password.text!!.length > 8)
        )
    }

}
