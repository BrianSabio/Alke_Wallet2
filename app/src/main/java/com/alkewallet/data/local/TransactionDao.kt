package com.alkewallet.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alkewallet.data.model.Transaction

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY localId DESC")
    fun getTransactionsByUserId(userId: Int): LiveData<List<Transaction>>

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()
}