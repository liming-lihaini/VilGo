-- 村民档案表
CREATE TABLE IF NOT EXISTS residents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    gender VARCHAR(10),
    birth_date DATE,
    household_status VARCHAR(20) DEFAULT 'normal',
    person_type VARCHAR(20),
    security_type VARCHAR(200),
    phone VARCHAR(20),
    address VARCHAR(200),
    household_head VARCHAR(50),
    relationship VARCHAR(20),
    village VARCHAR(50),
    is_local_household INTEGER DEFAULT 1,
    is_household_head INTEGER DEFAULT 0,
    is_local_resident INTEGER DEFAULT 1,
    external_address VARCHAR(200),
    remark TEXT,
    create_time TEXT,
    update_time TEXT,
    deleted INTEGER DEFAULT 0
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_residents_id_card ON residents(id_card);
CREATE INDEX IF NOT EXISTS idx_residents_name ON residents(name);
CREATE INDEX IF NOT EXISTS idx_residents_household_status ON residents(household_status);
CREATE INDEX IF NOT EXISTS idx_residents_person_type ON residents(person_type);
CREATE INDEX IF NOT EXISTS idx_residents_deleted ON residents(deleted);

-- 村民附件表
CREATE TABLE IF NOT EXISTS resident_attachments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    resident_id INTEGER NOT NULL,
    file_name VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size INTEGER,
    file_category VARCHAR(50) NOT NULL,
    create_time TEXT,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (resident_id) REFERENCES residents(id)
);

CREATE INDEX IF NOT EXISTS idx_attachments_resident_id ON resident_attachments(resident_id);
CREATE INDEX IF NOT EXISTS idx_attachments_category ON resident_attachments(file_category);

-- 家庭户表
CREATE TABLE IF NOT EXISTS households (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    household_no VARCHAR(20) NOT NULL UNIQUE,
    head_id INTEGER,
    head_name VARCHAR(50),
    head_id_card VARCHAR(18),
    address VARCHAR(200),
    phone VARCHAR(20),
    member_count INTEGER DEFAULT 0,
    create_time TEXT,
    update_time TEXT,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_households_no ON households(household_no);
CREATE INDEX IF NOT EXISTS idx_households_head ON households(head_id);
CREATE INDEX IF NOT EXISTS idx_households_deleted ON households(deleted);

-- 家庭成员表
CREATE TABLE IF NOT EXISTS household_members (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    household_id INTEGER NOT NULL,
    resident_id INTEGER NOT NULL,
    relation VARCHAR(20),
    create_time TEXT,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (household_id) REFERENCES households(id),
    FOREIGN KEY (resident_id) REFERENCES residents(id)
);

CREATE INDEX IF NOT EXISTS idx_members_household ON household_members(household_id);
CREATE INDEX IF NOT EXISTS idx_members_resident ON household_members(resident_id);

-- 年度收入表
CREATE TABLE IF NOT EXISTS household_income (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    household_id INTEGER NOT NULL,
    year INTEGER NOT NULL,
    total_income DECIMAL(12,2),
    per_capita_income DECIMAL(12,2),
    income_source VARCHAR(200),
    remark VARCHAR(500),
    create_time TEXT,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (household_id) REFERENCES households(id)
);

CREATE INDEX IF NOT EXISTS idx_income_household ON household_income(household_id);
CREATE INDEX IF NOT EXISTS idx_income_year ON household_income(year);

-- 户籍变动表
CREATE TABLE IF NOT EXISTS household_changes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    household_id INTEGER NOT NULL,
    change_type VARCHAR(20) NOT NULL,
    change_time TEXT,
    change_reason VARCHAR(200),
    related_persons VARCHAR(200),
    before_status VARCHAR(100),
    after_status VARCHAR(100),
    remark VARCHAR(500),
    create_time TEXT,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (household_id) REFERENCES households(id)
);

CREATE INDEX IF NOT EXISTS idx_changes_household ON household_changes(household_id);
CREATE INDEX IF NOT EXISTS idx_changes_type ON household_changes(change_type);

-- 特殊人群表
CREATE TABLE IF NOT EXISTS special_groups (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    resident_id INTEGER,
    id_card VARCHAR(18) NOT NULL,
    name VARCHAR(50),
    gender VARCHAR(10),
    birth_date DATE,
    phone VARCHAR(20),
    address VARCHAR(200),
    group_type VARCHAR(20) NOT NULL,
    helper_id INTEGER,
    helper_name VARCHAR(50),
    help_content TEXT,
    help_time TEXT,
    help_result TEXT,
    help_status VARCHAR(20) DEFAULT 'ongoing',
    creator VARCHAR(50),
    create_time TEXT,
    update_time TEXT,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_special_groups_id_card ON special_groups(id_card);
CREATE INDEX IF NOT EXISTS idx_special_groups_group_type ON special_groups(group_type);
CREATE INDEX IF NOT EXISTS idx_special_groups_helper ON special_groups(helper_id);
CREATE INDEX IF NOT EXISTS idx_special_groups_deleted ON special_groups(deleted);

-- 帮扶记录表
CREATE TABLE IF NOT EXISTS help_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    special_group_id INTEGER NOT NULL,
    help_content TEXT,
    help_time TEXT,
    help_result TEXT,
    creator VARCHAR(50),
    create_time TEXT,
    update_time TEXT,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (special_group_id) REFERENCES special_groups(id)
);

CREATE INDEX IF NOT EXISTS idx_help_records_group ON help_records(special_group_id);
CREATE INDEX IF NOT EXISTS idx_help_records_time ON help_records(help_time);
CREATE INDEX IF NOT EXISTS idx_help_records_deleted ON help_records(deleted);

