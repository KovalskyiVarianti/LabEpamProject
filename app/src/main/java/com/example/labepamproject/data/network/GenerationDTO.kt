package com.example.labepamproject.data.network

data class GenerationListResponse(
    val count: Int,
    val results: List<GenerationPartialResponse>,
)

data class GenerationPartialResponse(
    val name: String,
    val url: String,
)