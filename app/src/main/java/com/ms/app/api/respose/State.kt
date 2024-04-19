package com.ms.app.api.respose

import com.google.gson.annotations.SerializedName

data class State(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("status") val status: Int = 0,
    @SerializedName("state_id") val state_id: Int = 0,
    @SerializedName("district_id") val district_id: Int = 0,
    @SerializedName("deleted_at") val deletedAt: String? = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = ""
)


