package com.example.authorization

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.example.authorization.databinding.FragmentResetBinding
import com.example.authorization.helpers.Validator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_reset.view.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ResetFragment : Fragment() {

    private lateinit var binding: FragmentResetBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_reset, container, false)
        binding.userCredentials = userViewModel
        val view = binding.root

        view.reset_button_sign_up.setOnClickListener(activity as View.OnClickListener)

        view.reset_button_reset_password.setOnClickListener{
            view.fragment_reset.requestFocus()
            var isFormValid = Validator.checkForm(R.id.fragment_reset)
            if (isFormValid) {
                if (userViewModel.isUserExists()) {
                    userViewModel.createOrUpdateCredentials()

                    sendEmail(activity)

                    (activity as MainActivity).replaceFragmentOn(
                        LoginFragment()
                    )
                    Snackbar
                        .make(
                            view,
                            R.string.password_has_been_changed_successfully,
                            Snackbar.LENGTH_LONG)
                        .show()
                }
                else{
                    Snackbar
                        .make(
                            view,
                            R.string.current_account_does_not_exists,
                            Snackbar.LENGTH_LONG)
                        .setAction(R.string.register) {
                            (activity as MainActivity).replaceFragmentOn(RegisterFragment())
                        }
                        .setActionTextColor(ContextCompat.getColor(requireContext(),
                            R.color.buttonTextColorLight
                        ))
                        .show()
                }
            }
        }

        setUpValidation()
        return view
    }

    override fun onStart() {
        super.onStart()
        userViewModel.clearPassword()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val view = requireView()
        view.reset_button_sign_up.setOnClickListener(null)
        view.reset_button_reset_password.setOnClickListener(null)

        Validator.objects.removeIf { it.fragmentId == R.id.fragment_reset }
    }

    private fun setUpValidation(){
        val view = binding.root

        Validator(
            R.id.fragment_reset,
            view.reset_input_email,
            view.reset_input_layout_email,
            mapOf(
                getString(R.string.field_is_empty) to fun() =
                    !view.reset_input_email.text.isNullOrEmpty(),
                getString(R.string.bad_email) to fun() =
                    Patterns.EMAIL_ADDRESS.matcher(view.reset_input_email.text.toString())
                        .matches())
        )

        Validator(
            R.id.fragment_reset,
            view.reset_input_password,
            view.reset_input_layout_password,
            mapOf(
                getString(R.string.field_is_empty) to fun() =
                    !view.reset_input_password.text.isNullOrEmpty(),
                getString(R.string.length_must_be_more) to fun() =
                    view.reset_input_password.text!!.length > 8)
        )

        Validator(
            R.id.fragment_reset,
            view.reset_input_password_confirmation,
            view.reset_input_layout_password_confirmation,
            mapOf(
                getString(R.string.field_is_empty) to fun() =
                    !view.reset_input_password_confirmation.text.isNullOrEmpty(),
                getString(R.string.password_not_the_same) to fun() =
                    view.reset_input_password_confirmation.text.toString() == view.reset_input_password.text.toString()
            )
        )
    }

    private fun sendEmail(activity: FragmentActivity?){
        AppExecutors().diskIO().execute {
            val props = System.getProperties()
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "465"

            val session =  Session.getInstance(
                props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("andro.email.sendler@gmail.com", "EmAiLsendler689")
                    }
                })
            try {
                val mm = MimeMessage(session)

                //Setting sender address
                mm.setFrom(InternetAddress("andro.email.sendler@gmail.com"))

                //Adding receiver
                mm.addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(binding.root.reset_input_email.text.toString()))

                //Adding subject
                mm.subject = activity?.getString(R.string.your_password_has_been_changed)

                //Adding message
                mm.setText("${activity?.getString(R.string.your_current_password)}: ${binding.root.reset_input_password.text.toString()}")

                //Sending email
                Transport.send(mm)
            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }
}
