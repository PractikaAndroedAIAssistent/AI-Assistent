package ru.studentai.core.ui.resource

import android.content.Context
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android-реализация [ResourceProvider] поверх `ApplicationContext.resources`.
 *
 * Использует [ApplicationContext], а не Activity — гарантирует отсутствие утечек
 * и корректную работу из background-потоков.
 */
@Singleton
public class AndroidResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : ResourceProvider {

    override fun getString(res: Int): String = context.getString(res)

    override fun getString(res: Int, vararg formatArgs: Any): String =
        context.getString(res, *formatArgs)

    override fun getStringArray(res: Int): Array<String> =
        context.resources.getStringArray(res)

    override fun getQuantityString(
        res: Int,
        quantity: Int,
        vararg formatArgs: Any,
    ): String =
        context.resources.getQuantityString(res, quantity, *formatArgs)

    override fun getColorInt(res: Int): Int =
        ContextCompat.getColor(context, res)
}
