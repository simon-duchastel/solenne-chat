package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.duchastel.simon.solenne.Database
import dev.zacsweers.metro.Inject
import org.w3c.dom.Worker

@Inject
class WasmJsSqlDriverFactory : SqlDriverFactory {
    override suspend fun createSqlDriver(): SqlDriver {
        val driver = WebWorkerDriver(createSqlWorker())
        Database.Schema.create(driver).await()

        return driver
    }
}

fun createSqlWorker(): Worker = js(
    """new Worker(new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url))"""
)


