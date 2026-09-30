package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leave_requests")
data class LeaveRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userEmail: String,
    val type: LeaveType,
    val startDate: String, // YYYY-MM-DD
    val endDate: String,   // YYYY-MM-DD
    val days: Int,
    val reason: String = "",
    val substituteId: Long,
    val substituteName: String,
    val status: LeaveStatus = LeaveStatus.PENDING_SUBSTITUTE,
    val createdAt: String,
    val timestamp: Long = System.currentTimeMillis()
)
