package com.amigojolive.domain.repository

import com.amigojolive.core.network.ApiResult
import com.amigojolive.core.network.ApiService
import com.amigojolive.core.session.TokenStorage
import com.amigojolive.domain.model.*

class AuthRepository(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage,
) {
    suspend fun login(email: String, password: String): ApiResult<UserSummary> {
        val result = apiService.login(LoginRequest(email, password))
        if (result is ApiResult.Success) {
            tokenStorage.saveToken(result.data.token)
            return ApiResult.Success(result.data.user)
        }
        return result as ApiResult.Error
    }

    suspend fun registerRequest(
        email: String, password: String, fullName: String, area: String,
    ): ApiResult<Unit> =
        apiService.registerRequest(RegisterRequest(email, password, fullName, area))

    suspend fun getMe(): ApiResult<UserSummary> = apiService.getMe()

    fun logout() = tokenStorage.clearToken()

    fun isLoggedIn(): Boolean = tokenStorage.getToken() != null
}
