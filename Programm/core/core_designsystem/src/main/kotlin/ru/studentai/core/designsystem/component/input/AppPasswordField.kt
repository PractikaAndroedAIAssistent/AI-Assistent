package ru.studentai.core.designsystem.component.input

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import ru.studentai.core.designsystem.icon.StudentAiIcons
import ru.studentai.core.designsystem.preview.PreviewBox
import ru.studentai.core.designsystem.preview.ThemePreviews

/**
 * Поле ввода пароля. Поверх [AppTextField] добавляет:
 *  • toggle видимости (icon-кнопка справа);
 *  • дефолтный keyboard `Password` + `ImeAction.Done`;
 *  • `PasswordVisualTransformation` когда видимость выключена.
 *
 * State видимости сохраняется при rotation (`rememberSaveable`).
 *
 * @param toggleVisibilityContentDescription accessibility-описание для иконки toggle —
 *        строка передаётся снаружи, потому что core_designsystem не локализует тексты.
 */
@Composable
public fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = AppTextFieldDefaults.PasswordKeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: ImageVector? = null,
    toggleVisibilityContentDescription: String = "Показать или скрыть пароль",
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val transformation: VisualTransformation = remember(passwordVisible) {
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
    }

    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        supportingText = supportingText,
        errorMessage = errorMessage,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = transformation,
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) {
                        StudentAiIcons.VisibilityOff
                    } else {
                        StudentAiIcons.VisibilityOn
                    },
                    contentDescription = toggleVisibilityContentDescription,
                )
            }
        },
    )
}

@ThemePreviews
@Composable
private fun AppPasswordFieldPreview() = PreviewBox {
    AppPasswordField(value = "secret123", onValueChange = {}, label = "Пароль")
}

@ThemePreviews
@Composable
private fun AppPasswordFieldErrorPreview() = PreviewBox {
    AppPasswordField(
        value = "qwe",
        onValueChange = {},
        label = "Пароль",
        errorMessage = "Минимум 8 символов",
    )
}
