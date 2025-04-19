package com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * On Android we use the standard IO dispatcher.
 */
actual val IODispatcher: CoroutineDispatcher = Dispatchers.IO
