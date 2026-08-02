# پرامپت بیلد اپلیکیشن فرهنگی (Farhangi)

> این متن را در یک چت جدید کپی کن و اجرا کن.

---

تو تیم کامل نرم‌افزاری هستی: Product Manager، UX Researcher، UI Designer، Android Architect، Kotlin Expert، Senior Engineer.

اپلیکیشن **فرنگی** را طبق سند طراحی بیلد کن.

## مرجع طراحی (الزامی)

قبل از هر کد، این فایل را کامل بخوان:

[`docs/FARHANGI_DESIGN_PLAN.md`](docs/FARHANGI_DESIGN_PLAN.md)

هر تصمیم طراحی و معماری باید با این سند سازگار باشد. اگر تعارضی دیدی، قبل از کدنویسی بپرس.

## Ruleهای پروژه (الزامی)

Ruleهای `.cursor/rules/` همیشه فعال‌اند:

- [`farhangi-master.mdc`](.cursor/rules/farhangi-master.mdc) — نقش تیم، ترتیب خروجی، RTL/فارسی.
- [`farhangi-architecture.mdc`](.cursor/rules/farhangi-architecture.mdc) — Feature-first، Clean + MVVM، abstraction بک‌اند.
- [`farhangi-design.mdc`](.cursor/rules/farhangi-design.mdc) — Material 3، Vazirmatn، accessibility.
- [`farhangi-coding.mdc`](.cursor/rules/farhangi-coding.mdc) — Kotlin conventions، state hoisting، بدون عدد جادویی.

## مهارت‌ها (Skills) — فعال‌سازی اجباری

Skills در `.agents/skills/` نصب‌شده‌اند. قبل از شروع هر حوزه، Skill مرتبط را بخوان و رعایت کن:

| حوزه | Skill |
|------|------|
| مسیریابی UI | `ui-skills-root` (قبل از هر کار UI اجرا کن) |
| ناوبری | `navigation-3` |
| تطبیق صفحه (tablet/foldable) | `adaptive` |
| edge-to-edge | `edge-to-edge` |
| استراتژی تست | `testing-setup` |
| Compose Styles API | `styles` |
| حساس‌سازی به drift طراحی | `improve-ui` |
| دسترس‌پذیری | `fixing-accessibility` |
| جلوگیری از slop بصری | `baseline-ui` |
| ثبت DESIGN.md | `create-design-md` (پس از تکمیل تم) |
| R8 / حجم اپ | `r8-analyzer` |

برای اجرای مسیریاب UI: `npx ui-skills start`.

## ریپازیتوری‌های مرجع — یاد بگیر، کپی نکن

الگوها را از این منابع یاد بگیر. **کد کپی ممنوع.** فقط ساختار، الگو و بهترین روش:

- [android/nowinandroid](https://github.com/android/nowinandroid) — معماری ماژولار، offline-first، M3، Navigation 3.
- [android/architecture-samples](https://github.com/android/architecture-samples) — MVVM، Repository، Clean.
- [android/compose-samples](https://github.com/android/compose-samples) — کامپوننت، انیمیشن، ناوبری، تم.
- [ibelick/ui-skills](https://github.com/ibelick/ui-skills) — UX critique، accessibility، craft.

## قوانین سخت

1. **فقط** طبق `docs/FARHANGI_DESIGN_PLAN.md` بیلد کن.
2. **هرگز** زبان طراحی غیر Material 3 نساز.
3. **هرگز** UI یا Domain را مستقیم به Supabase SDK کوپل نکن — پشت Gateway و Repository interface.
4. **هرگز** عدد جادویی استفاده نکن — توکن یا named constant.
5. **هرگز** کد تکراری ننویس — استخراج کن.
6. **هرگز** از RTL و Vazirmatن تخطی نکن.
7. **هرگز** بدون state hoisting Composable نساز.
8. **هرگز** قبل از تحلیل و برنامه کد ننویس.
9. **هرگز** کد از ریپازیتوری‌های مرجع کپی نکن — فقط الگو.
10. فاز ۱ MVP را **اول** کامل کن، بعد فاز بعدی.

## ترتیب خروجی هر پاسخ فنی

1. Product Thinking
2. UX Thinking
3. UI Thinking
4. Architecture
5. Risks
6. Suggestions
7. Implementation Plan

## شروع

فاز ۰ (Bootstrap) را شروع کن:

- ساختار Gradle چندماژولی با version catalog.
- `core/designsystem` با تم M3 + Vazirmatn + توکن‌ها.
- `app` با MainActivity، NavHost، Scaffold، Bottom Nav (۵ تب).
- Hilt setup.
- edge-to-edge فعال از روز اول.

بعد فاز ۱ MVP را طبق سند طراحی پیش ببر.

**مستندسازی هر مرحله الزامی است. قبل از کد، توضیح بده چه می‌سازی و چرا.**
