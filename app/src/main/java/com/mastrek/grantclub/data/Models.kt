package com.mastrek.grantclub.data

data class DeviceAuthResponse(
    val status:       String,
    val daysRemaining: Int,
    val userId:       Int,
    val deviceKey:    String,
    val playlistUrl:  String?
)

data class Channel(
    val id:      String,
    val name:    String,
    val url:     String,
    val logoUrl: String?,
    val group:   String?
)

data class ActivateRequest(val code: String)
data class DeviceAuthRequest(val deviceKey: String)
