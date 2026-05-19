package ru.studentai.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 Shapes. Радиусы соответствуют M3 spec и работают согласованно
 * с `Card`, `Button`, `TextField` через стандартные слоты темы.
 *
 *  • extraSmall  4dp — chip, badge
 *  • small       8dp — кнопка
 *  • medium     12dp — TextField, малая карточка
 *  • large      16dp — стандартная карточка экрана
 *  • extraLarge 28dp — bottom sheet, диалог
 */
public val StudentAiShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
