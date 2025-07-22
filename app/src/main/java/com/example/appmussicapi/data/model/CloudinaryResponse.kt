package com.example.appmussicapi.data.model

data class CloudinaryResponse(
    val resources: List<CloudinaryResource>
)

data class CloudinaryResource(
    val public_id: String,
    val format: String,
    val resource_type: String,
    val secure_url: String,
    val created_at: String? = null
)
