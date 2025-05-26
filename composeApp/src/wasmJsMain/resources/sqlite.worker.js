importScripts("sqlite3.js");

let db = null;

async function createDatabase() {
  const sqlite3 = await sqlite3InitModule();

  const existingData = await loadDatabaseFile();
  if (existingData) {
    const buffer = new Uint8Array(existingData);
    db = new sqlite3.oo1.DB(buffer);
    console.log("Loaded database from IndexedDB.");
  } else {
    db = new sqlite3.oo1.DB();
    console.log("Created new empty database.");
  }
}

function openIndexedDB() {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open("database.db", 1);
    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      db.createObjectStore("files");
    };
    request.onsuccess = (event) => resolve(event.target.result);
    request.onerror = (event) => reject(event.target.error);
  });
}

async function loadDatabaseFile() {
  const idb = await openIndexedDB();
  return new Promise((resolve, reject) => {
    const tx = idb.transaction("files", "readonly");
    const store = tx.objectStore("files");
    const request = store.get("sqlite.db");

    request.onsuccess = () => resolve(request.result || null);
    request.onerror = () => reject(request.error);
  });
}

async function saveDatabaseFile() {
  const idb = await openIndexedDB();
  const data = db.export();
  return new Promise((resolve, reject) => {
    const tx = idb.transaction("files", "readwrite");
    const store = tx.objectStore("files");
    const request = store.put(data, "sqlite.db");

    request.onsuccess = () => resolve();
    request.onerror = () => reject(request.error);
  });
}

// Handle incoming messages
async function handleMessage() {
  const data = this.data;

  switch (data && data.action) {
    case "exec":
      if (!data["sql"]) {
        throw new Error("exec: Missing query string");
      }
      const results = db.exec({ sql: data.sql, bind: data.params, returnValue: "resultRows" });
      // Save DB after write queries
      if (/^\s*(INSERT|UPDATE|DELETE|REPLACE|CREATE|DROP|ALTER|BEGIN|END|COMMIT|ROLLBACK)/i.test(data.sql)) {
        await saveDatabaseFile();
      }
      return postMessage({ id: data.id, results: { values: results } });

    case "begin_transaction":
      db.exec("BEGIN TRANSACTION;");
      return postMessage({ id: data.id, results: null });

    case "end_transaction":
      db.exec("END TRANSACTION;");
      await saveDatabaseFile();
      return postMessage({ id: data.id, results: null });

    case "rollback_transaction":
      db.exec("ROLLBACK TRANSACTION;");
      await saveDatabaseFile();
      return postMessage({ id: data.id, results: null });

    default:
      throw new Error(`Unsupported action: ${data && data.action}`);
  }
}

function handleError(err) {
  return postMessage({
    id: this.data.id,
    error: err.toString(),
  });
}

if (typeof importScripts === "function") {
  db = null;
  const sqlModuleReady = createDatabase();
  self.onmessage = (event) => {
    sqlModuleReady
      .then(handleMessage.bind(event))
      .catch(handleError.bind(event));
  };
}
