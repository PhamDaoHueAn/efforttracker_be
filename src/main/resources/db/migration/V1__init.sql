-- Users
create table if not exists users (
  id varchar(36) primary key default gen_random_uuid(),
  email varchar(255) not null unique,
  password varchar(255) not null,
  first_name varchar(255),
  last_name varchar(255),
  role varchar(20) not null default 'user',
  hourly_rate numeric(10,2) default 0.00,
  notes text,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

-- Tasks
create table if not exists tasks (
  id varchar(36) primary key default gen_random_uuid(),
  name varchar(255) not null,
  description text,
  status varchar(50) default 'open', -- open, in_progress, done, cancelled
  start_date date,
  due_date date,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

-- Time Entries
create table if not exists time_entries (
  id varchar(36) primary key default gen_random_uuid(),
  user_id varchar(36) not null references users(id) on delete cascade,
  task_id varchar(36) references tasks(id) on delete set null,
  date date not null,
  hours numeric(5,2) not null,
  description text not null,
  earnings numeric(10,2) not null default 0.00,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

create index if not exists idx_time_entries_user_date on time_entries(user_id, date);
create index if not exists idx_time_entries_task on time_entries(task_id);

-- Seed admin (email unique -> insert if not exists)
insert into users(id, email, password, first_name, last_name, role)
select gen_random_uuid(), 'admin@example.com', '$2a$10$gV2y4Dqm1mTg2pO7.1sTn.1rY8fFqH2bF6vSgS5t0v8w2oH2yC4Da', 'Admin', 'User', 'admin'
where not exists (select 1 from users where email = 'admin@example.com');
-- Mật khẩu ở trên là bcrypt của "admin123"
