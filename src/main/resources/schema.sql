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