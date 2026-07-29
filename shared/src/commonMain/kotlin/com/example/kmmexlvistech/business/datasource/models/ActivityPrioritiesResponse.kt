package com.example.kmmexlvistech.business.datasource.models

import com.example.kmmexlvistech.business.datasource.dto.CentralCrmSortDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class ActivityPrioritiesResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<ActivityPriorityDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val errors: List<String> = emptyList()
)

@kotlinx.serialization.Serializable
data class ActivityPriorityDto(
    val id: String = "",
    val priorityId: Int = 0,
    val code: String? = null,
    val name: String = "",
    val colorCode: String? = null,
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
)

@kotlinx.serialization.Serializable
data class ActivityStatusesResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<ActivityStatusDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val errors: List<String> = emptyList()
)

@Serializable
data class ActivityStatusDto(
    val id: String = "",
    val statusId: Int = 0,
    val code: String? = null,
    val name: String = "",
    val colorCode: String? = null,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
    val activityTypeId: Int = 0,
    val isActive: Boolean = true,
)

@Serializable
data class UserListResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<UserOptionDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalRecords: Int = 0,
    val errors: List<String> = emptyList()
)

@Serializable
data class UserOptionDto(
    val id: String = "",
    val userId: Int = 0,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String = "",
    val emailId: String? = null,
    val roleId: Int = 0,
    val roleName: String? = null,
    val isUserActive: Boolean = true,
)

@Serializable
data class ActivityFilterDto(
    val activityTypeId: Int? = null,
    val search: String = "",
    val isActive: Boolean = true,
)

@Serializable
data class UserListFilterDto(
    val search: String = "",
    val status: String = "all",
    val roleId: Int? = null,
    val departmentId: Int? = null,
    val designationId: Int? = null,
)

@Serializable
data class UserListRequestDto(
    val page: Int = 1,
    val pageSize: Int = 20,
    val filters: UserListFilterDto = UserListFilterDto(),
    val sort: CentralCrmSortDto = CentralCrmSortDto(field = "firstName"),
)