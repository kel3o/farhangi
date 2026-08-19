---
product: Farhangi
platform: android
updated: 2026-08-18
---

# Farhangi DESIGN.md

## Intent

Minimal Material 3 cultural reading app for a governmental/religious cultural organization. Persian RTL first. Efficiency over ornament. Version 2 information architecture: Home plus four destinations (Books, Courses, Competitions, Magazine). Profile from the top-bar avatar.

## Foundations

### Color

- Primary seed fallback: `#0F6E56` (calm cultural teal-green)
- Dynamic Color on Android 12+ with static M3 fallback
- Roles only: primary, onPrimary, surface, onSurface, surfaceVariant, outline, error

### Typography

- Family: Vazirmatn (Regular / Medium / Bold)
- Scale: M3 Display → Label mapped in `FarhangiTypography`
- UI copy: Persian, short, readable

### Shape

- M3 scale: 4 / 8 / 12 / 16 / 28 dp

### Spacing

- 4dp grid: 4 · 8 · 12 · 16 · 24 · 32 (`FarhangiSpacing`)
- Touch target minimum: 48dp

### Elevation

- Prefer tonal surface elevation over heavy shadow

## Layout

- RTL via `LocalLayoutDirection.Rtl` in `FarhangiTheme`
- Adaptive navigation: `NavigationSuiteScaffold` (bar on phone, rail on larger)
- Five destinations: Home, Books, Courses, Competitions, Magazine
- Profile from Top App Bar avatar
- Edge-to-edge from MainActivity; Scaffold / Material bars own insets
- Search is global from Top App Bar — not a sixth tab

## Components

Owned by `core/designsystem` and `core/ui`:

- `FarhangiTopAppBar` — centered title, optional back/search
- `BookCard` / `CourseCard` / `ArticleCard` — interactive content containers only
- `SectionHeader`, `EmptyState`, `LoadingState`

## Do / Don't

- Do: Material 3 only, named spacing tokens, state-hoisted screens
- Don't: non-M3 visual languages, magic numbers, cards used as decoration, sixth bottom-nav tab for search