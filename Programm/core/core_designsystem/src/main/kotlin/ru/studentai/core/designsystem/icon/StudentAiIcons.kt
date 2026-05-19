package ru.studentai.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Централизованный фасад над `material-icons-extended`.
 *
 * Преимущества:
 *  • один источник правды — при замене иконки правится одно место;
 *  • быстро видно, какие иконки используются в проекте;
 *  • легко заменить на кастомные drawable при ребрендинге (вернуть `ImageVector`);
 *  • защита от дублирующих импортов.
 *
 * Запрещено импортировать `androidx.compose.material.icons.*` напрямую в feature-модулях —
 * только через этот объект.
 */
public object StudentAiIcons {

    // ─── Bottom navigation / разделы приложения ───────────────────────────
    public val Home: ImageVector = Icons.Outlined.Home
    public val Schedule: ImageVector = Icons.Outlined.Schedule
    public val Notes: ImageVector = Icons.AutoMirrored.Outlined.Notes
    public val Materials: ImageVector = Icons.AutoMirrored.Outlined.MenuBook
    public val Ai: ImageVector = Icons.Outlined.AutoAwesome
    public val Profile: ImageVector = Icons.Outlined.AccountCircle

    // ─── Учебные сущности ─────────────────────────────────────────────────
    public val Calendar: ImageVector = Icons.Outlined.CalendarMonth
    public val Subject: ImageVector = Icons.Outlined.School
    public val Test: ImageVector = Icons.Outlined.Quiz
    public val Flashcards: ImageVector = Icons.Outlined.Style
    public val Grade: ImageVector = Icons.Outlined.Grade
    public val Pdf: ImageVector = Icons.Outlined.PictureAsPdf
    public val Analytics: ImageVector = Icons.Outlined.Analytics

    // ─── Действия ─────────────────────────────────────────────────────────
    public val Add: ImageVector = Icons.Outlined.Add
    public val Edit: ImageVector = Icons.Outlined.Edit
    public val Delete: ImageVector = Icons.Outlined.Delete
    public val Search: ImageVector = Icons.Outlined.Search
    public val Filter: ImageVector = Icons.Outlined.FilterList
    public val Refresh: ImageVector = Icons.Outlined.Refresh
    public val More: ImageVector = Icons.Outlined.MoreVert
    public val Close: ImageVector = Icons.Outlined.Close
    public val Back: ImageVector = Icons.AutoMirrored.Outlined.ArrowBack
    public val Check: ImageVector = Icons.Outlined.Check
    public val Send: ImageVector = Icons.AutoMirrored.Outlined.Send
    public val Play: ImageVector = Icons.Outlined.PlayArrow
    public val Logout: ImageVector = Icons.AutoMirrored.Outlined.Logout
    public val Upload: ImageVector = Icons.Outlined.CloudUpload

    // ─── Статусы ──────────────────────────────────────────────────────────
    public val Success: ImageVector = Icons.Outlined.CheckCircle
    public val Warning: ImageVector = Icons.Outlined.Warning
    public val Info: ImageVector = Icons.Outlined.Info
    public val Error: ImageVector = Icons.Outlined.Error
    public val ErrorOutline: ImageVector = Icons.Outlined.ErrorOutline
    public val Offline: ImageVector = Icons.Outlined.SignalCellularConnectedNoInternet0Bar
    public val Reminder: ImageVector = Icons.Outlined.Alarm
    public val Notifications: ImageVector = Icons.Outlined.Notifications

    // ─── Настройки и сервис ───────────────────────────────────────────────
    public val Settings: ImageVector = Icons.Outlined.Settings
    public val VisibilityOn: ImageVector = Icons.Outlined.Visibility
    public val VisibilityOff: ImageVector = Icons.Outlined.VisibilityOff
}
