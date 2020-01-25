package com.cobresun.behemoth.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cobresun.behemoth.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.login_fragment.*
import splitties.toast.toast

class LoginFragment : Fragment() {

    private val navController by lazy { this.findNavController() }

    private val RC_SIGN_IN: Int = 8888
    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        currentUser?.let {
            val action = LoginFragmentDirections.actionLoginFragmentToMainFragment(it.uid)
            navController.navigate(action)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser!!
                val userObj = hashMapOf(
                    "uid" to user.uid,
                    "email" to user.email
                )

                val db = FirebaseFirestore.getInstance()
                if (response!!.isNewUser) {
                    db.collection("users")
                        .document(user.uid)
                        .set(userObj)
                }

                val action = LoginFragmentDirections.actionLoginFragmentToMainFragment(user.uid)
                navController.navigate(action)
            } else {
                response?.let {
                    toast("Error signing in: ${response.error}")
                }
            }
        }
    }

}
