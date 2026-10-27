-- MySQL dump 10.13  Distrib 8.4.0, for Win64 (x86_64)
--
-- Host: localhost    Database: drug_delivery_v2
-- ------------------------------------------------------
-- Server version	8.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `drug_delivery_v2`
--

/*!40000 DROP DATABASE IF EXISTS `drug_delivery_v2`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `drug_delivery_v2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `drug_delivery_v2`;

--
-- Table structure for table `delivery_task`
--

DROP TABLE IF EXISTS `delivery_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_no` varchar(50) NOT NULL COMMENT '任务编号',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `from_institution_id` bigint NOT NULL COMMENT '发货机构ID',
  `to_institution_id` bigint NOT NULL COMMENT '收货机构ID',
  `delivery_person_id` bigint DEFAULT NULL COMMENT '配送员ID',
  `delivery_person_name` varchar(50) DEFAULT NULL COMMENT '配送员姓名',
  `delivery_phone` varchar(20) DEFAULT NULL COMMENT '配送员电话',
  `status` tinyint DEFAULT '0' COMMENT '状态 0-待配送 1-配送中 2-已送达 3-已完成',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_no` (`task_no`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='配送任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_task`
--

LOCK TABLES `delivery_task` WRITE;
/*!40000 ALTER TABLE `delivery_task` DISABLE KEYS */;
INSERT INTO `delivery_task` VALUES (14,'PS202610140001',1001,1,3,6,'刘大伟','13800010005',3,'2026-10-16 11:00:00','2026-10-16 15:30:00',NULL,'2026-10-16 10:20:00','2026-10-22 11:12:26',0),(15,'PS202610140002',1002,3,5,6,'刘大伟','13800010005',3,'2026-10-16 16:00:00','2026-10-16 18:10:00',NULL,'2026-10-16 15:05:00','2026-10-22 11:12:26',0),(16,'PS202610150003',1003,1,4,6,'刘大伟','13800010005',3,'2026-10-17 12:00:00','2026-10-17 17:20:00',NULL,'2026-10-17 11:30:00','2026-10-22 11:12:26',0),(17,'PS202610150004',1004,3,6,6,'刘大伟','13800010005',3,'2026-10-17 13:00:00','2026-10-17 16:40:00',NULL,'2026-10-17 12:15:00','2026-10-22 11:12:26',0),(18,'PS202610160005',1006,1,3,6,'刘大伟','13800010005',3,'2026-10-18 11:00:00','2026-10-18 15:00:00',NULL,'2026-10-18 10:05:00','2026-10-22 11:12:26',0),(19,'PS202610160006',1007,3,5,6,'刘大伟','13800010005',1,'2026-10-18 15:00:00',NULL,'路上有点堵，晚了半天','2026-10-18 14:50:00','2026-10-22 11:12:26',0),(20,'PS202610170007',1008,1,4,NULL,NULL,NULL,0,NULL,NULL,NULL,'2026-10-19 11:10:00','2026-10-22 11:12:26',0),(21,'PS202610170008',1009,3,6,6,'刘大伟','13800010005',3,'2026-10-19 13:00:00','2026-10-19 16:30:00',NULL,'2026-10-19 12:45:00','2026-10-22 11:12:26',0),(22,'PS202610170009',1010,1,3,6,'刘大伟','13800010005',3,'2026-10-19 18:00:00','2026-10-19 20:15:00',NULL,'2026-10-19 17:20:00','2026-10-22 11:12:26',0),(23,'PS202610180010',1011,4,7,6,'刘大伟','13800010005',1,'2026-10-20 11:00:00',NULL,NULL,'2026-10-20 10:35:00','2026-10-22 11:12:26',0),(24,'PS202610180011',1012,3,5,NULL,NULL,NULL,0,NULL,NULL,NULL,'2026-10-20 15:40:00','2026-10-22 11:12:26',0),(25,'PS202610190012',1014,1,3,NULL,NULL,NULL,0,NULL,NULL,NULL,'2026-10-21 10:50:00','2026-10-22 11:12:26',0),(26,'PS202610200013',1018,1,3,NULL,NULL,NULL,3,'2026-10-22 11:40:39','2026-10-22 11:40:39',NULL,'2026-10-22 11:15:00','2026-10-22 11:12:26',0),(27,'PS202610221141053925',1019,1,5,NULL,'刘大伟','13800010005',3,'2026-10-22 11:41:06','2026-10-22 11:41:06','联调配送','2026-10-22 11:41:06','2026-10-22 11:41:06',0);
/*!40000 ALTER TABLE `delivery_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drug`
--

DROP TABLE IF EXISTS `drug`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `drug_name` varchar(100) NOT NULL COMMENT '药品名称',
  `drug_code` varchar(50) DEFAULT NULL COMMENT '药品编码',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `specification` varchar(200) DEFAULT NULL COMMENT '规格',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '生产厂家',
  `approval_number` varchar(50) DEFAULT NULL COMMENT '批准文号',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `status` tinyint DEFAULT '1' COMMENT '状态 1-在售 0-下架',
  `description` text COMMENT '描述',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `warning_threshold` int DEFAULT '10' COMMENT '预警阈值',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `drug_code` (`drug_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drug`
--

LOCK TABLES `drug` WRITE;
/*!40000 ALTER TABLE `drug` DISABLE KEYS */;
INSERT INTO `drug` VALUES (1,'阿莫西林胶囊','AMX001',4,'0.25g*24粒','华北制药股份有限公司','国药准字H13021770',12.50,'盒',1,NULL,NULL,50,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(2,'头孢拉定胶囊','TBL001',4,'0.25g*24粒','石药集团欧意药业','国药准字H13020787',15.80,'盒',1,NULL,NULL,30,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(3,'布洛芬缓释胶囊','BLF001',5,'0.3g*20粒','中美史克','国药准字H10900089',18.00,'盒',1,NULL,NULL,40,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(4,'对乙酰氨基酚片','DYAJ001',5,'0.5g*12片','拜耳医药','国药准字H11021490',8.50,'盒',1,NULL,NULL,60,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(5,'感冒灵颗粒','GML001',6,'10g*9袋','三九医药','国药准字Z44021940',15.00,'盒',1,NULL,NULL,50,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(6,'复方甘草片','FFGC001',6,'100片','天津药业集团','国药准字H12020802',6.50,'瓶',1,NULL,NULL,80,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(7,'奥美拉唑肠溶胶囊','AML001',7,'20mg*14粒','阿斯利康制药','国药准字H20030945',45.00,'盒',1,NULL,NULL,30,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(8,'多潘立酮片','DPLT001',7,'10mg*30片','西安杨森','国药准字H10910003',22.00,'盒',1,NULL,NULL,40,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(9,'硝苯地平缓释片','XBDP001',8,'10mg*30片','拜耳医药','国药准字H10910056',32.00,'盒',1,NULL,NULL,35,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(10,'阿司匹林肠溶片','ASPL001',8,'100mg*30片','拜耳医药','国药准字J20130078',16.00,'盒',1,NULL,NULL,45,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(11,'连花清瘟胶囊','LHQW001',9,'0.35g*24粒','以岭药业','国药准字Z20040063',14.80,'盒',1,NULL,NULL,100,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(12,'板蓝根颗粒','BLG001',9,'10g*20袋','白云山制药','国药准字Z44020026',12.00,'盒',1,NULL,NULL,80,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(13,'复方丹参片','FFDS001',10,'60片','同仁堂制药','国药准字Z11021185',25.00,'瓶',1,NULL,NULL,50,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(14,'六味地黄丸','LWDH001',10,'9g*10丸','同仁堂制药','国药准字Z11020133',28.00,'盒',1,NULL,NULL,40,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(15,'维生素C片','WSSC001',1,'100mg*100片','东北制药','国药准字H21020701',5.50,'瓶',1,NULL,NULL,100,'2026-10-19 19:17:16','2026-10-19 19:17:16',0);
/*!40000 ALTER TABLE `drug` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drug_category`
--

DROP TABLE IF EXISTS `drug_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_name` varchar(100) NOT NULL COMMENT '分类名称',
  `category_code` varchar(50) DEFAULT NULL COMMENT '分类编码',
  `parent_id` bigint DEFAULT '0' COMMENT '父级分类ID',
  `sort` int DEFAULT '0' COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint DEFAULT '1' COMMENT '状态 1-正常 0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drug_category`
--

LOCK TABLES `drug_category` WRITE;
/*!40000 ALTER TABLE `drug_category` DISABLE KEYS */;
INSERT INTO `drug_category` VALUES (1,'西药','XY',0,1,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(2,'中药','ZY',0,2,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(3,'中成药','ZCY',0,3,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(4,'抗生素','KSS',1,1,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(5,'解热镇痛药','JRZTY',1,2,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(6,'呼吸系统用药','HXXT',1,3,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(7,'消化系统用药','XHXT',1,4,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(8,'心血管系统用药','XXGX',1,5,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(9,'清热解毒','QRJD',3,1,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(10,'补气养血','BQYX',3,2,NULL,1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0);
/*!40000 ALTER TABLE `drug_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drug_order`
--

DROP TABLE IF EXISTS `drug_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `institution_id` bigint NOT NULL COMMENT '申请机构ID',
  `user_id` bigint DEFAULT NULL COMMENT '申请人ID',
  `total_amount` decimal(12,2) DEFAULT '0.00' COMMENT '订单总金额',
  `total_quantity` int DEFAULT '0' COMMENT '总数量',
  `status` tinyint DEFAULT '0' COMMENT '订单状态 0-待审核 1-已审核 2-已驳回 3-配送中 4-已完成 5-已取消',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `approve_by` bigint DEFAULT NULL COMMENT '审核人ID',
  `approve_time` datetime DEFAULT NULL COMMENT '审核时间',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1020 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drug_order`
--

LOCK TABLES `drug_order` WRITE;
/*!40000 ALTER TABLE `drug_order` DISABLE KEYS */;
INSERT INTO `drug_order` VALUES (1001,'DD202610140001',3,3,690.00,50,4,'常规补货',2,'2026-10-16 10:20:00','审核通过','2026-10-16 09:20:00','2026-10-22 11:12:26',0),(1002,'DD202610140002',5,5,550.00,40,4,'村卫生室常用药',2,'2026-10-16 15:05:00','审核通过','2026-10-16 14:05:00','2026-10-22 11:12:26',0),(1003,'DD202610150003',4,4,1760.00,70,4,NULL,2,'2026-10-17 11:30:00','审核通过','2026-10-17 10:30:00','2026-10-22 11:12:26',0),(1004,'DD202610150004',6,5,530.00,40,4,NULL,2,'2026-10-17 12:15:00','审核通过','2026-10-17 11:15:00','2026-10-22 11:12:26',0),(1005,'DD202610150005',7,5,530.00,20,2,NULL,2,'2026-10-17 16:40:00','申请数量超出本月计划，先驳回','2026-10-17 15:40:00','2026-10-22 11:12:26',0),(1006,'DD202610160006',3,3,1340.00,40,4,'慢性病用药补充',2,'2026-10-18 10:05:00','审核通过','2026-10-18 09:05:00','2026-10-22 11:12:26',0),(1007,'DD202610160007',5,5,596.00,40,3,NULL,2,'2026-10-18 14:50:00','审核通过','2026-10-18 13:50:00','2026-10-22 11:12:26',0),(1008,'DD202610170008',4,4,1615.00,120,1,'降温了，感冒发烧的多',2,'2026-10-19 11:10:00','审核通过','2026-10-19 10:10:00','2026-10-22 11:12:26',0),(1009,'DD202610170009',6,5,505.00,45,4,NULL,2,'2026-10-19 12:45:00','审核通过','2026-10-19 11:45:00','2026-10-22 11:12:26',0),(1010,'DD202610170010',3,3,1360.00,55,4,NULL,2,'2026-10-19 17:20:00','审核通过','2026-10-19 16:20:00','2026-10-22 11:12:26',0),(1011,'DD202610180011',7,5,684.00,50,3,NULL,2,'2026-10-20 10:35:00','审核通过','2026-10-20 09:35:00','2026-10-22 11:12:26',0),(1012,'DD202610180012',5,5,470.00,40,1,NULL,2,'2026-10-20 15:40:00','审核通过','2026-10-20 14:40:00','2026-10-22 11:12:26',0),(1013,'DD202610180013',4,4,390.00,15,2,NULL,2,'2026-10-20 18:05:00','这两种药院里库存还够，先不进了','2026-10-20 17:05:00','2026-10-22 11:12:26',0),(1014,'DD202610190014',3,3,974.00,70,1,NULL,2,'2026-10-21 10:50:00','审核通过','2026-10-21 09:50:00','2026-10-22 11:12:26',0),(1015,'DD202610190015',6,5,305.00,50,0,'麻烦尽快审核一下',NULL,NULL,NULL,'2026-10-21 15:30:00','2026-10-22 11:12:26',0),(1016,'DD202610200016',5,5,690.00,50,0,'村里感冒的比较多',NULL,NULL,NULL,'2026-10-22 08:40:00','2026-10-22 11:12:26',0),(1017,'DD202610200017',4,4,755.00,70,0,NULL,NULL,NULL,NULL,'2026-10-22 09:25:00','2026-10-22 11:12:26',0),(1018,'DD202610200018',3,3,1030.00,70,4,NULL,2,'2026-10-22 11:15:00','审核通过','2026-10-22 10:15:00','2026-10-22 11:12:26',0),(1019,'DD202610221140166965',5,5,425.00,30,4,'????????',1,'2026-10-22 11:41:06','联调审核通过','2026-10-22 11:40:17','2026-10-22 11:40:17',0);
/*!40000 ALTER TABLE `drug_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `institution`
--

DROP TABLE IF EXISTS `institution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `institution` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '机构名称',
  `code` varchar(50) DEFAULT NULL COMMENT '机构编码',
  `level` tinyint NOT NULL COMMENT '机构级别 1-县级 2-乡级 3-村级',
  `parent_id` bigint DEFAULT '0' COMMENT '父级机构ID',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint DEFAULT '1' COMMENT '状态 1-正常 0-禁用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='医疗机构表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `institution`
--

LOCK TABLES `institution` WRITE;
/*!40000 ALTER TABLE `institution` DISABLE KEYS */;
INSERT INTO `institution` VALUES (1,'县人民医院','COUNTY001',1,0,'县城中心路1号','张院长','13800138001',1,NULL,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(2,'县中医院','COUNTY002',1,0,'县城健康路2号','李院长','13800138002',1,NULL,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(3,'第一乡卫生院','TOWNSHIP001',2,1,'第一乡政府街','王院长','13800138003',1,NULL,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(4,'第二乡卫生院','TOWNSHIP002',2,1,'第二乡政府街','赵院长','13800138004',1,NULL,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(5,'A村卫生室','VILLAGE001',3,3,'A村','村医A','13800138005',1,NULL,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(6,'B村卫生室','VILLAGE002',3,3,'B村','村医B','13800138006',1,NULL,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(7,'C村卫生室','VILLAGE003',3,4,'C村','村医C','13800138007',1,NULL,'2026-10-19 19:17:16','2026-10-19 19:17:16',0);
/*!40000 ALTER TABLE `institution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `drug_id` bigint NOT NULL COMMENT '药品ID',
  `drug_name` varchar(100) DEFAULT NULL COMMENT '药品名称',
  `specification` varchar(200) DEFAULT NULL COMMENT '规格',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `quantity` int NOT NULL COMMENT '数量',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (64,1001,5,'感冒灵颗粒','10g*9袋','盒',30,15.00,450.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(65,1001,12,'板蓝根颗粒','10g*20袋','盒',20,12.00,240.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(66,1002,11,'连花清瘟胶囊','0.35g*24粒','盒',25,14.80,370.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(67,1002,12,'板蓝根颗粒','10g*20袋','盒',15,12.00,180.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(68,1003,9,'硝苯地平缓释片','10mg*30片','盒',40,32.00,1280.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(69,1003,10,'阿司匹林肠溶片','100mg*30片','盒',30,16.00,480.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(70,1004,3,'布洛芬缓释胶囊','0.3g*20粒','盒',20,18.00,360.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(71,1004,4,'对乙酰氨基酚片','0.5g*12片','盒',20,8.50,170.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(72,1005,13,'复方丹参片','60片','瓶',10,25.00,250.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(73,1005,14,'六味地黄丸','9g*10丸','盒',10,28.00,280.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(74,1006,7,'奥美拉唑肠溶胶囊','20mg*14粒','盒',20,45.00,900.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(75,1006,8,'多潘立酮片','10mg*30片','盒',20,22.00,440.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(76,1007,5,'感冒灵颗粒','10g*9袋','盒',20,15.00,300.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(77,1007,11,'连花清瘟胶囊','0.35g*24粒','盒',20,14.80,296.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(78,1008,3,'布洛芬缓释胶囊','0.3g*20粒','盒',50,18.00,900.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(79,1008,4,'对乙酰氨基酚片','0.5g*12片','盒',40,8.50,340.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(80,1008,1,'阿莫西林胶囊','0.25g*24粒','盒',30,12.50,375.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(81,1009,5,'感冒灵颗粒','10g*9袋','盒',25,15.00,375.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(82,1009,6,'复方甘草片','100片','瓶',20,6.50,130.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(83,1010,9,'硝苯地平缓释片','10mg*30片','盒',30,32.00,960.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(84,1010,10,'阿司匹林肠溶片','100mg*30片','盒',25,16.00,400.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(85,1011,11,'连花清瘟胶囊','0.35g*24粒','盒',30,14.80,444.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(86,1011,12,'板蓝根颗粒','10g*20袋','盒',20,12.00,240.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(87,1012,5,'感冒灵颗粒','10g*9袋','盒',20,15.00,300.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(88,1012,4,'对乙酰氨基酚片','0.5g*12片','盒',20,8.50,170.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(89,1013,13,'复方丹参片','60片','瓶',10,25.00,250.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(90,1013,14,'六味地黄丸','9g*10丸','盒',5,28.00,140.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(91,1014,1,'阿莫西林胶囊','0.25g*24粒','盒',40,12.50,500.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(92,1014,2,'头孢拉定胶囊','0.25g*24粒','盒',30,15.80,474.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(93,1015,6,'复方甘草片','100片','瓶',30,6.50,195.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(94,1015,15,'维生素C片','100mg*100片','瓶',20,5.50,110.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(95,1016,5,'感冒灵颗粒','10g*9袋','盒',30,15.00,450.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(96,1016,12,'板蓝根颗粒','10g*20袋','盒',20,12.00,240.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(97,1017,1,'阿莫西林胶囊','0.25g*24粒','盒',40,12.50,500.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(98,1017,4,'对乙酰氨基酚片','0.5g*12片','盒',30,8.50,255.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(99,1018,3,'布洛芬缓释胶囊','0.3g*20粒','盒',50,18.00,900.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(100,1018,6,'复方甘草片','100片','瓶',20,6.50,130.00,'2026-10-20 11:12:26','2026-10-20 11:12:26',0),(127,1019,1,'阿莫西林胶囊','0.25g*24粒','盒',10,12.50,125.00,'2026-10-22 11:40:17','2026-10-22 11:40:17',0),(128,1019,5,'感冒灵颗粒','10g*9袋','盒',20,15.00,300.00,'2026-10-22 11:40:17','2026-10-22 11:40:17',0);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock`
--

DROP TABLE IF EXISTS `stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `drug_id` bigint NOT NULL COMMENT '药品ID',
  `institution_id` bigint NOT NULL COMMENT '机构ID',
  `quantity` int DEFAULT '0' COMMENT '库存数量',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `batch_number` varchar(50) DEFAULT NULL COMMENT '批号',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_drug_institution` (`drug_id`,`institution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock`
--

LOCK TABLES `stock` WRITE;
/*!40000 ALTER TABLE `stock` DISABLE KEYS */;
INSERT INTO `stock` VALUES (1,1,1,150,12.50,'20240101','2024-01-01','2026-01-01','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(2,2,1,150,15.80,'20240201','2024-02-01','2026-02-01','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(3,3,1,180,18.00,'20240115','2024-01-15','2026-01-15','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(4,4,1,300,8.50,'20240301','2024-03-01','2026-03-01','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(5,5,1,5,15.00,'20240101','2024-01-01','2025-12-01','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(6,6,1,100,6.50,'20240215','2024-02-15','2026-02-15','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(7,7,1,80,45.00,'20240110','2024-01-10','2026-01-10','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(8,8,1,120,22.00,'20240305','2024-03-05','2026-03-05','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(9,9,1,90,32.00,'20240220','2024-02-20','2026-02-20','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(10,10,1,110,16.00,'20240125','2024-01-25','2026-01-25','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(11,11,1,50,14.80,'20240401','2024-04-01','2026-04-01','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(12,12,1,200,12.00,'20240315','2024-03-15','2026-03-15','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(13,13,1,60,25.00,'20240210','2024-02-10','2026-02-10','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(14,14,1,70,28.00,'20240105','2024-01-05','2026-01-05','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(15,15,1,500,5.50,'20240410','2024-04-10','2026-04-10','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(16,1,3,20,12.50,'20240101','2024-01-01','2026-01-01','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(17,3,3,15,18.00,'20240115','2024-01-15','2026-01-15','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(18,5,3,10,15.00,'20240101','2024-01-01','2025-12-01','2026-10-19 19:17:16','2026-10-19 19:17:16',0),(19,1,4,50,NULL,NULL,NULL,NULL,'2026-10-22 11:41:49','2026-10-22 11:41:49',0);
/*!40000 ALTER TABLE `stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_record`
--

DROP TABLE IF EXISTS `stock_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `drug_id` bigint NOT NULL COMMENT '药品ID',
  `institution_id` bigint NOT NULL COMMENT '机构ID',
  `operation_type` tinyint NOT NULL COMMENT '操作类型 1-入库 2-出库 3-调拨',
  `quantity` int NOT NULL COMMENT '操作数量',
  `before_quantity` int DEFAULT NULL COMMENT '操作前数量',
  `after_quantity` int DEFAULT NULL COMMENT '操作后数量',
  `batch_number` varchar(50) DEFAULT NULL COMMENT '批号',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `related_order_no` varchar(50) DEFAULT NULL COMMENT '关联订单号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存操作记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_record`
--

LOCK TABLES `stock_record` WRITE;
/*!40000 ALTER TABLE `stock_record` DISABLE KEYS */;
INSERT INTO `stock_record` VALUES (1,1,1,3,50,200,150,'20240101',1,'admin','调拨出库',NULL,'2026-10-22 11:41:49',0),(2,1,4,3,50,0,50,'20240101',1,'admin','调拨入库',NULL,'2026-10-22 11:41:49',0);
/*!40000 ALTER TABLE `stock_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `status` tinyint DEFAULT '1' COMMENT '状态 1-正常 0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'系统管理员','admin','系统管理员，拥有所有权限',1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(2,'县级管理员','county_admin','县级医疗机构管理员',1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(3,'乡级管理员','township_admin','乡级医疗机构管理员',1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(4,'村级用户','village_user','村级医疗机构用户',1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(5,'配送员','delivery','药品配送员',1,'2026-10-19 19:17:16','2026-10-19 19:17:16',0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role_id` int DEFAULT NULL COMMENT '角色ID',
  `institution_id` bigint DEFAULT NULL COMMENT '所属机构ID',
  `status` tinyint DEFAULT '1' COMMENT '状态 1-正常 0-禁用',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','e10adc3949ba59abbe56e057f20f883e','系统管理员','13800138000',NULL,1,1,1,NULL,'2026-10-19 19:17:16','2026-10-19 19:17:16',0),(2,'pharmacy01','e10adc3949ba59abbe56e057f20f883e','王志强','13800010001',NULL,2,1,1,NULL,'2026-10-20 10:45:26','2026-10-20 10:45:26',0),(3,'township01','e10adc3949ba59abbe56e057f20f883e','李丽华','13800010002',NULL,3,3,1,NULL,'2026-10-20 10:45:26','2026-10-20 10:45:26',0),(4,'township02','e10adc3949ba59abbe56e057f20f883e','赵建军','13800010003',NULL,3,4,1,NULL,'2026-10-20 10:45:26','2026-10-20 10:45:26',0),(5,'village01','e10adc3949ba59abbe56e057f20f883e','张秀英','13800010004',NULL,4,5,1,NULL,'2026-10-20 10:45:26','2026-10-20 10:45:26',0),(6,'delivery01','e10adc3949ba59abbe56e057f20f883e','刘大伟','13800010005',NULL,5,1,1,NULL,'2026-10-20 10:45:26','2026-10-20 10:45:26',0),(12,'111','14e1b600b1fd579f47433b88e8d85291','11','17885623670','',4,1,1,NULL,'2026-10-20 10:45:26','2026-10-20 11:59:17',1);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-22 11:46:32
