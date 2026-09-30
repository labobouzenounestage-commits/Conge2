package com.example.data.model

enum class LeaveStatus(val code: String) {
    PENDING_SUBSTITUTE("PendingSubstitute"),
    PENDING_MANAGER("PendingManager"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    REJECTED_SUBSTITUTE("RejectedSubstitute");

    companion object {
        fun fromCode(code: String): LeaveStatus {
            return entries.firstOrNull { 
                it.code.equals(code, ignoreCase = true) || it.name.equals(code, ignoreCase = true) 
            } ?: PENDING_SUBSTITUTE
        }
    }
}
