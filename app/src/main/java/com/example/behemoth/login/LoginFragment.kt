package com.example.behemoth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.behemoth.R
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {

    private val navController by lazy { this.findNavController() }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        login_button.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }

}
