package com.amigojolive.domain.model

import kotlinx.serialization.Serializable

/** Payload enviado a POST /auth/register-request */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val area: String,
)

/** Elemento de GET /admin/registration-requests */
@Serializable
data class RegistrationRequest(
    val id: Int,
    val email: String,
    val fullName: String,
    val area: String,
    val status: String,       // "pending" | "approved" | "rejected"
    val createdAt: String,
)
