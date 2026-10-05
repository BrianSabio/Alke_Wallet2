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
import com.alkewallet.databinding.FragmentLoginBinding
import com.alkewallet.ui.home.HomePageActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels {
        val repository = (requireActivity().application as WalletApplication).repository
        AuthViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            authViewModel.login(email, pass)
        }

        binding.tvCrearNuevaCuenta.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, SignupFragment())
                .addToBackStack(null)
                .commit()
        }

        authViewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is WalletResult.Loading -> binding.btnLogin.isEnabled = false
                is WalletResult.Success -> {
                    binding.btnLogin.isEnabled = true
                    val intent = Intent(requireContext(), HomePageActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is WalletResult.Error -> {
                    binding.btnLogin.isEnabled = true
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