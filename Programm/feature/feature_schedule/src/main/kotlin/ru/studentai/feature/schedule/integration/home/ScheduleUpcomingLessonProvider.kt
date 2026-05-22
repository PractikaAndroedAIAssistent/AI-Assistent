package ru.studentai.feature.schedule.integration.home

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.datetime.Clock
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.home.domain.contract.UpcomingLessonProvider
import ru.studentai.feature.home.domain.model.UpcomingLesson
import ru.studentai.feature.schedule.domain.repository.ScheduleRepository

/**
 * Адаптер: предоставляет данные feature_home о ближайшем занятии.
 *
 * Реализует контракт [UpcomingLessonProvider] из `feature_home`. Биндинг регистрируется
 * в app-модуле (см. `AppScheduleIntegrationModule`) — это закрывает плагинную точку
 * расширения и на главном экране появляется реальная карточка «Ближайшая пара».
 *
 * Маппинг доменной модели `ScheduleItem` в `UpcomingLesson`:
 *  • `lessonType` → строка (через `LessonTypeFormatter`-like локальный маппер);
 *  • остальные поля переносятся 1:1.
 *
 * Если в расписании нет ближайших занятий — возвращает `null`, что feature_home
 * рендерит как «Сегодня пар больше нет».
 */
@Singleton
public class ScheduleUpcomingLessonProvider @Inject constructor(
    private val repository: ScheduleRepository,
) : UpcomingLessonProvider {

    override suspend fun fetch(userId: String): UpcomingLesson? {
        val result = repository.getUpcoming(userId, Clock.System.now())
        val item = (result as? DomainResult.Success)?.value ?: return null
        return UpcomingLesson(
            startAt = item.startAt,
            endAt = item.endAt,
            subject = item.subjectName,
            lessonType = item.customTypeLabel ?: item.lessonType.toDisplayLabel(),
            room = item.room,
            teacher = item.teacher,
        )
    }

    /**
     * Локальная функция отображения типа занятия — здесь нет доступа к ResourceProvider
     * (это data-слой, без Android Context). UI на главной получает уже готовую строку,
     * локализованную дефолтным значением. При появлении настоящего DI-ресурс провайдера
     * в core_ui можно перевыпустить с локализацией.
     */
    private fun ru.studentai.feature.schedule.domain.model.LessonType.toDisplayLabel(): String =
        when (this) {
            ru.studentai.feature.schedule.domain.model.LessonType.Lecture -> "Лекция"
            ru.studentai.feature.schedule.domain.model.LessonType.Seminar -> "Семинар"
            ru.studentai.feature.schedule.domain.model.LessonType.Lab -> "Лабораторная"
            ru.studentai.feature.schedule.domain.model.LessonType.Practice -> "Практика"
            ru.studentai.feature.schedule.domain.model.LessonType.Consultation -> "Консультация"
            ru.studentai.feature.schedule.domain.model.LessonType.Exam -> "Экзамен"
            ru.studentai.feature.schedule.domain.model.LessonType.Other -> "Другое"
        }
}
