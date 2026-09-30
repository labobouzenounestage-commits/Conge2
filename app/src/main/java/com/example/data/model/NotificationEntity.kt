package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetUserId: Long? = null,
    val targetRole: String? = null,
    val messageAr: String,
    val messageFr: String,
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)
