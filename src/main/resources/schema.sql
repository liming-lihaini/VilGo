-- 村民档案表
CREATE TABLE IF NOT EXISTS residents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    gender VARCHAR(10) COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    household_status VARCHAR(20) DEFAULT 'normal' COMMENT '户籍状态: normal-正常, cancelled-已销户',
    person_type VARCHAR(20) COMMENT '人员类型: farmer-农民, worker-工人, teacher-教师, doctor-医生, Student-学生, other-其他',
    security_type VARCHAR(20) COMMENT '保障类型: pension-养老保险, insurance-医疗保险, allowance-低保, none-未参保',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '住址',
    household_head VARCHAR(50) COMMENT '户主姓名',
    relationship VARCHAR(20) COMMENT '与户主关系',
    village VARCHAR(50) COMMENT '所属村组',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '软删除标记: 0-正常, 1-已删除'
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_residents_id_card ON residents(id_card);
CREATE INDEX IF NOT EXISTS idx_residents_name ON residents(name);
CREATE INDEX IF NOT EXISTS idx_residents_household_status ON residents(household_status);
CREATE INDEX IF NOT EXISTS idx_residents_person_type ON residents(person_type);
CREATE INDEX IF NOT EXISTS idx_residents_deleted ON residents(deleted);