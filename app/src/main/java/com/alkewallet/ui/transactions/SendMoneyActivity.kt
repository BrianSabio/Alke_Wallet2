package com.alkewallet.ui.transactions

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alkewallet.WalletApplication
import com.alkewallet.data.model.WalletResult
import com.alkewallet.databinding.ActivitySendMoneyBinding

class SendMoneyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendMoneyBinding

    private val transactionViewModel: TransactionViewModel by viewModels {
        val repository = (application as WalletApplication).repository
        TransactionViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnEnviarDinero.setOnClickListener {
            val recipient = binding.etRecipient.text.toString().trim()
            val amountStr = binding.etAmount.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            val concept = if (recipient.isNotEmpty()) "Para: $recipient - $notes" else notes
            transactionViewModel.sendMoney(amountStr, concept, toUserId = 0, type = "send")
        }

        transactionViewModel.transactionState.observe(this) { result ->
            when (result) {
                is WalletResult.Loading -> binding.btnEnviarDinero.isEnabled = false
                is WalletResult.Success -> {
                    binding.btnEnviarDinero.isEnabled = true
                    Toast.makeText(this, "Transacción realizada con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is WalletResult.Error -> {
                    binding.btnEnviarDinero.isEnabled = true
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}