package ir.farhangi.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class OrgInboxRecipient {
    ADVERTISING,
    LIBRARIES,
    FAMILY_AFFAIRS,
    ART_GROUP,
    CULTURAL_DEPUTY,
}

fun OrgInboxRecipient.persianLabel(): String = when (this) {
    OrgInboxRecipient.ADVERTISING -> "بخش تبلیغات"
    OrgInboxRecipient.LIBRARIES -> "بخش کتابخانه‌ها"
    OrgInboxRecipient.FAMILY_AFFAIRS -> "بخش امور خانواده"
    OrgInboxRecipient.ART_GROUP -> "بخش گروه هنری"
    OrgInboxRecipient.CULTURAL_DEPUTY -> "معاونت فرهنگی"
}

@Serializable
data class OrgMessage(
    val id: String,
    val fromName: String,
    val fromRole: UserRole,
    val title: String,
    val body: String,
    val createdAt: Instant,
    val isRead: Boolean,
    val recipient: OrgInboxRecipient = OrgInboxRecipient.CULTURAL_DEPUTY,
    val imageUrl: String? = null,
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
