package com.amigojolive.domain.model

import kotlinx.serialization.Serializable

/** Elemento de GET /admin/users */
@Serializable
data class AdminUser(
    val id: Int,
    val email: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String,
    val profile: ProfileSummary? = null,
)

/** Payload de PATCH /admin/users/:id/status */
@Serializable
data class UpdateUserStatusRequest(val isActive: Boolean)

/** Payload de PATCH /profiles/me */
@Serializable
data class UpdateProfileRequest(
    val fullName: String? = null,
    val area: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
)
