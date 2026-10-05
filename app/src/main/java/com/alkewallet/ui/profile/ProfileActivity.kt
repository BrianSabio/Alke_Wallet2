package com.alkewallet.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alkewallet.R
import com.alkewallet.WalletApplication
import com.alkewallet.databinding.ActivityProfileBinding
import com.alkewallet.ui.auth.AuthActivity
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val profileViewModel: ProfileViewModel by viewModels {
        val repository = (application as WalletApplication).repository
        ProfileViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivEdit.setOnClickListener {
            Toast.makeText(this, "Función no disponible", Toast.LENGTH_SHORT).show()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        profileViewModel.userLiveData.observe(this) { user ->
            user?.let {
                binding.tvUsername.text = "${it.name}\n${it.email}"

                if (!it.avatarUrl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(it.avatarUrl)
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(binding.ivProfilePhoto)
                }
            }
        }

        binding.itemLogout.setOnClickListener {
            profileViewModel.logout {
                val intent = Intent(this, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}