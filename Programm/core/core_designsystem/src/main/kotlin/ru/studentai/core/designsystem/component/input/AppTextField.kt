package ru.studentai.core.designsystem.component.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews

/**
 * Стандартное текстовое поле дизайн-системы (OutlinedTextField + согласованные дефолты).
 *
 * @param value             текущее значение
 * @param onValueChange     колбэк изменения значения
 * @param label             подпись поля
 * @param modifier          внешний модификатор (по умолчанию заполняет ширину)
 * @param placeholder       подсказка внутри поля
 * @param supportingText    подсказка под полем (нп. правила, hint); игнорируется если есть [errorMessage]
 * @param errorMessage      если не null — поле в состоянии ошибки + сообщение под ним
 * @param enabled           включено ли поле
 * @param readOnly          read-only (можно копировать, нельзя редактировать)
 * @param singleLine        однострочный режим
 * @param maxLines          ограничение строк (по умолчанию 1 при singleLine)
 * @param keyboardOptions   опции клавиатуры (см. также [AppTextFieldDefaults])
 * @param keyboardActions   действия клавиатуры (onDone/onNext/...)
 * @param visualTransformation  трансформация ввода (например, [androidx.compose.ui.text.input.PasswordVisualTransformation])
 * @param leadingIcon       иконка слева внутри поля
 * @param trailingIcon      Composable справа (часто IconButton для toggle/clear)
 */
@Composable
public fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = AppTextFieldDefaults.KeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val isError = errorMessage != null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let { {
            Icon(imageVector = it, contentDescription = null)
        } },
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = MaterialTheme.shapes.medium,
        supportingText = when {
            isError -> { { Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error) } }
            supportingText != null -> { { Text(text = supportingText) } }
            else -> null
        },
        colors = OutlinedTextFieldDefaults.colors(),
    )
}

public object AppTextFieldDefaults {
    public val KeyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
    )
    public val EmailKeyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next,
    )
    public val PasswordKeyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done,
    )
    public val NumberKeyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
    )
}

@ThemePreviews
@Composable
private fun AppTextFieldPreview() = PreviewBox {
    AppTextField(value = "user@vuz.ru", onValueChange = {}, label = "Email")
}

@ThemePreviews
@Composable
private fun AppTextFieldErrorPreview() = PreviewBox {
    AppTextField(
        value = "bad",
        onValueChange = {},
        label = "Email",
        errorMessage = "Некорректный формат email",
    )
}
