package ru.studentai.core.navigation.navigator

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import ru.studentai.core.navigation.command.NavigationCommand
import ru.studentai.core.navigation.command.NavigationOptions
import ru.studentai.core.navigation.route.NavigationRoute

class DefaultNavigatorTest {

    @Serializable
    private data object FakeHome : NavigationRoute

    @Serializable
    private data class FakeProfile(val id: String) : NavigationRoute

    @Test
    fun `navigateTo emits NavigateTo command`() = runTest {
        val sut = DefaultNavigator()
        sut.commands.test {
            sut.navigateTo(FakeHome)
            val cmd = awaitItem()
            assertThat(cmd).isInstanceOf(NavigationCommand.NavigateTo::class)
            assertThat((cmd as NavigationCommand.NavigateTo).route).isEqualTo(FakeHome as NavigationRoute)
        }
    }

    @Test
    fun `navigateTo with options preserves them`() = runTest {
        val sut = DefaultNavigator()
        val options = NavigationOptions.popUpToRoot()
        sut.commands.test {
            sut.navigateTo(FakeProfile("42"), options)
            val cmd = awaitItem() as NavigationCommand.NavigateTo
            assertThat(cmd.route).isEqualTo(FakeProfile("42") as NavigationRoute)
            assertThat(cmd.options).isEqualTo(options)
        }
    }

    @Test
    fun `back emits NavigateBack`() = runTest {
        val sut = DefaultNavigator()
        sut.commands.test {
            sut.back()
            assertThat(awaitItem()).isEqualTo(NavigationCommand.NavigateBack as NavigationCommand)
        }
    }

    @Test
    fun `up emits NavigateUp`() = runTest {
        val sut = DefaultNavigator()
        sut.commands.test {
            sut.up()
            assertThat(awaitItem()).isEqualTo(NavigationCommand.NavigateUp as NavigationCommand)
        }
    }

    @Test
    fun `popUpTo emits PopUpTo command with inclusive flag`() = runTest {
        val sut = DefaultNavigator()
        sut.commands.test {
            sut.popUpTo(FakeHome, inclusive = true)
            val cmd = awaitItem() as NavigationCommand.PopUpTo
            assertThat(cmd.route).isEqualTo(FakeHome as NavigationRoute)
            assertThat(cmd.inclusive).isEqualTo(true)
        }
    }
}
