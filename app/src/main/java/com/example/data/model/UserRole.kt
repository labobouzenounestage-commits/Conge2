package com.example.data.model

enum class UserRole(val code: String) {
    ADMIN("Admin"),
    MANAGER("Manager"),
    EMPLOYEE("Employé");

    companion object {
        fun fromCode(code: String): UserRole {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) || it.name.equals(code, ignoreCase = true) }
                ?: EMPLOYEE
        }
    }
}
