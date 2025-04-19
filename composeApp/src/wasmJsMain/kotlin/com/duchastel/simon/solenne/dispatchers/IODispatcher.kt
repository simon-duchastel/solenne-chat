package com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * On JS/Wasm we delegate to Dispatchers.Default
 */
actual val IODispatcher: CoroutineDispatcher = Dispatchers.Default
