package models

data class PostRequest(
    val userId: Int,
    val title: String,
    val body: String
)
