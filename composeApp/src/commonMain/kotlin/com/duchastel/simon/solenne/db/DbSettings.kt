package com.duchastel.simon.solenne.db

import com.russhwolf.settings.Settings
import dev.zacsweers.metro.Qualifier


/**
 * Annotates the [Settings] instance used for storings database settings, like the currently
 * stored version.
 */
@Qualifier
annotation class DbSettings