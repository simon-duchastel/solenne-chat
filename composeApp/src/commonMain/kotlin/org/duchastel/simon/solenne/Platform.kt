package org.duchastel.simon.solenne

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform