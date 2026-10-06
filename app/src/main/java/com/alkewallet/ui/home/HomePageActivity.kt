package com.alkewallet.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alkewallet.R
import com.alkewallet.WalletApplication
import com.alkewallet.databinding.ActivityHomeBinding
import com.alkewallet.ui.auth.AuthActivity
import com.alkewallet.ui.profile.ProfileActivity
import com.alkewallet.ui.transactions.RequestMoneyActivity
import com.alkewallet.ui.transactions.SendMoneyActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeViewModel by viewModels {
        val repository = (application as WalletApplication).repository
        HomeViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as WalletApplication).repository
        val userId = repository.getStoredUserId()

        if (userId == -1) {
            val intent = Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnviarDinero.setOnClickListener {
            startActivity(Intent(this, SendMoneyActivity::class.java))
        }

        binding.btnIngresarDinero.setOnClickListener {
            startActivity(Intent(this, RequestMoneyActivity::class.java))
        }

        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val adapter = TransactionAdapter()
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = adapter

        homeViewModel.userLiveData.observe(this) { user ->
            user?.let {
                binding.tvBalanceAmount.text = "$${it.points}"
                binding.tvGreeting.text = "Hola, ${it.name}"

                if (!it.avatarUrl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(it.avatarUrl)
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(binding.ivProfile)
                }
            }
        }

        homeViewModel.transactionsLiveData.observe(this) { transactions ->
            adapter.updateTransactions(transactions)
        }

        homeViewModel.refreshData()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refreshData()
    }
}