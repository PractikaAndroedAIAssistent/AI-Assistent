package ru.studentai.feature.home.domain.repository

import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.home.domain.model.HomeSnapshot

/**
 * Domain-контракт главного экрана. Возвращает консолидированный [HomeSnapshot]
 * (Student или Teacher), собрав данные параллельно из всех доступных провайдеров.
 *
 * Контракт устойчив к отсутствующим провайдерам: если соответствующий
 * `XxxProvider` не подключён в Hilt-граф (Optional.empty), блок будет пустой,
 * но snapshot всё равно вернётся `Success` — экран не ломается.
 */
public interface HomeRepository {

    public suspend fun loadStudentSnapshot(user: User): DomainResult<HomeSnapshot.Student>

    public suspend fun loadTeacherSnapshot(user: User): DomainResult<HomeSnapshot.Teacher>
}
