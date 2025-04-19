package com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * A dispatcher for IO‐bound work.
 * Expect/actual will map it to IO on most platforms, but Default on iOS
 * and Wasm/JS.
 */
expect val IODispatcher: CoroutineDispatcher