-- 职位管理表
CREATE TABLE IF NOT EXISTS positions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50),
    sort_order INTEGER DEFAULT 0,
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0
);

-- 两委班子成员表
CREATE TABLE IF NOT EXISTS two_committee (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    resident_id INTEGER,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    position_ids VARCHAR(200),
    position_names VARCHAR(200),
    phone VARCHAR(20),
    divided_work VARCHAR(100),
    join_date VARCHAR(20),
    creator VARCHAR(50),
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (resident_id) REFERENCES residents(id)
);

CREATE INDEX IF NOT EXISTS idx_two_committee_resident ON two_committee(resident_id);
CREATE INDEX IF NOT EXISTS idx_two_committee_name ON two_committee(name);
CREATE INDEX IF NOT EXISTS idx_two_committee_deleted ON two_committee(deleted);

-- 任务表
CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    year INTEGER,
    assigner VARCHAR(50),
    assignee_id INTEGER,
    content TEXT,
    deadline VARCHAR(20),
    status VARCHAR(20) DEFAULT 'pending',
    result TEXT,
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (assignee_id) REFERENCES two_committee(id)
);

CREATE INDEX IF NOT EXISTS idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_deadline ON tasks(deadline);
CREATE INDEX IF NOT EXISTS idx_tasks_deleted ON tasks(deleted);

-- 公益活动表
CREATE TABLE IF NOT EXISTS public_activities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    activity_type VARCHAR(50),
    start_time VARCHAR(50) NOT NULL,
    end_time VARCHAR(50) NOT NULL,
    location VARCHAR(200),
    content TEXT,
    requirement TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    summary TEXT,
    creator VARCHAR(50),
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_activities_name ON public_activities(name);
CREATE INDEX IF NOT EXISTS idx_activities_start_time ON public_activities(start_time);
CREATE INDEX IF NOT EXISTS idx_activities_status ON public_activities(status);
CREATE INDEX IF NOT EXISTS idx_activities_deleted ON public_activities(deleted);

-- 活动报名表
CREATE TABLE IF NOT EXISTS activity_signups (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    activity_id INTEGER NOT NULL,
    resident_id INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'registered',
    sign_in_time VARCHAR(50),
    work_hours DECIMAL(5,1),
    work_value DECIMAL(10,2),
    remark VARCHAR(500),
    creator VARCHAR(50),
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (activity_id) REFERENCES public_activities(id),
    FOREIGN KEY (resident_id) REFERENCES residents(id)
);

CREATE INDEX IF NOT EXISTS idx_signups_activity ON activity_signups(activity_id);
CREATE INDEX IF NOT EXISTS idx_signups_resident ON activity_signups(resident_id);
CREATE INDEX IF NOT EXISTS idx_signups_status ON activity_signups(status);
CREATE INDEX IF NOT EXISTS idx_signups_deleted ON activity_signups(deleted);

-- 会议记录表
CREATE TABLE IF NOT EXISTS meetings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    meeting_time VARCHAR(50),
    location VARCHAR(200),
    attendees VARCHAR(500),
    content TEXT,
    resolution TEXT,
    implementation TEXT,
    creator VARCHAR(50),
    create_time VARCHAR(50),
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_meetings_time ON meetings(meeting_time);
CREATE INDEX IF NOT EXISTS idx_meetings_deleted ON meetings(deleted);

-- =============================================
-- 党务管理相关表
-- =============================================

-- 党员档案表
CREATE TABLE IF NOT EXISTS party_members (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    name VARCHAR(50),
    gender VARCHAR(10),
    birth_date DATE,
    phone VARCHAR(20),
    address VARCHAR(200),
    join_date VARCHAR(20),
    convert_date VARCHAR(20),
    status VARCHAR(20) DEFAULT 'activist',
    creator VARCHAR(50),
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_party_members_id_card ON party_members(id_card);
CREATE INDEX IF NOT EXISTS idx_party_members_status ON party_members(status);
CREATE INDEX IF NOT EXISTS idx_party_members_name ON party_members(name);
CREATE INDEX IF NOT EXISTS idx_party_members_deleted ON party_members(deleted);

-- 党务活动表
CREATE TABLE IF NOT EXISTS party_activities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    theme VARCHAR(100) NOT NULL,
    activity_type VARCHAR(50),
    activity_time VARCHAR(50),
    location VARCHAR(200),
    participation VARCHAR(500),
    content TEXT,
    creator VARCHAR(50),
    create_time VARCHAR(50),
    update_time VARCHAR(50),
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (member_id) REFERENCES party_members(id)
);

CREATE INDEX IF NOT EXISTS idx_party_activities_member ON party_activities(member_id);
CREATE INDEX IF NOT EXISTS idx_party_activities_time ON party_activities(activity_time);
CREATE INDEX IF NOT EXISTS idx_party_activities_deleted ON party_activities(deleted);

-- 党费收缴表
CREATE TABLE IF NOT EXISTS party_dues (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    pay_month VARCHAR(20),
    pay_date VARCHAR(20),
    status VARCHAR(20) DEFAULT 'unpaid',
    remark VARCHAR(500),
    creator VARCHAR(50),
    create_time VARCHAR(50),
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (member_id) REFERENCES party_members(id)
);

CREATE INDEX IF NOT EXISTS idx_party_dues_member ON party_dues(member_id);
CREATE INDEX IF NOT EXISTS idx_party_dues_pay_month ON party_dues(pay_month);
CREATE INDEX IF NOT EXISTS idx_party_dues_status ON party_dues(status);
CREATE INDEX IF NOT EXISTS idx_party_dues_deleted ON party_dues(deleted);