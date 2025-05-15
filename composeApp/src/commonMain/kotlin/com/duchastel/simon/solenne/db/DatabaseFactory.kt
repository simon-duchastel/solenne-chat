package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver
import com.duchastel.simon.solenne.Database
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Inject
class DatabaseFactory(private val driverFactory: SqlDriverFactory) {
    private lateinit var driver: SqlDriver
    private var db: Database? = null

    private val mutex = Mutex()

    suspend fun <T> withDatabase(block: suspend Database.() -> T): T {
        mutex.withLock {
            if (db == null) {
                driver = driverFactory.createDriver()
                db = Database(driver)
            }
        }

        return db!!.block()
    }
}