// Courtesy of @IlyaGulya:
// https://github.com/IlyaGulya/TodoAppDecomposeMviKotlin/blob/fd6d535783f55bdf2ce5494768a0520acd27a7dd/web/src/jsMain/resources/sqlite.worker.js#L4
importScripts("sqlite3.js");
import initSqlJs from "sql.js";

let db = null;

async function createDatabase() {
  const sqlite3 = await sqlite3InitModule();

  // Check if OPFS VFS is available, fallback to in-memory if not
  if (!sqlite3.capi.sqlite3_vfs_find("opfs") || !sqlite3.oo1.OpfsDb) {
    let SQL = await initSqlJs({ locateFile: file => '/sql-wasm.wasm' });
    db = new SQL.Database();
  }

  try {
    db = new sqlite3.oo1.OpfsDb("/database.db", "c");
  } catch (error) {
    // Handle OPFS-specific errors (locking, I/O, etc.)
    console.error("Failed to create OPFS database:", error);
    throw new Error(`Database creation failed: ${error.message}`);
  }
}

function handleMessage() {
  const data = this.data;

  try {
    switch (data && data.action) {
      case "exec":
        if (!data["sql"]) {
          throw new Error("exec: Missing query string");
        }

        return postMessage({
          id: data.id,
          results: { values: db.exec({ sql: data.sql, bind: data.params, returnValue: "resultRows" }) },
        });

      case "begin_transaction":
        return postMessage({
          id: data.id,
          results: db.exec("BEGIN TRANSACTION;"),
        });

      case "end_transaction":
        return postMessage({
          id: data.id,
          results: db.exec("END TRANSACTION;"),
        });

      case "rollback_transaction":
        return postMessage({
          id: data.id,
          results: db.exec("ROLLBACK TRANSACTION;"),
        });

      default:
        throw new Error(`Unsupported action: ${data && data.action}`);
    }
  } catch (error) {
    // Better error handling for OPFS-specific issues
    return postMessage({
      id: data.id,
      error: {
        message: error.message,
        // OPFS I/O errors might indicate locking issues
        isLockingError: error.message.includes("I/O") || error.message.includes("lock")
      }
    });
  }
}

function handleError(err) {
  return postMessage({
    id: this.data.id,
    error: {
      message: err.message,
      stack: err.stack
    }
  });
}

if (typeof importScripts === "function") {
  db = null;
  const sqlModuleReady = createDatabase();
  self.onmessage = (event) => {
    return sqlModuleReady.then(handleMessage.bind(event))
    .catch(handleError.bind(event));
  }
}
