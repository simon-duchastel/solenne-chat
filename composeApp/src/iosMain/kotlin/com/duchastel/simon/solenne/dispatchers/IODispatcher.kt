package com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * On iOS there isn’t a separate IO‐optimized queue,
 * sopackage com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kat
import kotlinx.coroutines.CoroutineDispatcherom/import kotlinx.coroutines.Dispatchers

/** Och
/** On Android we use the standard lenactual val IODispatcher: CoroutineDispatcher = DispDiEOF

cat << 'EOF' > composeApp/src/desktopMain/kotlin/com/duJS/Wasmpackage com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kut
import kotlinx.coroutines.Coroutinls composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers
find composeApp/src -type f | grep dispatchers
mkdir -p composeApp/src/desktopMain/kotlin/com/duchastel/simon/solenne/dispatchers composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers composeApp/src/wasmJsMain/kotlin/com/duchastel/simon/solenne/dispatchers && echo 'package com.duchastel.simon.solenne.dispatchers' > composeApp/src/desktopMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '' >> composeApp/src/desktopMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'import kotlinx.coroutines.CoroutineDispatcher' >> composeApp/src/desktopMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'import kotlinx.coroutines.Dispatchers' >> composeApp/src/desktopMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '' >> composeApp/src/desktopMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '/** On JVM desktop we use Dispatchers.IO. */' >> composeApp/src/desktopMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'actual val IODispatcher: CoroutineDispatcher = Dispatchers.IO' >> composeApp/src/desktopMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'package com.duchastel.simon.solenne.dispatchers' > composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '' >> composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'import kotlinx.coroutines.CoroutineDispatcher' >> composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'import kotlinx.coroutines.Dispatchers' >> composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '' >> composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '/** On iOS we fallback to Dispatchers.Default. */' >> composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'actual val IODispatcher: CoroutineDispatcher = Dispatchers.Default' >> composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'package com.duchastel.simon.solenne.dispatchers' > composeApp/src/wasmJsMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '' >> composeApp/src/wasmJsMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'import kotlinx.coroutines.CoroutineDispatcher' >> composeApp/src/wasmJsMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'import kotlinx.coroutines.Dispatchers' >> composeApp/src/wasmJsMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '' >> composeApp/src/wasmJsMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo '/** On JS/Wasm we use Dispatchers.IO. */' >> composeApp/src/wasmJsMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt && echo 'actual val IODispatcher: CoroutineDispatcher = Dispatchers.IO' >> composeApp/src/wasmJsMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt
mkdir -p composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers && cat << 'EOF' > composeApp/src/iosMain/kotlin/com/duchastel/simon/solenne/dispatchers/IODispatcher.kt
package com.duchastel.simon.solenne.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** On iOS there isn’t a separate IO-optimized queue, so we fall back to Default. */
actual val IODispatcher: CoroutineDispatcher = Dispatchers.Default
