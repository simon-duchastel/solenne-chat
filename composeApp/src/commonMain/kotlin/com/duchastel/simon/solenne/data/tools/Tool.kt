package com.duchastel.simon.solenne.data.tools

import kotlinx.serialization.json.JsonObject

data class Tool(
    val name: String,
    val description: String?,
    val parameters: JsonObject,
    val requiredParameters: List<String>,
)