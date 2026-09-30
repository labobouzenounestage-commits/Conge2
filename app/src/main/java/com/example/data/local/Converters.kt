package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.EMPLOYEE)

    @TypeConverter
    fun fromLeaveType(type: LeaveType): String = type.name

    @TypeConverter
    fun toLeaveType(value: String): LeaveType = runCatching { LeaveType.valueOf(value) }.getOrDefault(LeaveType.ANNUAL)

    @TypeConverter
    fun fromLeaveStatus(status: LeaveStatus): String = status.name

    @TypeConverter
    fun toLeaveStatus(value: String): LeaveStatus = runCatching { LeaveStatus.valueOf(value) }.getOrDefault(LeaveStatus.PENDING_SUBSTITUTE)
}
