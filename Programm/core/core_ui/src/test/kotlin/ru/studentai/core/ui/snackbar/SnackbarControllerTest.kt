package ru.studentai.core.ui.snackbar

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SnackbarControllerTest {

    @Test
    fun `showInfo emits SnackbarMessage with Info type`() = runTest {
        val sut = SnackbarController()
        sut.messages.test {
            sut.showInfo("Загрузка завершена")
            val msg = awaitItem()
            assertThat(msg.text).isEqualTo("Загрузка завершена")
            assertThat(msg.type).isEqualTo(SnackbarType.Info)
        }
    }

    @Test
    fun `showSuccess emits SnackbarMessage with Success type`() = runTest {
        val sut = SnackbarController()
        sut.messages.test {
            sut.showSuccess("Сохранено")
            assertThat(awaitItem().type).isEqualTo(SnackbarType.Success)
        }
    }

    @Test
    fun `showError emits SnackbarMessage with Error type`() = runTest {
        val sut = SnackbarController()
        sut.messages.test {
            sut.showError("Ошибка")
            assertThat(awaitItem().type).isEqualTo(SnackbarType.Error)
        }
    }

    @Test
    fun `showWarning emits SnackbarMessage with Warning type`() = runTest {
        val sut = SnackbarController()
        sut.messages.test {
            sut.showWarning("Внимание")
            assertThat(awaitItem().type).isEqualTo(SnackbarType.Warning)
        }
    }

    @Test
    fun `showMessage preserves all fields`() = runTest {
        val sut = SnackbarController()
        val expected = SnackbarMessage(text = "x", actionLabel = "OK", type = SnackbarType.Info)
        sut.messages.test {
            sut.showMessage(expected)
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }
}
