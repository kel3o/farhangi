package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class MagazineCategory {
    CULTURE,
    FAMILY,
    NEWS,
    ECONOMY,
    POLITICS,
    READING,
    ART,
    HISTORY,
    NARRATIVE,
}

fun MagazineCategory.persianLabel(): String = when (this) {
    MagazineCategory.CULTURE -> "فرهنگی"
    MagazineCategory.FAMILY -> "خانواده"
    MagazineCategory.NEWS -> "اخبار"
    MagazineCategory.ECONOMY -> "اقتصادی"
    MagazineCategory.POLITICS -> "سیاسی"
    MagazineCategory.READING -> "کتابخوانی"
    MagazineCategory.ART -> "هنری"
    MagazineCategory.HISTORY -> "تاریخ"
    MagazineCategory.NARRATIVE -> "روایت"
}
