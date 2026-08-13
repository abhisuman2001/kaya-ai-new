-- Row Level Security (RLS) Policies for Supabase Tables

-- Enable RLS on live_ai_sessions
alter table live_ai_sessions enable row level security;

-- Policies for live_ai_sessions
drop policy if exists "Users can select own live_ai_sessions" on live_ai_sessions;
create policy "Users can select own live_ai_sessions"
    on live_ai_sessions
    for select
    using (auth.uid() = user_id);

drop policy if exists "Users can insert own live_ai_sessions" on live_ai_sessions;
create policy "Users can insert own live_ai_sessions"
    on live_ai_sessions
    for insert
    with check (auth.uid() = user_id);

drop policy if exists "Users can update own live_ai_sessions" on live_ai_sessions;
create policy "Users can update own live_ai_sessions"
    on live_ai_sessions
    for update
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);

drop policy if exists "Users can delete own live_ai_sessions" on live_ai_sessions;
create policy "Users can delete own live_ai_sessions"
    on live_ai_sessions
    for delete
    using (auth.uid() = user_id);

-- Enable RLS on voice_messages
alter table voice_messages enable row level security;

-- Policies for voice_messages
drop policy if exists "Users can select own voice_messages" on voice_messages;
create policy "Users can select own voice_messages"
    on voice_messages
    for select
    using (auth.uid() = user_id);

drop policy if exists "Users can insert own voice_messages" on voice_messages;
create policy "Users can insert own voice_messages"
    on voice_messages
    for insert
    with check (auth.uid() = user_id);

drop policy if exists "Users can update own voice_messages" on voice_messages;
create policy "Users can update own voice_messages"
    on voice_messages
    for update
    using (auth.uid() = user_id)
    with check (auth.uid() = user_id);

drop policy if exists "Users can delete own voice_messages" on voice_messages;
create policy "Users can delete own voice_messages"
    on voice_messages
    for delete
    using (auth.uid() = user_id);

-- RLS Policies for existing site tables (site_events, reports, tasks)
do $$
begin
    if exists (select 1 from pg_tables where tablename = 'site_events') then
        alter table site_events enable row level security;
        drop policy if exists "Users can select own site_events" on site_events;
        create policy "Users can select own site_events" on site_events for select using (auth.uid() = user_id);
        drop policy if exists "Users can insert own site_events" on site_events;
        create policy "Users can insert own site_events" on site_events for insert with check (auth.uid() = user_id);
    end if;

    if exists (select 1 from pg_tables where tablename = 'reports') then
        alter table reports enable row level security;
        drop policy if exists "Users can select own reports" on reports;
        create policy "Users can select own reports" on reports for select using (auth.uid() = user_id);
        drop policy if exists "Users can insert own reports" on reports;
        create policy "Users can insert own reports" on reports for insert with check (auth.uid() = user_id);
    end if;

    if exists (select 1 from pg_tables where tablename = 'tasks') then
        alter table tasks enable row level security;
        drop policy if exists "Users can select own tasks" on tasks;
        create policy "Users can select own tasks" on tasks for select using (auth.uid() = user_id);
        drop policy if exists "Users can insert own tasks" on tasks;
        create policy "Users can insert own tasks" on tasks for insert with check (auth.uid() = user_id);
    end if;
end $$;
