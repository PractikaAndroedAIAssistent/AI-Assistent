package com.example.assistentai.grades

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import ru.studentai.core.common.error.StorageException
import ru.studentai.core.common.result.DomainResult
import ru.studentai.feature.grades.domain.analyzer.GradeAnalyzer
import ru.studentai.feature.grades.domain.model.Grade
import ru.studentai.feature.grades.domain.model.GradeRecommendation
import ru.studentai.feature.grades.domain.model.GradeType
import ru.studentai.feature.grades.domain.model.GroupActivitySnapshot
import ru.studentai.feature.grades.domain.model.OverallAverage
import ru.studentai.feature.grades.domain.model.SubjectAverage
import ru.studentai.feature.grades.domain.repository.GradeRepository

/**
 * Demo-реализация [GradeRepository] (in-memory) + использует тот же [GradeAnalyzer]
 * что и production, поэтому динамика/рекомендации в demo-режиме работают идентично.
 */
@Singleton
public class DemoGradeRepository @Inject constructor(
    private val analyzer: GradeAnalyzer,
) : GradeRepository {

    private val items: MutableStateFlow<List<Grade>> = MutableStateFlow(seed())

    override fun observeGrades(ownerUserId: String): Flow<List<Grade>> = items.map { list ->
        list.filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
            .sortedByDescending { it.recordedAt }
    }

    override fun observeSubjectAverages(ownerUserId: String): Flow<List<SubjectAverage>> =
        items.map { list ->
            val grades = list.filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
            analyzer.calculateSubjectAverages(grades)
        }

    override suspend fun getById(id: String): DomainResult<Grade> {
        delay(SIMULATED_DELAY_MS / 2)
        return items.value.firstOrNull { it.id == id }
            ?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(StorageException.NotFound(entity = "Grade", id = id))
    }

    override suspend fun getOverallAverage(ownerUserId: String): DomainResult<OverallAverage?> {
        val list = items.value.filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
        val averages = analyzer.calculateSubjectAverages(list)
        return DomainResult.Success(analyzer.calculateOverall(averages))
    }

    override suspend fun getRecommendations(
        ownerUserId: String,
        limit: Int,
    ): DomainResult<List<GradeRecommendation>> {
        val list = items.value.filter { it.ownerUserId == OWNER_ALL || it.ownerUserId == ownerUserId }
        val averages = analyzer.calculateSubjectAverages(list)
        return DomainResult.Success(analyzer.generateRecommendations(averages, limit))
    }

    override suspend fun upsert(grade: Grade): DomainResult<Grade> {
        delay(SIMULATED_DELAY_MS)
        items.update { list ->
            val index = list.indexOfFirst { it.id == grade.id }
            if (index >= 0) list.toMutableList().apply { set(index, grade) } else list + grade
        }
        return DomainResult.Success(grade)
    }

    override suspend fun delete(id: String): DomainResult<Unit> {
        delay(SIMULATED_DELAY_MS / 2)
        items.update { list -> list.filterNot { it.id == id } }
        return DomainResult.Success(Unit)
    }

    override suspend fun refresh(ownerUserId: String): DomainResult<Unit> {
        delay(SIMULATED_DELAY_MS)
        return DomainResult.Success(Unit)
    }

    override suspend fun getGroupActivity(teacherId: String): DomainResult<GroupActivitySnapshot?> {
        delay(SIMULATED_DELAY_MS / 2)
        return DomainResult.Success(
            GroupActivitySnapshot(
                groupName = "ИУ-101",
                studentCount = 24,
                averageScore = 4.12,
                maxScore = 5.0,
                submissionRatePercent = 72,
            ),
        )
    }

    private fun seed(): List<Grade> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val daysAgo = { n: Int -> today.minus(DatePeriod(days = n)) }
        return listOf(
            grade("Алгоритмы", 4.0, 5.0, GradeType.HomeWork, daysAgo(20)),
            grade("Алгоритмы", 3.5, 5.0, GradeType.Lab, daysAgo(14), weight = 1.5),
            grade("Алгоритмы", 4.5, 5.0, GradeType.Test, daysAgo(7), weight = 2.0),
            grade("Алгоритмы", 5.0, 5.0, GradeType.Lab, daysAgo(2)),
            grade("Базы данных", 4.0, 5.0, GradeType.HomeWork, daysAgo(18)),
            grade("Базы данных", 3.0, 5.0, GradeType.Test, daysAgo(10), weight = 2.0),
            grade("Базы данных", 3.5, 5.0, GradeType.Lab, daysAgo(4)),
            grade("Операционные системы", 4.5, 5.0, GradeType.Class, daysAgo(15)),
            grade("Операционные системы", 4.0, 5.0, GradeType.HomeWork, daysAgo(5)),
            grade("Высшая математика", 2.5, 5.0, GradeType.Exam, daysAgo(12), weight = 3.0),
            grade("Высшая математика", 3.0, 5.0, GradeType.HomeWork, daysAgo(3)),
        )
    }

    private fun grade(
        subject: String,
        value: Double,
        max: Double,
        type: GradeType,
        date: kotlinx.datetime.LocalDate,
        weight: Double = 1.0,
    ): Grade = Grade(
        id = UUID.randomUUID().toString(),
        ownerUserId = OWNER_ALL,
        subjectId = null,
        subjectName = subject,
        value = value,
        maxValue = max,
        weight = weight,
        type = type,
        recordedAt = date,
        note = null,
    )

    private companion object {
        const val OWNER_ALL = "*"
        const val SIMULATED_DELAY_MS = 200L
    }
}
