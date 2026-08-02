# Supabase setup (Farhangi)

## Overview

Gateways are backend-agnostic:

- `AuthGateway` / `ContentGateway` (interfaces)
- `SupabaseAuthAdapter` / `SupabaseContentAdapter` (when configured)
- `DemoAuthGateway` / `DemoContentGateway` (fallback when keys are blank)

UI and Domain never depend on Supabase SDK.

## Configure credentials

1. Copy `local.properties.example` → `local.properties` (already gitignored for typical Android projects).
2. Set:

```properties
SUPABASE_URL=https://YOUR_PROJECT.supabase.co
SUPABASE_ANON_KEY=YOUR_ANON_KEY
SUPABASE_AUTH_ENABLED=false
```

3. Sync Gradle / rebuild. `core/network` injects these into `BuildConfig`.

Binding rules:

- Empty URL/key → **Demo** content + **Demo** auth
- URL/key set → **Supabase** content
- `SUPABASE_AUTH_ENABLED=true` → **Supabase** phone OTP (needs SMS provider)
- `SUPABASE_AUTH_ENABLED=false` → **Demo** auth (OTP `123456`) even when content is live

## Demo OTP

With Demo auth: any valid-length phone, OTP code `123456`.

## Expected tables (Content)

Align with `docs/FARHANGI_DESIGN_PLAN.md`: `books`, `courses`, `articles`, `announcements`, plus progress tables as needed.

Project currently wired: **farhangi** (`tfiytmoncssxdbbzyaab`).
