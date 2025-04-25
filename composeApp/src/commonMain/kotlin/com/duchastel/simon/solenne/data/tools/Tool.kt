package com.duchastel.simon.solenne.data.tools

import kotlinx.serialization.json.JsonElement

data class Tool(
    val name: String,
    val description: String?,
    val argumentsSchema: Map<String, JsonElement>,
    val requiredArguments: List<String>,
)