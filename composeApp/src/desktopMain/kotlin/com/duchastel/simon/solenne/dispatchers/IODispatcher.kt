package com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * On JVM desktop we delegate to Dispatchers.IO.
 */
actual val IODispatcher: CoroutineDispatcher = Dispatchers.IO
