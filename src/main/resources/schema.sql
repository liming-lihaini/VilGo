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