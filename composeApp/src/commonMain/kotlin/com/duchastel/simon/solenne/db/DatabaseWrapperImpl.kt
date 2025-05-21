package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver
import com.duchastel.simon.solenne.Database
import com.duchastel.simon.solenne.db.SqlDriverFactory.Companion.DB_VERSION_CURRENT
import com.duchastel.simon.solenne.db.SqlDriverFactory.Companion.DB_VERSION_PERSISTENCE_KEY
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Inject
class DatabaseWrapperImpl(
    private val driverFactory: SqlDriverFactory,
    @DbSettings private val settings: ObservableSettings,
): DatabaseWrapper {

    private var db: Database? = null

    private val mutex = Mutex()

    private suspend fun initDatabase(): Database {
        val sqlDriver = driverFactory.createSqlDriver()
        val oldDbVersion = settings.getLong(DB_VERSION_PERSISTENCE_KEY, 1)
        return Database(sqlDriver).apply {
            Database.Schema.migrate(
                driver = sqlDriver,
                oldVersion = oldDbVersion,
                newVersion = 2,
            )
            settings.putLong(DB_VERSION_PERSISTENCE_KEY, DB_VERSION_CURRENT)
        }
    }

    override suspend fun <T> execute(block: suspend Database.() -> T): T {
        mutex.withLock {
            if (db == null) {
                db = initDatabase()
            }
        }

        return db!!.block()
    }
}