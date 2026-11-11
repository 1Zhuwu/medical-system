-- =====================================================================
-- 医疗挂号系统 · 数据库建表脚本（实验二）
-- 数据库：MySQL 8.0    字符集：utf8mb4    引擎：InnoDB
-- 设计说明：
--   1. 全部表满足第三范式（3NF）：
--      - 1NF：字段均为原子值，无数组/逗号分隔串；
--      - 2NF：主键均为单一代理主键 id，非主键字段完全依赖主键；
--      - 3NF：非主键字段之间无传递依赖 —— 科室名称只存 department，
--             医生表只存 dept_id 而不冗余科室名称；挂号单只存 doctor_id/patient_id，
--             挂号费按下单时刻快照存入 registration.reg_fee，避免随医生调价而变动。
--   2. 约束齐备：主键 PRIMARY KEY、非空 NOT NULL、唯一 UNIQUE、外键 FOREIGN KEY。
--   3. 业务编号（dept_code / doctor_no / patient_no / reg_no / presc_no）均建唯一索引，
--      既是业务主键，也承担幂等防重放的作用。
--   4. 逻辑删除：deleted 0=正常 1=已删除。注意唯一索引建在业务字段上，
--      逻辑删除后该编号/手机号仍被占用；若业务需要删除后复用，
--      应改为物理删除，或把唯一索引改为 (字段, deleted) 并在删除时把字段改写为 "值_原id"。
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `medical_db`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `medical_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `prescription_item`;
DROP TABLE IF EXISTS `prescription`;
DROP TABLE IF EXISTS `registration`;
DROP TABLE IF EXISTS `drug`;
DROP TABLE IF EXISTS `patient`;
DROP TABLE IF EXISTS `doctor`;
DROP TABLE IF EXISTS `department`;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. 科室表
-- ---------------------------------------------------------------------
CREATE TABLE `department`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `dept_code`   VARCHAR(20)     NOT NULL COMMENT '科室编码，业务唯一标识',
    `dept_name`   VARCHAR(50)     NOT NULL COMMENT '科室名称',
    `dept_type`   VARCHAR(20)              DEFAULT '门诊' COMMENT '科室类型：门诊/住院/医技',
    `location`    VARCHAR(100)             DEFAULT NULL COMMENT '科室位置（楼层/诊区）',
    `phone`       VARCHAR(20)              DEFAULT NULL COMMENT '科室联系电话',
    `description` VARCHAR(255)             DEFAULT NULL COMMENT '科室简介',
    `sort_no`     INT             NOT NULL DEFAULT 0 COMMENT '排序号，越小越靠前',
    `status`      TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`),
    KEY `idx_dept_name` (`dept_name`),
    KEY `idx_dept_status` (`status`, `deleted`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='科室表';

-- ---------------------------------------------------------------------
-- 2. 医生表
--    外键 dept_id -> department.id，删除科室用 RESTRICT，防止"孤儿医生"
-- ---------------------------------------------------------------------
CREATE TABLE `doctor`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `doctor_no`    VARCHAR(20)     NOT NULL COMMENT '医生工号，业务唯一标识',
    `doctor_name`  VARCHAR(50)     NOT NULL COMMENT '医生姓名',
    `gender`       CHAR(1)         NOT NULL DEFAULT '1' COMMENT '性别：1男 2女',
    `phone`        VARCHAR(20)     NOT NULL COMMENT '手机号（敏感字段，展示需脱敏）',
    `id_card`      CHAR(18)        NOT NULL COMMENT '身份证号（敏感字段，展示需脱敏）',
    `dept_id`      BIGINT UNSIGNED NOT NULL COMMENT '所属科室ID',
    `title`        VARCHAR(20)              DEFAULT NULL COMMENT '职称：主任医师/副主任医师/主治医师/住院医师',
    `specialty`    VARCHAR(100)             DEFAULT NULL COMMENT '擅长领域',
    `introduction` VARCHAR(500)             DEFAULT NULL COMMENT '个人简介',
    `reg_fee`      DECIMAL(8, 2)   NOT NULL DEFAULT 0.00 COMMENT '挂号费（元）',
    `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1出诊 0停诊',
    `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_doctor_no` (`doctor_no`),
    UNIQUE KEY `uk_doctor_phone` (`phone`),
    UNIQUE KEY `uk_doctor_id_card` (`id_card`),
    KEY `idx_doctor_dept` (`dept_id`),
    KEY `idx_doctor_name` (`doctor_name`),
    CONSTRAINT `fk_doctor_dept` FOREIGN KEY (`dept_id`) REFERENCES `department` (`id`)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='医生表';

-- ---------------------------------------------------------------------
-- 3. 患者表
-- ---------------------------------------------------------------------
CREATE TABLE `patient`
(
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `patient_no`        VARCHAR(20)     NOT NULL COMMENT '患者档案号，业务唯一标识',
    `patient_name`      VARCHAR(50)     NOT NULL COMMENT '患者姓名',
    `gender`            CHAR(1)         NOT NULL DEFAULT '1' COMMENT '性别：1男 2女',
    `id_card`           CHAR(18)        NOT NULL COMMENT '身份证号（唯一，敏感字段）',
    `phone`             VARCHAR(20)     NOT NULL COMMENT '手机号（敏感字段）',
    `birth_date`        DATE                     DEFAULT NULL COMMENT '出生日期',
    `address`           VARCHAR(200)             DEFAULT NULL COMMENT '联系地址',
    `emergency_contact` VARCHAR(50)              DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone`   VARCHAR(20)              DEFAULT NULL COMMENT '紧急联系人电话',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_patient_no` (`patient_no`),
    UNIQUE KEY `uk_patient_id_card` (`id_card`),
    KEY `idx_patient_name` (`patient_name`),
    KEY `idx_patient_phone` (`phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='患者表';

-- ---------------------------------------------------------------------
-- 4. 挂号记录表
--    同一医生同一时段不能重复给同一患者挂号 -> 唯一索引 (patient_id, doctor_id, visit_date, time_slot)
--    对应实验三「重复挂号」的业务拦截，数据库层先兜底
-- ---------------------------------------------------------------------
CREATE TABLE `registration`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `reg_no`      VARCHAR(32)     NOT NULL COMMENT '挂号单号，业务唯一标识',
    `patient_id`  BIGINT UNSIGNED NOT NULL COMMENT '患者ID',
    `doctor_id`   BIGINT UNSIGNED NOT NULL COMMENT '医生ID',
    `dept_id`     BIGINT UNSIGNED NOT NULL COMMENT '科室ID（下单时快照，便于按科室统计）',
    `visit_date`  DATE            NOT NULL COMMENT '就诊日期',
    `time_slot`   VARCHAR(20)     NOT NULL COMMENT '就诊时段：上午/下午/08:00-09:00',
    `reg_fee`     DECIMAL(8, 2)   NOT NULL COMMENT '挂号费快照（元）',
    `status`      TINYINT         NOT NULL DEFAULT 0 COMMENT '状态：0待就诊 1已就诊 2已取消 3爽约',
    `remark`      VARCHAR(255)             DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reg_no` (`reg_no`),
    UNIQUE KEY `uk_reg_unique` (`patient_id`, `doctor_id`, `visit_date`, `time_slot`),
    KEY `idx_reg_doctor_date` (`doctor_id`, `visit_date`),
    KEY `idx_reg_dept` (`dept_id`),
    CONSTRAINT `fk_reg_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_reg_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_reg_dept` FOREIGN KEY (`dept_id`) REFERENCES `department` (`id`)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='挂号记录表';

-- ---------------------------------------------------------------------
-- 5. 电子处方表（与挂号记录 1:1，reg_id 唯一）
-- ---------------------------------------------------------------------
CREATE TABLE `prescription`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `presc_no`     VARCHAR(32)     NOT NULL COMMENT '处方编号，业务唯一标识',
    `reg_id`       BIGINT UNSIGNED NOT NULL COMMENT '挂号记录ID（1:1）',
    `patient_id`   BIGINT UNSIGNED NOT NULL COMMENT '患者ID',
    `doctor_id`    BIGINT UNSIGNED NOT NULL COMMENT '开方医生ID',
    `diagnosis`    VARCHAR(500)             DEFAULT NULL COMMENT '诊断结论',
    `total_amount` DECIMAL(10, 2)  NOT NULL DEFAULT 0.00 COMMENT '处方总金额（元）',
    `status`       TINYINT         NOT NULL DEFAULT 0 COMMENT '状态：0待发药 1已发药 2已作废',
    `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开方时间',
    `update_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_presc_no` (`presc_no`),
    UNIQUE KEY `uk_presc_reg` (`reg_id`),
    KEY `idx_presc_patient` (`patient_id`),
    KEY `idx_presc_doctor` (`doctor_id`),
    CONSTRAINT `fk_presc_reg` FOREIGN KEY (`reg_id`) REFERENCES `registration` (`id`)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_presc_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT `fk_presc_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='电子处方表';

-- ---------------------------------------------------------------------
-- 6. 药品表（实验三「药品库存扣减」的支撑表，实验二一并建好，避免后续改表）
-- ---------------------------------------------------------------------
CREATE TABLE `drug`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `drug_code`   VARCHAR(30)     NOT NULL COMMENT '药品编码',
    `drug_name`   VARCHAR(100)    NOT NULL COMMENT '药品名称',
    `spec`        VARCHAR(50)              DEFAULT NULL COMMENT '规格',
    `unit`        VARCHAR(10)              DEFAULT NULL COMMENT '单位',
    `price`       DECIMAL(10, 2)  NOT NULL DEFAULT 0.00 COMMENT '单价（元）',
    `stock`       INT             NOT NULL DEFAULT 0 COMMENT '库存数量',
    `status`      TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1在售 0停用',
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_drug_code` (`drug_code`),
    KEY `idx_drug_name` (`drug_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='药品表';

-- ---------------------------------------------------------------------
-- 7. 处方明细表（一张处方多行药品，1NF 要求不能把药品塞进处方主表的逗号串里）
-- ---------------------------------------------------------------------
CREATE TABLE `prescription_item`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `presc_id`    BIGINT UNSIGNED NOT NULL COMMENT '处方ID',
    `drug_id`     BIGINT UNSIGNED NOT NULL COMMENT '药品ID',
    `quantity`    INT             NOT NULL COMMENT '数量',
    `unit_price`  DECIMAL(10, 2)  NOT NULL COMMENT '单价快照（元）',
    `amount`      DECIMAL(10, 2)  NOT NULL COMMENT '小计金额（元）',
    `usage_desc`  VARCHAR(100)             DEFAULT NULL COMMENT '用法用量',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_presc_drug` (`presc_id`, `drug_id`),
    KEY `idx_item_drug` (`drug_id`),
    CONSTRAINT `fk_item_presc` FOREIGN KEY (`presc_id`) REFERENCES `prescription` (`id`)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `fk_item_drug` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='处方明细表';

-- =====================================================================
-- 初始化演示数据（可删）
-- =====================================================================
INSERT INTO `department` (`dept_code`, `dept_name`, `dept_type`, `location`, `phone`, `description`, `sort_no`)
VALUES ('NK', '内科', '门诊', '门诊楼2层东区', '0571-88001001', '涵盖呼吸、消化、心血管等常见内科疾病诊疗', 1),
       ('WK', '外科', '门诊', '门诊楼3层西区', '0571-88001002', '普外、骨科、泌尿外科门诊', 2),
       ('EK', '儿科', '门诊', '门诊楼1层北区', '0571-88001003', '0-14岁儿童常见病、多发病诊疗', 3),
       ('FK', '妇产科', '门诊', '门诊楼4层', '0571-88001004', '妇科、产科门诊及产前检查', 4),
       ('JZK', '急诊科', '急诊', '急诊楼1层', '0571-88001005', '7×24小时急诊救治', 0);

INSERT INTO `doctor` (`doctor_no`, `doctor_name`, `gender`, `phone`, `id_card`, `dept_id`, `title`, `specialty`, `reg_fee`)
VALUES ('D1001', '张明远', '1', '13800138001', '330102198005121234', 1, '主任医师', '高血压、冠心病诊治', 50.00),
       ('D1002', '李慧敏', '2', '13800138002', '330102198710234567', 1, '主治医师', '慢性胃炎、消化道溃疡', 20.00),
       ('D1003', '王建国', '1', '13800138003', '330102197803158901', 2, '副主任医师', '腹腔镜微创手术', 35.00),
       ('D1004', '陈晓雨', '2', '13800138004', '330102199005172345', 3, '住院医师', '小儿呼吸道感染', 15.00);

INSERT INTO `patient` (`patient_no`, `patient_name`, `gender`, `id_card`, `phone`, `birth_date`, `address`, `emergency_contact`, `emergency_phone`)
VALUES ('P20260001', '赵一鸣', '1', '330106199203084321', '13900139001', '1992-03-08', '杭州市西湖区文一西路 100 号', '赵父', '13900139099'),
       ('P20260002', '孙雅琴', '2', '330106199508221234', '13900139002', '1995-08-22', '杭州市拱墅区莫干山路 20 号', '孙母', '13900139098');

INSERT INTO `drug` (`drug_code`, `drug_name`, `spec`, `unit`, `price`, `stock`)
VALUES ('DR001', '阿莫西林胶囊', '0.25g*24粒', '盒', 18.50, 500),
       ('DR002', '布洛芬缓释胶囊', '0.3g*20粒', '盒', 22.00, 300),
       ('DR003', '氯化钠注射液', '500ml', '瓶', 5.80, 1000);
