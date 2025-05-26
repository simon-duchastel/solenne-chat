package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.duchastel.simon.solenne.Database
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.await
import org.w3c.dom.Worker
import kotlin.js.Promise

@Inject
class WasmJsSqlDriverFactory : SqlDriverFactory {
    override suspend fun createSqlDriver(): SqlDriver {
        val webWorker = createSqlWorker()
        return WebWorkerDriver(webWorker).apply {
            val dbExists = checkIfDbExistsJs().await<JsBoolean>().toBoolean()
            if (!dbExists) {
                Database.Schema.create(this@apply).await()
            }
        }
    }
}

fun createSqlWorker(): Worker = js(
    """new Worker(new URL("sqlite.worker.js", import.meta.url))"""
)

val checkIfDbExistsJs: () -> Promise<JsBoolean> = js("""
  async function() {
    try {
      const storageManager = navigator.storage;
      const root = await storageManager.getDirectory();
      await root.getFileHandle("database.db", { create: false });
      return true;
    } catch (e) {
      return false;
    }
  }
""")