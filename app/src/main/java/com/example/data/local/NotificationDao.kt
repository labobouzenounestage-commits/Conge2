package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE targetUserId = :userId OR targetRole = :userRole OR (targetRole = 'MANAGER' AND :userRole = 'ADMIN') ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long, userRole: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET read = 1 WHERE targetUserId = :userId OR targetRole = :userRole OR (targetRole = 'MANAGER' AND :userRole = 'ADMIN')")
    suspend fun markAllAsReadForUser(userId: Long, userRole: String)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
