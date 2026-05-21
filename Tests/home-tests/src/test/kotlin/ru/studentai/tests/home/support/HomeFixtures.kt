package ru.studentai.tests.home.support

import kotlinx.datetime.LocalDateTime
import ru.studentai.feature.auth.domain.model.User
import ru.studentai.feature.auth.domain.model.UserProfile
import ru.studentai.feature.auth.domain.model.UserRole
import ru.studentai.feature.home.domain.model.AverageScoreSummary
import ru.studentai.feature.home.domain.model.DeadlineItem
import ru.studentai.feature.home.domain.model.DeadlinePriority
import ru.studentai.feature.home.domain.model.GroupActivity
import ru.studentai.feature.home.domain.model.HomeSnapshot
import ru.studentai.feature.home.domain.model.PendingReview
import ru.studentai.feature.home.domain.model.Recommendation
import ru.studentai.feature.home.domain.model.ScoreTrend
import ru.studentai.feature.home.domain.model.TeacherTask
import ru.studentai.feature.home.domain.model.UpcomingLesson

internal object HomeFixtures {

    fun studentUser(
        id: String = "student-1",
        email: String = "student@example.com",
        fullName: String = "Ivan Petrov",
    ): User = User(
        id = id,
        email = email,
        fullName = fullName,
        role = UserRole.Student,
    )

    fun teacherUser(
        id: String = "teacher-1",
        email: String = "teacher@example.com",
        fullName: String = "Petr Ivanov",
    ): User = User(
        id = id,
        email = email,
        fullName = fullName,
        role = UserRole.Teacher,
    )

    fun studentProfile(user: User = studentUser()): UserProfile = UserProfile(
        user = user,
        university = "MSU",
        group = "IU7-41B",
        course = 3,
        speciality = "Computer Science",
    )

    fun teacherProfile(user: User = teacherUser()): UserProfile = UserProfile(
        user = user,
        university = "MSU",
        speciality = "Mathematics",
    )

    fun upcomingLesson(
        subject: String = "Algorithms",
    ): UpcomingLesson = UpcomingLesson(
        startAt = LocalDateTime.parse("2026-05-21T10:00:00"),
        endAt = LocalDateTime.parse("2026-05-21T11:30:00"),
        subject = subject,
        lessonType = "Lecture",
        room = "305",
        teacher = "P.P. Petrov",
    )

    fun deadline(id: String = "deadline-1"): DeadlineItem = DeadlineItem(
        id = id,
        subject = "Algorithms",
        title = "Lab 2",
        dueAt = LocalDateTime.parse("2026-05-22T23:59:00"),
        priority = DeadlinePriority.Normal,
        isOverdue = false,
    )

    fun recommendation(id: String = "rec-1"): Recommendation =
        Recommendation(
            id = id,
            title = "Repeat topic",
            body = "Focus on graphs and shortest paths.",
            subject = "Algorithms",
        )

    fun averageScore(value: Double = 4.6): AverageScoreSummary =
        AverageScoreSummary(
            value = value,
            maxValue = 5.0,
            trend = ScoreTrend.Up,
            subjectCount = 6,
        )

    fun teacherTask(id: String = "task-1"): TeacherTask =
        TeacherTask(id = id, title = "Check lab reports")

    fun groupActivity(): GroupActivity = GroupActivity(
        groupName = "IU7-41B",
        studentCount = 24,
        averageScore = 4.1,
        maxScore = 5.0,
        submissionRatePercent = 72,
    )

    fun pendingReview(id: String = "review-1"): PendingReview = PendingReview(
        id = id,
        groupName = "IU7-41B",
        subject = "Algorithms",
        pendingCount = 3,
        oldestSubmittedAt = LocalDateTime.parse("2026-05-19T09:00:00"),
    )

    fun studentSnapshot(
        user: User = studentUser(),
        subject: String = "Algorithms",
    ): HomeSnapshot.Student = HomeSnapshot.Student(
        user = user,
        upcomingLesson = upcomingLesson(subject),
        weekDeadlines = listOf(deadline()),
        averageScore = averageScore(),
        recommendations = listOf(recommendation()),
        isScheduleAvailable = true,
        isDeadlinesAvailable = true,
        isGradesAvailable = true,
        isRecommendationsAvailable = true,
    )

    fun teacherSnapshot(
        user: User = teacherUser(),
        subject: String = "Algorithms",
    ): HomeSnapshot.Teacher = HomeSnapshot.Teacher(
        user = user,
        upcomingLesson = upcomingLesson(subject),
        tasks = listOf(teacherTask()),
        groupActivity = groupActivity(),
        pendingReviews = listOf(pendingReview()),
        isScheduleAvailable = true,
        isTasksAvailable = true,
        isGroupActivityAvailable = true,
        isPendingReviewsAvailable = true,
    )
}
