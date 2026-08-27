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

fun formatPublishedDate(instant: Instant): String {
    val epochDay = instant.toEpochMilliseconds() / MILLIS_PER_DAY
    // Stable demo display: YYYY-MM-DD from UTC epoch without calendar deps
    val days = epochDay
    var y = 1970
    var remaining = days
    while (true) {
        val yearDays = if (isLeapYear(y)) 366L else 365L
        if (remaining < yearDays) break
        remaining -= yearDays
        y++
    }
    val monthDays = monthLengths(isLeapYear(y))
    var m = 1
    for (len in monthDays) {
        if (remaining < len) break
        remaining -= len
        m++
    }
    val d = (remaining + 1).toInt()
    val yyyy = y.toString()
    val mm = m.toString().padStart(2, '0')
    val dd = d.toString().padStart(2, '0')
    return "$yyyy/$mm/$dd".toPersianDigits()
}

private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

private fun monthLengths(leap: Boolean): List<Long> = listOf(
    31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31,
)

private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_MINUTE = 60_000L
private const val MINUTES_PER_DAY = 24 * 60
private const val MILLIS_PER_DAY = 86_400_000L
