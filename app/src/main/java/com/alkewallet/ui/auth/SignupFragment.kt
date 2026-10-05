package com.alkewallet.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alkewallet.R
import com.alkewallet.WalletApplication
import com.alkewallet.data.model.WalletResult
import com.alkewallet.databinding.FragmentSignupBinding
import com.alkewallet.ui.home.HomePageActivity

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels {
        val repository = (requireActivity().application as WalletApplication).repository
        AuthViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCrearCuenta.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val lastname = binding.etLastname.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val rePass = binding.etRePassword.text.toString().trim()

            if (pass != rePass) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullName = if (lastname.isNotEmpty()) "$name $lastname" else name
            authViewModel.signup(fullName, email, pass)
        }

        binding.tvYaTienesCuenta.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        authViewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is WalletResult.Loading -> binding.btnCrearCuenta.isEnabled = false
                is WalletResult.Success -> {
                    binding.btnCrearCuenta.isEnabled = true
                    val intent = Intent(requireContext(), HomePageActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is WalletResult.Error -> {
                    binding.btnCrearCuenta.isEnabled = true
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}