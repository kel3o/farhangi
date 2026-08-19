package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    USER,
    EDITOR,
    ORGANIZATIONAL,
    SUPER_ADMIN,
}

fun UserRole.canEditContent(): Boolean =
    this == UserRole.EDITOR || this == UserRole.SUPER_ADMIN

fun UserRole.canAccessOrgInbox(): Boolean =
    this == UserRole.ORGANIZATIONAL || this == UserRole.SUPER_ADMIN

fun UserRole.canViewReports(): Boolean = this == UserRole.SUPER_ADMIN

fun UserRole.canManageRoles(): Boolean = this == UserRole.SUPER_ADMIN

fun UserRole.persianLabel(): String = when (this) {
    UserRole.USER -> "کاربر"
    UserRole.EDITOR -> "ویرایشگر"
    UserRole.ORGANIZATIONAL -> "سازمانی"
    UserRole.SUPER_ADMIN -> "مدیرکل"
}
