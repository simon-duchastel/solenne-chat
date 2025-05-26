package com.duchastel.simon.solenne.util

import com.slack.circuit.test.FakeNavigator

/**
 * Asserts that the no navigation have occurred.
 * Wraps the existing [FakeNavigator.expectNoPopEvents], [FakeNavigator.expectNoGoToEvents],
 * and [FakeNavigator.expectNoResetRootEvents] for convenience.
 */
fun FakeNavigator.expectNoNavigationEvents() {
    expectNoPopEvents()
    expectNoGoToEvents()
    expectNoResetRootEvents()
}
