package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "email_logs")
data class EmailLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val toEmail: String,
    val recipientName: String,
    val subject: String,
    val body: String,
    val triggerType: String, // "REQUEST_SUBMITTED", "REQUEST_APPROVED", "REQUEST_REJECTED", "LOW_BALANCE_REMINDER"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SENT" // "SENT", "QUEUED", "DELIVERED"
)
