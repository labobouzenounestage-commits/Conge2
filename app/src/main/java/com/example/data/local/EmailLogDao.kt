package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.EmailLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailLogDao {
    @Query("SELECT * FROM email_logs ORDER BY timestamp DESC")
    fun getAllEmailLogs(): Flow<List<EmailLogEntity>>

    @Query("SELECT * FROM email_logs WHERE toEmail = :email ORDER BY timestamp DESC")
    fun getEmailLogsForRecipient(email: String): Flow<List<EmailLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmailLog(emailLog: EmailLogEntity): Long

    @Query("DELETE FROM email_logs")
    suspend fun clearAllEmailLogs()
}
