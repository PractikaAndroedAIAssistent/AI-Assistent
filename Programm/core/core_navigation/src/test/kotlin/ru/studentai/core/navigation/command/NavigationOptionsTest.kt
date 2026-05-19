package ru.studentai.core.navigation.command

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import ru.studentai.core.navigation.route.NavigationRoute

class NavigationOptionsTest {

    @Serializable
    private data object FakeStart : NavigationRoute

    @Test
    fun `default options have no special flags`() {
        val opts = NavigationOptions.Default
        assertThat(opts.popUpTo).isNull()
        assertThat(opts.popUpToInclusive).isFalse()
        assertThat(opts.singleTop).isFalse()
        assertThat(opts.restoreState).isFalse()
    }

    @Test
    fun `popUpToRoot clears entire stack inclusively`() {
        val opts = NavigationOptions.popUpToRoot()
        assertThat(opts.popUpTo).isNull()
        assertThat(opts.popUpToInclusive).isTrue()
        assertThat(opts.singleTop).isTrue()
    }

    @Test
    fun `bottomNavSwitch preserves state and uses singleTop`() {
        val opts = NavigationOptions.bottomNavSwitch(FakeStart)
        assertThat(opts.popUpTo).isEqualTo(FakeStart as NavigationRoute)
        assertThat(opts.popUpToInclusive).isFalse()
        assertThat(opts.singleTop).isTrue()
        assertThat(opts.restoreState).isTrue()
    }
}
