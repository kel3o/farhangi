package ir.farhangi.core.model

object BookCategories {
    const val LITERATURE = "ادبیات"
    const val HISTORY = "تاریخ"
    const val BOOK_SUMMARY = "خلاصه کتاب"
    const val FICTION = "داستان و رمان"
    const val RELIGIOUS = "مذهبی"
    const val PSYCHOLOGY_SUCCESS = "روان‌شناسی و موفقیت"
    const val MEMOIR = "زندگی‌نامه و خاطرات"
    const val SACRED_DEFENSE = "دفاع مقدس"
    const val POETRY = "شعر"
    const val CHILDREN = "کودک و نوجوان"
    const val EDUCATIONAL = "آموزشی"

    val ALL: List<String> = listOf(
        LITERATURE,
        HISTORY,
        BOOK_SUMMARY,
        FICTION,
        RELIGIOUS,
        PSYCHOLOGY_SUCCESS,
        MEMOIR,
        SACRED_DEFENSE,
        POETRY,
        CHILDREN,
        EDUCATIONAL,
    )
}
