package ru.studentai.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class MigrationContainerTest {

    private fun noop(from: Int, to: Int) = object : Migration(from, to) {
        override fun migrate(db: SupportSQLiteDatabase) { /* no-op for test */ }
    }

    @Test
    fun `all returns migrations in original order`() {
        val m12 = noop(1, 2)
        val m23 = noop(2, 3)
        val container = MigrationContainer(m12, m23)
        val arr = container.all()
        assertThat(arr).hasSize(2)
        assertThat(arr[0].startVersion).isEqualTo(1)
        assertThat(arr[1].endVersion).isEqualTo(3)
    }

    @Test
    fun `between finds matching migration`() {
        val m12 = noop(1, 2)
        val m23 = noop(2, 3)
        val container = MigrationContainer(m12, m23)
        assertThat(container.between(2, 3)).isNotNull()
        assertThat(container.between(2, 3)).isEqualTo(m23 as Migration)
    }

    @Test
    fun `between returns null when no migration matches`() {
        val container = MigrationContainer(noop(1, 2))
        assertThat(container.between(3, 4)).isNull()
    }
}
