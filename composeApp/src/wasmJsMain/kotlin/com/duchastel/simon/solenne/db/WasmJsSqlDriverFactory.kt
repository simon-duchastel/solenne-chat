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
            try {
                val dbExists = checkIfDbExistsJs().await<JsBoolean>().toBoolean()
                if (!dbExists) {
                    Database.Schema.create(this@apply).await()
                }
            }catch (ex: Exception) {
                println(ex)
            }
        }
    }
}

fun createSqlWorker(): Worker = js(
    """new Worker(new URL("sqlite.worker.js", import.meta.url))"""
)

val checkIfDbExistsJs: () -> Promise<JsBoolean> = js(
    """
    new Promise(function(resolve, reject) {
        var request = indexedDB.open('database.db', 1);
        request.onupgradeneeded = function(event) {
            event.target.transaction.abort();  // avoid creating a new store if not needed
        };
        request.onsuccess = function(event) {
            var db = event.target.result;
            if (!db.objectStoreNames.contains('files')) {
                resolve(false);
                return;
            }
            var tx = db.transaction('files', 'readonly');
            var store = tx.objectStore('files');
            var getReq = store.get('sqlite.db');
            getReq.onsuccess = function() {
                resolve(getReq.result != undefined && getReq.result != null);
            };
            getReq.onerror = function() {
                reject(getReq.error);
            };
        };
        request.onerror = function(event) {
            reject(request.error);
        };
    })
    """
)