-- Farhangi content schema (applied to project tfiytmoncssxdbbzyaab)
-- Matches Android DTOs in core/network

create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users (id) on delete cascade,
  phone text,
  display_name text,
  avatar_url text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.books (
  id text primary key,
  title text not null,
  author text not null,
  cover_url text,
  categories text[] not null default '{}',
  total_pages integer not null default 0,
  rating double precision,
  description text not null default '',
  created_at timestamptz not null default now()
);

create table if not exists public.courses (
  id text primary key,
  title text not null,
  type text not null check (type in ('PRACTICAL', 'PROFESSIONAL')),
  cover_url text,
  description text not null default '',
  sections jsonb not null default '[]'::jsonb,
  progress real not null default 0,
  created_at timestamptz not null default now()
);

create table if not exists public.articles (
  id text primary key,
  title text not null,
  type text not null,
  category text not null,
  summary text not null default '',
  body text not null default '',
  media_url text,
  cover_url text,
  published_at timestamptz not null default now()
);

create table if not exists public.announcements (
  id text primary key,
  title text not null,
  body text not null,
  published_at timestamptz not null default now()
);

create table if not exists public.book_progress (
  user_id uuid not null references auth.users (id) on delete cascade,
  book_id text not null references public.books (id) on delete cascade,
  page integer not null default 0,
  percent real not null default 0,
  updated_at timestamptz not null default now(),
  primary key (user_id, book_id)
);

create table if not exists public.bookmarks (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users (id) on delete cascade,
  book_id text not null references public.books (id) on delete cascade,
  page integer not null default 0,
  note text,
  created_at timestamptz not null default now()
);

create table if not exists public.highlights (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users (id) on delete cascade,
  book_id text not null references public.books (id) on delete cascade,
  page integer not null default 0,
  text text not null,
  color text,
  created_at timestamptz not null default now()
);

create table if not exists public.course_progress (
  user_id uuid not null references auth.users (id) on delete cascade,
  course_id text not null references public.courses (id) on delete cascade,
  section_id text not null,
  completed boolean not null default false,
  updated_at timestamptz not null default now(),
  primary key (user_id, course_id, section_id)
);

create table if not exists public.reading_goals (
  user_id uuid not null references auth.users (id) on delete cascade,
  year integer not null,
  goal_count integer not null default 0,
  current_count integer not null default 0,
  primary key (user_id, year)
);

alter table public.profiles enable row level security;
alter table public.books enable row level security;
alter table public.courses enable row level security;
alter table public.articles enable row level security;
alter table public.announcements enable row level security;
alter table public.book_progress enable row level security;
alter table public.bookmarks enable row level security;
alter table public.highlights enable row level security;
alter table public.course_progress enable row level security;
alter table public.reading_goals enable row level security;

create policy "books_public_read" on public.books for select to anon, authenticated using (true);
create policy "courses_public_read" on public.courses for select to anon, authenticated using (true);
create policy "articles_public_read" on public.articles for select to anon, authenticated using (true);
create policy "announcements_public_read" on public.announcements for select to anon, authenticated using (true);

create policy "profiles_own_all" on public.profiles for all to authenticated using (auth.uid() = id) with check (auth.uid() = id);
create policy "book_progress_own_all" on public.book_progress for all to authenticated using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "bookmarks_own_all" on public.bookmarks for all to authenticated using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "highlights_own_all" on public.highlights for all to authenticated using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "course_progress_own_all" on public.course_progress for all to authenticated using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "reading_goals_own_all" on public.reading_goals for all to authenticated using (auth.uid() = user_id) with check (auth.uid() = user_id);
