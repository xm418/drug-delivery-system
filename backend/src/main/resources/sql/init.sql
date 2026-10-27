-- 医共体药品配送管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS drug_delivery DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE drug_delivery;

-- 1. 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 2. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role_id INT COMMENT '角色ID',
    institution_id BIGINT COMMENT '所属机构ID',
    status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    avatar VARCHAR(255) COMMENT '头像',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 3. 医疗机构表
CREATE TABLE IF NOT EXISTS institution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '机构名称',
    code VARCHAR(50) UNIQUE COMMENT '机构编码',
    level TINYINT NOT NULL COMMENT '机构级别 1-县级 2-乡级 3-村级',
    parent_id BIGINT DEFAULT 0 COMMENT '父级机构ID',
    address VARCHAR(255) COMMENT '地址',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医疗机构表';

-- 4. 药品分类表
CREATE TABLE IF NOT EXISTS drug_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) COMMENT '分类编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父级分类ID',
    sort INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品分类表';

-- 5. 药品表
CREATE TABLE IF NOT EXISTS drug (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    drug_name VARCHAR(100) NOT NULL COMMENT '药品名称',
    drug_code VARCHAR(50) UNIQUE COMMENT '药品编码',
    category_id BIGINT COMMENT '分类ID',
    specification VARCHAR(200) COMMENT '规格',
    manufacturer VARCHAR(100) COMMENT '生产厂家',
    approval_number VARCHAR(50) COMMENT '批准文号',
    price DECIMAL(10,2) COMMENT '单价',
    unit VARCHAR(20) COMMENT '单位',
    status TINYINT DEFAULT 1 COMMENT '状态 1-在售 0-下架',
    description TEXT COMMENT '描述',
    image VARCHAR(255) COMMENT '图片',
    warning_threshold INT DEFAULT 10 COMMENT '预警阈值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品表';

-- 6. 库存表
CREATE TABLE IF NOT EXISTS stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    institution_id BIGINT NOT NULL COMMENT '机构ID',
    quantity INT DEFAULT 0 COMMENT '库存数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    batch_number VARCHAR(50) COMMENT '批号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '有效期至',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    UNIQUE KEY uk_drug_institution (drug_id, institution_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 7. 库存操作记录表
CREATE TABLE IF NOT EXISTS stock_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    institution_id BIGINT NOT NULL COMMENT '机构ID',
    operation_type TINYINT NOT NULL COMMENT '操作类型 1-入库 2-出库 3-调拨',
    quantity INT NOT NULL COMMENT '操作数量',
    before_quantity INT COMMENT '操作前数量',
    after_quantity INT COMMENT '操作后数量',
    batch_number VARCHAR(50) COMMENT '批号',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    remark VARCHAR(500) COMMENT '备注',
    related_order_no VARCHAR(50) COMMENT '关联订单号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存操作记录表';

-- 8. 订单表
CREATE TABLE IF NOT EXISTS drug_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    institution_id BIGINT NOT NULL COMMENT '申请机构ID',
    user_id BIGINT COMMENT '申请人ID',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '订单总金额',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    status TINYINT DEFAULT 0 COMMENT '订单状态 0-待审核 1-已审核 2-已驳回 3-配送中 4-已完成 5-已取消',
    remark VARCHAR(500) COMMENT '备注',
    approve_by BIGINT COMMENT '审核人ID',
    approve_time DATETIME COMMENT '审核时间',
    approve_remark VARCHAR(500) COMMENT '审核意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 9. 订单明细表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    drug_name VARCHAR(100) COMMENT '药品名称',
    specification VARCHAR(200) COMMENT '规格',
    unit VARCHAR(20) COMMENT '单位',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(10,2) COMMENT '总价',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 10. 配送任务表
CREATE TABLE IF NOT EXISTS delivery_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_no VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编号',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    from_institution_id BIGINT NOT NULL COMMENT '发货机构ID',
    to_institution_id BIGINT NOT NULL COMMENT '收货机构ID',
    delivery_person_id BIGINT COMMENT '配送员ID',
    delivery_person_name VARCHAR(50) COMMENT '配送员姓名',
    delivery_phone VARCHAR(20) COMMENT '配送员电话',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待配送 1-配送中 2-已送达 3-已完成',
    delivery_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '收货时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送任务表';

-- 初始化数据
-- 角色数据
INSERT INTO sys_role (role_name, role_code, description, status) VALUES
('系统管理员', 'admin', '系统管理员，拥有所有权限', 1),
('县级管理员', 'county_admin', '县级医疗机构管理员', 1),
('乡级管理员', 'township_admin', '乡级医疗机构管理员', 1),
('村级用户', 'village_user', '村级医疗机构用户', 1),
('配送员', 'delivery', '药品配送员', 1);

-- 用户数据（密码都是 123456 的MD5：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO sys_user (username, password, real_name, phone, role_id, institution_id, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800138000', 1, 1, 1);

-- 医疗机构数据
INSERT INTO institution (name, code, level, parent_id, address, contact_person, contact_phone, status) VALUES
('县人民医院', 'COUNTY001', 1, 0, '县城中心路1号', '张院长', '13800138001', 1),
('县中医院', 'COUNTY002', 1, 0, '县城健康路2号', '李院长', '13800138002', 1),
('第一乡卫生院', 'TOWNSHIP001', 2, 1, '第一乡政府街', '王院长', '13800138003', 1),
('第二乡卫生院', 'TOWNSHIP002', 2, 1, '第二乡政府街', '赵院长', '13800138004', 1),
('A村卫生室', 'VILLAGE001', 3, 3, 'A村', '村医A', '13800138005', 1),
('B村卫生室', 'VILLAGE002', 3, 3, 'B村', '村医B', '13800138006', 1),
('C村卫生室', 'VILLAGE003', 3, 4, 'C村', '村医C', '13800138007', 1);

-- 药品分类数据
INSERT INTO drug_category (category_name, category_code, parent_id, sort, status) VALUES
('西药', 'XY', 0, 1, 1),
('中药', 'ZY', 0, 2, 1),
('中成药', 'ZCY', 0, 3, 1),
('抗生素', 'KSS', 1, 1, 1),
('解热镇痛药', 'JRZTY', 1, 2, 1),
('呼吸系统用药', 'HXXT', 1, 3, 1),
('消化系统用药', 'XHXT', 1, 4, 1),
('心血管系统用药', 'XXGX', 1, 5, 1),
('清热解毒', 'QRJD', 3, 1, 1),
('补气养血', 'BQYX', 3, 2, 1);

-- 药品数据
INSERT INTO drug (drug_name, drug_code, category_id, specification, manufacturer, approval_number, price, unit, status, warning_threshold) VALUES
('阿莫西林胶囊', 'AMX001', 4, '0.25g*24粒', '华北制药股份有限公司', '国药准字H13021770', 12.50, '盒', 1, 50),
('头孢拉定胶囊', 'TBL001', 4, '0.25g*24粒', '石药集团欧意药业', '国药准字H13020787', 15.80, '盒', 1, 30),
('布洛芬缓释胶囊', 'BLF001', 5, '0.3g*20粒', '中美史克', '国药准字H10900089', 18.00, '盒', 1, 40),
('对乙酰氨基酚片', 'DYAJ001', 5, '0.5g*12片', '拜耳医药', '国药准字H11021490', 8.50, '盒', 1, 60),
('感冒灵颗粒', 'GML001', 6, '10g*9袋', '三九医药', '国药准字Z44021940', 15.00, '盒', 1, 50),
('复方甘草片', 'FFGC001', 6, '100片', '天津药业集团', '国药准字H12020802', 6.50, '瓶', 1, 80),
('奥美拉唑肠溶胶囊', 'AML001', 7, '20mg*14粒', '阿斯利康制药', '国药准字H20030945', 45.00, '盒', 1, 30),
('多潘立酮片', 'DPLT001', 7, '10mg*30片', '西安杨森', '国药准字H10910003', 22.00, '盒', 1, 40),
('硝苯地平缓释片', 'XBDP001', 8, '10mg*30片', '拜耳医药', '国药准字H10910056', 32.00, '盒', 1, 35),
('阿司匹林肠溶片', 'ASPL001', 8, '100mg*30片', '拜耳医药', '国药准字J20130078', 16.00, '盒', 1, 45),
('连花清瘟胶囊', 'LHQW001', 9, '0.35g*24粒', '以岭药业', '国药准字Z20040063', 14.80, '盒', 1, 100),
('板蓝根颗粒', 'BLG001', 9, '10g*20袋', '白云山制药', '国药准字Z44020026', 12.00, '盒', 1, 80),
('复方丹参片', 'FFDS001', 10, '60片', '同仁堂制药', '国药准字Z11021185', 25.00, '瓶', 1, 50),
('六味地黄丸', 'LWDH001', 10, '9g*10丸', '同仁堂制药', '国药准字Z11020133', 28.00, '盒', 1, 40),
('维生素C片', 'WSSC001', 1, '100mg*100片', '东北制药', '国药准字H21020701', 5.50, '瓶', 1, 100);

-- 库存数据（县人民医院有初始库存）
INSERT INTO stock (drug_id, institution_id, quantity, unit_price, batch_number, production_date, expiry_date) VALUES
(1, 1, 200, 12.50, '20240101', '2024-01-01', '2026-01-01'),
(2, 1, 150, 15.80, '20240201', '2024-02-01', '2026-02-01'),
(3, 1, 180, 18.00, '20240115', '2024-01-15', '2026-01-15'),
(4, 1, 300, 8.50, '20240301', '2024-03-01', '2026-03-01'),
(5, 1, 5, 15.00, '20240101', '2024-01-01', '2025-12-01'),
(6, 1, 100, 6.50, '20240215', '2024-02-15', '2026-02-15'),
(7, 1, 80, 45.00, '20240110', '2024-01-10', '2026-01-10'),
(8, 1, 120, 22.00, '20240305', '2024-03-05', '2026-03-05'),
(9, 1, 90, 32.00, '20240220', '2024-02-20', '2026-02-20'),
(10, 1, 110, 16.00, '20240125', '2024-01-25', '2026-01-25'),
(11, 1, 50, 14.80, '20240401', '2024-04-01', '2026-04-01'),
(12, 1, 200, 12.00, '20240315', '2024-03-15', '2026-03-15'),
(13, 1, 60, 25.00, '20240210', '2024-02-10', '2026-02-10'),
(14, 1, 70, 28.00, '20240105', '2024-01-05', '2026-01-05'),
(15, 1, 500, 5.50, '20240410', '2024-04-10', '2026-04-10'),
(1, 3, 20, 12.50, '20240101', '2024-01-01', '2026-01-01'),
(3, 3, 15, 18.00, '20240115', '2024-01-15', '2026-01-15'),
(5, 3, 10, 15.00, '20240101', '2024-01-01', '2025-12-01');
