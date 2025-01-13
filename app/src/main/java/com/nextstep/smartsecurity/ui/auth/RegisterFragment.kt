package com.nextstep.smartsecurity.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nextstep.smartsecurity.data.AppDatabase
import com.nextstep.smartsecurity.data.User
import com.nextstep.smartsecurity.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            registerUser(name, email, password)
        }

        binding.loginTextView.setOnClickListener {
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    user?.let {
                        saveUserToDatabase(it.uid, name, email)
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToDatabase(uid: String, name: String, email: String) {
        val user = User(uid, name, email)
        viewLifecycleOwner.lifecycleScope.launch {
            AppDatabase.getDatabase(requireContext()).userDao().insert(user)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}