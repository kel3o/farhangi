package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Course

interface CourseRepository {
    suspend fun getCourses(query: String? = null): Result<List<Course>>
    suspend fun getCourse(id: String): Result<Course>
    suspend fun completeSection(courseId: String, sectionId: String): Result<Course>
}
