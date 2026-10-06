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

    @Query("SELECT remote_id FROM transactions WHERE user_id = :userId AND remote_id IS NOT NULL")
    suspend fun getExistingRemoteIds(userId: Int): List<Int>

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC, localId DESC")
    fun getTransactionsByUserId(userId: Int): LiveData<List<Transaction>>

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()
}