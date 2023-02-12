package com.matrix159.mastadonclone.shared.data.models.mastadonapi.instance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Application(
  @SerialName("name")
  val name: String,
  @SerialName("website")
  val website: String? = null
)
