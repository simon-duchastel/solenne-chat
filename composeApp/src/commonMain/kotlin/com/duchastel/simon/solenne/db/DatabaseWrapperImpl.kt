package com.duchastel.simon.solenne.db

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

    private suspend fun initDatabase(): Database {
        val sqlDriver = driverFactory.createSqlDriver()
        val oldDbVersion = settings.getLongOrNull(DB_VERSION_PERSISTENCE_KEY)
        return Database(sqlDriver).apply {
            if (oldDbVersion != null && oldDbVersion < DB_VERSION_CURRENT) {
                Database.Schema.migrate(
                    driver = sqlDriver,
                    oldVersion = oldDbVersion,
                    newVersion = DB_VERSION_CURRENT,
                )
            }
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

    companion object {
        private var db: Database? = null
        private val mutex = Mutex()
    }
}