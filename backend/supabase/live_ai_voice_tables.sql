-- SQL Migration for Live AI Sessions and Voice Messages Tables

-- 1. Create live_ai_sessions table
create table if not exists live_ai_sessions (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id),
    zone text,
    ai_summary text,
    ppe_compliance_percent int,
    blueprint_deviation_mm float,
    detected_objects jsonb,
    created_at timestamptz not null default now()
);

-- 2. Create voice_messages table
create table if not exists voice_messages (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id),
    session_id uuid not null,
    role text not null check (role in ('user', 'assistant')),
    text_content text not null,
    created_at timestamptz not null default now()
);
