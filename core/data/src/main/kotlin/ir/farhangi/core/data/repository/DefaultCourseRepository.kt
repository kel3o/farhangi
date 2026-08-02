package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.common.result.map
import ir.farhangi.core.data.mapper.toDomain
import ir.farhangi.core.model.Course
import ir.farhangi.core.network.gateway.ContentGateway
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCourseRepository @Inject constructor(
    private val contentGateway: ContentGateway,
) : CourseRepository {

    private val completedSections = ConcurrentHashMap<String, MutableSet<String>>()

    override suspend fun getCourses(query: String?): Result<List<Course>> =
        contentGateway.getCourses(query).map { list ->
            list.map { applyLocalProgress(it.toDomain()) }
        }

    override suspend fun getCourse(id: String): Result<Course> =
        contentGateway.getCourse(id).map { applyLocalProgress(it.toDomain()) }

    override suspend fun completeSection(courseId: String, sectionId: String): Result<Course> {
        completedSections.getOrPut(courseId) { mutableSetOf() }.add(sectionId)
        return getCourse(courseId)
    }

    private fun applyLocalProgress(course: Course): Course {
        val done = completedSections[course.id].orEmpty()
        if (done.isEmpty()) return course
        val sections = course.sections.map { section ->
            section.copy(isCompleted = section.isCompleted || section.id in done)
        }
        val completedCount = sections.count { it.isCompleted }
        val progress = if (sections.isEmpty()) {
            0f
        } else {
            completedCount.toFloat() / sections.size.toFloat()
        }
        return course.copy(sections = sections, progress = progress.coerceIn(0f, 1f))
    }
}
