package ir.farhangi.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class OrgMessage(
    val id: String,
    val fromName: String,
    val fromRole: UserRole,
    val title: String,
    val body: String,
    val createdAt: Instant,
    val isRead: Boolean,
)

@Serializable
data class NamedCount(
    val name: String,
    val count: Int,
)

@Serializable
data class PlatformReport(
    val sectionAudience: List<NamedCount>,
    val topBooks: List<NamedCount>,
    val topCourses: List<NamedCount>,
    val topArticles: List<NamedCount>,
)

@Serializable
data class StaffMember(
    val id: String,
    val displayName: String,
    val phone: String,
    val role: UserRole,
)
