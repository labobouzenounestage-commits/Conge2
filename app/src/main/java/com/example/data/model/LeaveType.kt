package com.example.data.model

enum class LeaveType(val code: String, val icon: String) {
    ANNUAL("سنوية", "🏖️"),
    SICK("مرضية", "🩺"),
    RTT("استرجاع", "⏱️");

    companion object {
        fun fromCode(code: String): LeaveType {
            return entries.firstOrNull { 
                it.code.equals(code, ignoreCase = true) || it.name.equals(code, ignoreCase = true) 
            } ?: ANNUAL
        }
    }
}
