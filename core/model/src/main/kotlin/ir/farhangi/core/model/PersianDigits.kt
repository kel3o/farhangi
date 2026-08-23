package ir.farhangi.core.model

import kotlinx.datetime.Instant

fun Int.toPersianDigits(): String = toString().toPersianDigits()

fun Long.toPersianDigits(): String = toString().toPersianDigits()

fun String.toPersianDigits(): String = buildString(length) {
    for (char in this@toPersianDigits) {
        append(
            when (char) {
                '0' -> '۰'
                '1' -> '۱'
                '2' -> '۲'
                '3' -> '۳'
                '4' -> '۴'
                '5' -> '۵'
                '6' -> '۶'
                '7' -> '۷'
                '8' -> '۸'
                '9' -> '۹'
                else -> char
            },
        )
    }
}

fun String.fromPersianDigits(): String = buildString(length) {
    for (char in this@fromPersianDigits) {
        append(
            when (char) {
                '۰' -> '0'
                '۱' -> '1'
                '۲' -> '2'
                '۳' -> '3'
                '۴' -> '4'
                '۵' -> '5'
                '۶' -> '6'
                '۷' -> '7'
                '۸' -> '8'
                '۹' -> '9'
                else -> char
            },
        )
    }
}

fun formatDurationClock(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / SECONDS_PER_MINUTE
    val seconds = safe % SECONDS_PER_MINUTE
    return "${minutes.toPersianDigits()}:${seconds.toString().padStart(2, '0').toPersianDigits()}"
}

fun formatRemainingDaysAndMinutes(endsAt: Instant, now: Instant): String {
    val remainingMs = (endsAt.toEpochMilliseconds() - now.toEpochMilliseconds()).coerceAtLeast(0L)
    val totalMinutes = remainingMs / MILLIS_PER_MINUTE
    val days = totalMinutes / MINUTES_PER_DAY
    val minutes = totalMinutes % MINUTES_PER_DAY
    return "${days.toPersianDigits()} روز و ${minutes.toPersianDigits()} دقیقه"
}

private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_MINUTE = 60_000L
private const val MINUTES_PER_DAY = 24 * 60
