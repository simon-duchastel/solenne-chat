package com.duchastel.simon.solenne.tests.screens.topbar

import com.duchastel.simon.solenne.screens.topbar.TopBarPresenter
import com.duchastel.simon.solenne.screens.topbar.TopBarScreen
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TopBarPresenterTest {
    companion object {
        private const val TEST_TITLE = "Test Title"
    }

    private val navigator: FakeNavigator = FakeNavigator(TopBarScreen(TEST_TITLE))
    private lateinit var presenter: TopBarPresenter

    @BeforeTest
    fun setup() {
        val screen = TopBarScreen(TEST_TITLE)
        presenter = TopBarPresenter(screen = screen)
    }

    @Test
    fun `present - emits state with correct title`() = runTest {
        presenter.test {
            val state = awaitItem()
            assertEquals(TEST_TITLE, state.title)
        }
    }
}
