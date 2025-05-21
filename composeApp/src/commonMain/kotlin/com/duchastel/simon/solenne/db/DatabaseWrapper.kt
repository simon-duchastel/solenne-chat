package com.duchastel.simon.solenne.db

import com.duchastel.simon.solenne.Database

interface DatabaseWrapper {
    /**
     * Executes the given query block with the database.
     */
    suspend fun <T> execute(block: suspend Database.() -> T): T
}