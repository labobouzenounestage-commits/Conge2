package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val balanceAnnual: Int = 30,
    val balanceSick: Int = 15,
    val balanceRtt: Int = 5,
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}
