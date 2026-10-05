package com.alkewallet.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alkewallet.R
import com.alkewallet.data.model.Transaction
import com.alkewallet.databinding.ItemTransactionBinding
import java.util.Locale

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var transactions: List<Transaction> = emptyList()

    class ViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateTransactions(newList: List<Transaction>) {
        transactions = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactions[position]
        val isSend = item.type == "send"

        with(holder.binding) {
            tvUserName.text = item.concept
            tvDate.text = item.date

            val sign = if (isSend) "-" else "+"
            tvAmount.text = String.format(Locale.US, "%s$%.2f", sign, item.amount)

            val colorRes = if (isSend) {
                ivTransactionIcon.setImageResource(R.drawable.ic_send)
                android.R.color.holo_red_light
            } else {
                ivTransactionIcon.setImageResource(R.drawable.ic_request)
                R.color.alke_green_action
            }

            tvAmount.setTextColor(ContextCompat.getColor(root.context, colorRes))
        }
    }

    override fun getItemCount(): Int = transactions.size
}