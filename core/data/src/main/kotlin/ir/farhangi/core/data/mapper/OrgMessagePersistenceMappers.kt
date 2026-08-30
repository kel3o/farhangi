package ir.farhangi.core.data.mapper

import ir.farhangi.core.database.entity.OrgMessageEntity
import ir.farhangi.core.model.OrgInboxRecipient
import ir.farhangi.core.model.OrgMessage
import ir.farhangi.core.model.UserRole
import ir.farhangi.core.network.model.OrgMessageDto
import kotlinx.datetime.Instant

fun OrgMessageDto.toEntity(): OrgMessageEntity = OrgMessageEntity(
    id = id,
    fromName = fromName,
    fromRole = fromRole,
    title = title,
    body = body,
    createdAt = createdAt,
    isRead = isRead,
    recipient = recipient,
    imageUrl = imageUrl,
)

fun OrgMessageEntity.toDomain(): OrgMessage = OrgMessage(
    id = id,
    fromName = fromName,
    fromRole = runCatching { UserRole.valueOf(fromRole) }.getOrDefault(UserRole.ORGANIZATIONAL),
    title = title,
    body = body,
    createdAt = Instant.parse(createdAt),
    isRead = isRead,
    recipient = runCatching { OrgInboxRecipient.valueOf(recipient) }
        .getOrDefault(OrgInboxRecipient.CULTURAL_DEPUTY),
    imageUrl = imageUrl,
)
