package com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * On iOS we delegate to Dispatchers.Default.
 */
actual val IODispatcher: CoroutineDispatcher = Dispatchers.Default