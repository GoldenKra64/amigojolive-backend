package com.amigojolive.domain.repository

import com.amigojolive.core.network.ApiResult
import com.amigojolive.core.network.ApiService
import com.amigojolive.domain.model.AdminUser
import com.amigojolive.domain.model.RegistrationRequest

class AdminRepository(private val apiService: ApiService) {
    suspend fun getUsers(): ApiResult<List<AdminUser>> = apiService.getUsers()
    suspend fun setUserActive(userId: Int, isActive: Boolean): ApiResult<Unit> =
        apiService.updateUserStatus(userId, isActive)

    suspend fun getRegistrationRequests(): ApiResult<List<RegistrationRequest>> =
        apiService.getRegistrationRequests()

    suspend fun approve(id: Int): ApiResult<Unit> = apiService.approveRequest(id)
    suspend fun reject(id: Int): ApiResult<Unit>  = apiService.rejectRequest(id)
}
