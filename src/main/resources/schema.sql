-- 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS domitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE domitory;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT COMMENT '性别：1-男，0-女',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 楼宇表
CREATE TABLE IF NOT EXISTS building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_name VARCHAR(50) NOT NULL COMMENT '楼宇名称',
    building_code VARCHAR(20) NOT NULL UNIQUE COMMENT '楼宇编号',
    floors INT COMMENT '楼层数',
    rooms_per_floor INT COMMENT '每层房间数',
    gender_type TINYINT COMMENT '性别类型：1-男寝，0-女寝，2-混合',
    manager_id BIGINT COMMENT '宿管ID',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼宇表';

-- 房间表
CREATE TABLE IF NOT EXISTS room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    building_id BIGINT NOT NULL COMMENT '楼宇ID',
    floor INT COMMENT '所在楼层',
    bed_count INT DEFAULT 4 COMMENT '床位数量',
    occupied_count INT DEFAULT 0 COMMENT '已入住人数',
    room_type VARCHAR(20) COMMENT '房间类型：标准间、豪华间等',
    price DECIMAL(10,2) COMMENT '房间单价/月',
    status TINYINT DEFAULT 1 COMMENT '状态：1-可用，0-已满，2-维修中',
    description VARCHAR(500) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (building_id) REFERENCES building(id),
    UNIQUE KEY uk_building_room (building_id, room_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- 维修申请表
CREATE TABLE IF NOT EXISTS repair_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_no VARCHAR(50) NOT NULL UNIQUE COMMENT '申请单号',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    repair_type VARCHAR(50) COMMENT '维修类型：水电、家具、门锁等',
    title VARCHAR(100) COMMENT '标题',
    content TEXT COMMENT '维修内容描述',
    image_url VARCHAR(500) COMMENT '图片URL，多张用逗号分隔',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已完成，3-已取消',
    handler_id BIGINT COMMENT '处理人ID（后勤人员）',
    handle_content TEXT COMMENT '处理内容',
    handle_time DATETIME COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES sys_user(id),
    FOREIGN KEY (room_id) REFERENCES room(id),
    FOREIGN KEY (handler_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修申请表';

-- 账单表
CREATE TABLE IF NOT EXISTS bill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bill_no VARCHAR(50) NOT NULL UNIQUE COMMENT '账单编号',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    bill_type VARCHAR(20) NOT NULL COMMENT '账单类型：住宿费、电费、水费、网费等',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    billing_month VARCHAR(20) COMMENT '账单月份：yyyy-MM',
    due_date DATE COMMENT '到期日期',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未支付，1-已支付，2-已逾期',
    pay_time DATETIME COMMENT '支付时间',
    payment_method VARCHAR(20) COMMENT '支付方式',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES sys_user(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

-- 记账记录表
CREATE TABLE IF NOT EXISTS account_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_no VARCHAR(50) NOT NULL UNIQUE COMMENT '记录编号',
    record_type TINYINT NOT NULL COMMENT '类型：1-收入，0-支出',
    category VARCHAR(50) COMMENT '分类：住宿费、维修费、水电费等',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    description VARCHAR(500) COMMENT '描述',
    operator_id BIGINT COMMENT '操作人ID',
    record_date DATE COMMENT '记录日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账记录表';

-- 宿舍分配表
CREATE TABLE IF NOT EXISTS dorm_allocation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    bed_number INT COMMENT '床位号',
    check_in_date DATE COMMENT '入住日期',
    check_out_date DATE COMMENT '退宿日期',
    status TINYINT DEFAULT 1 COMMENT '状态：1-入住中，0-已退宿',
    description VARCHAR(500) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES sys_user(id),
    FOREIGN KEY (room_id) REFERENCES room(id),
    UNIQUE KEY uk_student_active (student_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍分配表';

-- 初始化角色数据
INSERT INTO sys_role (role_name, role_code, description) VALUES 
('学生', 'STUDENT', '普通学生用户'),
('宿管', 'DORM_MANAGER', '宿舍管理员'),
('后勤', 'LOGISTICS', '后勤管理人员');

-- 初始化管理员账户（密码：123456，BCrypt加密）
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES 
('admin', '$2a$10$Eqck95qD2hBv0.6hJnVhIeG2s9q8w7e6r5t4y3u2i1o0p9a8s7d', '系统管理员', '13800138000', 1);

-- 给管理员分配所有角色
INSERT INTO sys_user_role (user_id, role_id) VALUES 
(1, 1), (1, 2), (1, 3);
