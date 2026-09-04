package ir.farhangi.core.model

object CourseCategories {
    const val PSYCHOLOGY = "روان‌شناسی"
    const val ART_DESIGN = "هنر و طراحی"
    const val LIFE_SKILL = "مهارت زندگی"
    const val HISTORY_CULTURE = "تاریخ و فرهنگ"
    const val FAMILY = "خانواده"
    const val MUSIC = "موسیقی"
    const val COMMUNICATION = "مهارت ارتباطی"
    const val ART = "هنر"
    const val GENERAL = "عمومی"

    val ALL: List<String> = listOf(
        PSYCHOLOGY,
        ART_DESIGN,
        LIFE_SKILL,
        HISTORY_CULTURE,
        FAMILY,
        MUSIC,
        COMMUNICATION,
        ART,
        GENERAL,
    )
}
