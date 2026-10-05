package com.alkewallet.ui.transactions

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alkewallet.WalletApplication
import com.alkewallet.data.model.WalletResult
import com.alkewallet.databinding.ActivityRequestMoneyBinding

class RequestMoneyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestMoneyBinding

    private val transactionViewModel: TransactionViewModel by viewModels {
        val repository = (application as WalletApplication).repository
        TransactionViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnIngresarDinero.setOnClickListener {
            val requester = binding.etRequesterName.text.toString().trim()
            val amountStr = binding.etAmount.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            val concept = if (requester.isNotEmpty()) "De: $requester - $notes" else notes
            transactionViewModel.sendMoney(amountStr, concept, toUserId = 0, type = "request")
        }

        transactionViewModel.transactionState.observe(this) { result ->
            when (result) {
                is WalletResult.Loading -> binding.btnIngresarDinero.isEnabled = false
                is WalletResult.Success -> {
                    binding.btnIngresarDinero.isEnabled = true
                    Toast.makeText(this, "Solicitud realizada con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is WalletResult.Error -> {
                    binding.btnIngresarDinero.isEnabled = true
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}