package ru.studentai.core.ui.resource

import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * Тестируемая абстракция над `Context.resources`.
 *
 * Domain- и presentation-слои НЕ должны напрямую обращаться к `Context.getString(...)`:
 *  • это делает unit-тесты `viewmodel` невозможными без Robolectric;
 *  • заставляет тащить Context в слой бизнес-логики.
 *
 * `ResourceProvider` инжектится через Hilt singleton и тривиально мокается в тестах.
 */
public interface ResourceProvider {

    public fun getString(@StringRes res: Int): String

    public fun getString(@StringRes res: Int, vararg formatArgs: Any): String

    public fun getStringArray(@ArrayRes res: Int): Array<String>

    public fun getQuantityString(
        @PluralsRes res: Int,
        quantity: Int,
        vararg formatArgs: Any,
    ): String

    public fun getColorInt(@ColorRes res: Int): Int
}
