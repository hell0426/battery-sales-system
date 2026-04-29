/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : battery_sales

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 28/04/2026 14:46:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for brand
-- ----------------------------
DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '品牌名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of brand
-- ----------------------------
INSERT INTO `brand` VALUES (1, '风帆');
INSERT INTO `brand` VALUES (2, '骆驼');
INSERT INTO `brand` VALUES (3, '瓦尔塔');
INSERT INTO `brand` VALUES (4, '天能');
INSERT INTO `brand` VALUES (5, '超威');

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户名/修理厂名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'individual' COMMENT '类型: individual(散客), shop(修理厂)',
  `discount_rate` decimal(3,2) NULL DEFAULT 1.00 COMMENT '客户折扣率 (1.00=原价, 0.85=85折)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除(1:已删, 0:未删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES (1, '普通散客 (现金)', '无', 'individual', 1.00, '2026-04-24 14:31:51', '2026-04-24 14:31:51', 0);
INSERT INTO `customer` VALUES (2, '京东养车 (府前路店)', '13811112222', 'shop', 0.88, '2026-04-24 14:31:51', '2026-04-24 14:31:51', 0);
INSERT INTO `customer` VALUES (3, '途虎养车中心', '13933334444', 'shop', 0.90, '2026-04-24 14:31:51', '2026-04-24 14:31:51', 0);
INSERT INTO `customer` VALUES (4, '老王私家修理铺', '13555556666', 'shop', 0.95, '2026-04-24 14:31:51', '2026-04-24 14:31:51', 0);
INSERT INTO `customer` VALUES (5, '顺风快修', '13677778888', 'shop', 0.92, '2026-04-24 14:31:51', '2026-04-24 14:31:51', 0);

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` int NOT NULL COMMENT '归属订单ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名快照',
  `quantity` int NULL DEFAULT 1 COMMENT '购买数量',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '成交单价',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 1, '风帆 - 6-QW-45(390)-L', 1, 350.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (2, 2, 5, '瓦尔塔 - AGM-H6', 1, 1200.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (3, 3, 5, '瓦尔塔 - AGM-H6', 2, 1200.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (4, 4, 3, '骆驼 - 6-QW-60(450)-L', 2, 420.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (5, 5, 1, '风帆 - 6-QW-45(390)-L', 2, 350.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (6, 5, 3, '骆驼 - 6-QW-60(450)-L', 1, 420.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (7, 6, 2, '风帆 - 12V-100AH', 1, 680.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (8, 7, 1, '风帆 - 6-QW-45(390)-L', 2, 350.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (9, 8, 4, '瓦尔塔 - L2-400', 2, 550.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (10, 9, 2, '风帆 - 12V-100AH', 3, 700.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (11, 10, 1, '风帆 - 6-QW-45(390)-L', 1, 350.00, '2026-04-24 14:31:51');
INSERT INTO `order_item` VALUES (12, 11, 6, '天能 (Tianneng) - 6-DZM-12', 3, 260.00, '2026-04-24 15:36:16');
INSERT INTO `order_item` VALUES (13, 12, 9, '风帆f - 12V-100AHH', 1, 650.00, '2026-04-24 18:03:19');
INSERT INTO `order_item` VALUES (14, 13, 16, '风帆f - 12V-100AHH', 1, 690.00, '2026-04-27 18:12:26');
INSERT INTO `order_item` VALUES (15, 14, 16, '风帆f - 12V-100AHH', 1, 690.00, '2026-04-27 18:22:01');
INSERT INTO `order_item` VALUES (16, 15, 22, '风帆 - 6-QW-45(390)-L', 2, 335.00, '2026-04-28 13:06:20');
INSERT INTO `order_item` VALUES (17, 16, 15, '风帆 - 6-QW-45(390)-L', 2, 345.00, '2026-04-28 13:17:38');
INSERT INTO `order_item` VALUES (18, 17, 8, '风帆 - 6-QW-45(390)-L', 1, 360.00, '2026-04-28 13:18:41');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` int NOT NULL COMMENT '客户ID',
  `total_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'paid' COMMENT '状态: paid(已付), debt(挂账)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除(1:已删, 0:未删)',
  `user_id` int NULL DEFAULT NULL COMMENT '开单员工ID',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '折扣金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '销售订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 1, 350.00, 'paid', '2026-02-05 14:00:00', '2026-04-27 17:11:20', 0, 1, 0.00);
INSERT INTO `orders` VALUES (2, 2, 1200.00, 'paid', '2026-02-15 09:30:00', '2026-04-27 17:11:20', 0, 1, 0.00);
INSERT INTO `orders` VALUES (3, 3, 2400.00, 'paid', '2026-03-05 10:00:00', '2026-04-27 17:11:20', 0, 1, 0.00);
INSERT INTO `orders` VALUES (4, 4, 840.00, 'debt', '2026-03-12 16:20:00', '2026-04-27 17:11:20', 0, 1, 0.00);
INSERT INTO `orders` VALUES (5, 2, 1500.00, 'paid', '2026-03-25 11:00:00', '2026-04-27 17:11:20', 0, 1, 0.00);
INSERT INTO `orders` VALUES (6, 5, 680.00, 'debt', '2026-04-05 15:00:00', '2026-04-27 17:11:20', 0, 2, 0.00);
INSERT INTO `orders` VALUES (7, 1, 700.00, 'paid', '2026-04-10 13:00:00', '2026-04-27 17:11:20', 0, 2, 0.00);
INSERT INTO `orders` VALUES (8, 3, 1100.00, 'paid', '2026-04-18 09:00:00', '2026-04-27 17:11:20', 0, 2, 0.00);
INSERT INTO `orders` VALUES (9, 2, 2100.00, 'debt', '2026-04-22 14:00:00', '2026-04-27 17:11:20', 0, 2, 0.00);
INSERT INTO `orders` VALUES (10, 1, 350.00, 'paid', '2026-04-24 14:31:51', '2026-04-27 17:11:20', 0, 2, 0.00);
INSERT INTO `orders` VALUES (11, 4, 780.00, 'debt', '2026-04-24 15:36:16', '2026-04-27 17:11:20', 0, 3, 0.00);
INSERT INTO `orders` VALUES (12, 2, 650.00, 'debt', '2026-04-24 18:03:19', '2026-04-27 17:11:20', 0, 3, 0.00);
INSERT INTO `orders` VALUES (14, 3, 640.00, 'debt', '2026-04-27 18:22:01', '2026-04-27 18:22:01', 0, 1, 50.00);
INSERT INTO `orders` VALUES (15, 4, 650.00, 'paid', '2026-04-28 13:06:20', '2026-04-28 13:06:20', 0, 1, 20.00);
INSERT INTO `orders` VALUES (16, 5, 670.00, 'paid', '2026-04-28 13:17:38', '2026-04-28 13:17:38', 0, 2, 20.00);
INSERT INTO `orders` VALUES (17, 2, 340.00, 'paid', '2026-04-28 13:18:41', '2026-04-28 13:18:41', 0, 2, 20.00);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `cost_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '进价',
  `selling_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '售价',
  `stock` int NULL DEFAULT 0 COMMENT '库存数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除(1:已删, 0:未删)',
  `model_id` int NULL DEFAULT NULL COMMENT '关联型号表ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '电瓶商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 210.00, 350.00, 45, '2026-02-01 10:00:00', '2026-04-24 14:31:51', 0, 1);
INSERT INTO `product` VALUES (2, 450.00, 680.00, 5, '2026-02-01 10:00:00', '2026-04-24 14:31:51', 0, 2);
INSERT INTO `product` VALUES (3, 280.00, 420.00, 30, '2026-02-10 10:00:00', '2026-04-24 14:31:51', 0, 3);
INSERT INTO `product` VALUES (4, 320.00, 550.00, 8, '2026-02-15 10:00:00', '2026-04-24 14:31:51', 0, 4);
INSERT INTO `product` VALUES (5, 800.00, 1200.00, 15, '2026-03-01 10:00:00', '2026-04-24 14:31:51', 0, 5);
INSERT INTO `product` VALUES (6, 150.00, 260.00, 97, '2026-03-05 10:00:00', '2026-04-24 14:31:51', 0, 6);
INSERT INTO `product` VALUES (7, 180.00, 310.00, 3, '2026-04-01 10:00:00', '2026-04-24 14:31:51', 0, 7);
INSERT INTO `product` VALUES (8, 220.00, 360.00, 24, '2026-04-24 10:00:00', '2026-04-24 17:20:16', 0, 1);
INSERT INTO `product` VALUES (9, 430.00, 650.00, 14, '2026-04-24 10:10:00', '2026-04-24 17:20:16', 0, 2);
INSERT INTO `product` VALUES (10, 290.00, 410.00, 40, '2026-04-24 10:20:00', '2026-04-24 17:20:16', 0, 3);
INSERT INTO `product` VALUES (11, 330.00, 520.00, 12, '2026-04-24 10:30:00', '2026-04-24 17:20:16', 0, 4);
INSERT INTO `product` VALUES (12, 750.00, 1100.00, 8, '2026-04-24 10:40:00', '2026-04-24 17:20:16', 0, 5);
INSERT INTO `product` VALUES (13, 160.00, 280.00, 90, '2026-04-24 10:50:00', '2026-04-24 17:20:16', 0, 6);
INSERT INTO `product` VALUES (14, 190.00, 320.00, 50, '2026-04-24 11:00:00', '2026-04-24 17:20:16', 0, 7);
INSERT INTO `product` VALUES (15, 215.00, 345.00, 18, '2026-04-24 11:10:00', '2026-04-24 17:20:16', 0, 1);
INSERT INTO `product` VALUES (16, 460.00, 690.00, 8, '2026-04-24 11:20:00', '2026-04-24 17:20:16', 0, 2);
INSERT INTO `product` VALUES (17, 275.00, 405.00, 35, '2026-04-24 11:30:00', '2026-04-24 17:20:16', 0, 3);
INSERT INTO `product` VALUES (18, 315.00, 515.00, 22, '2026-04-24 11:40:00', '2026-04-24 17:20:16', 0, 4);
INSERT INTO `product` VALUES (19, 820.00, 1250.00, 5, '2026-04-24 11:50:00', '2026-04-24 17:20:16', 0, 5);
INSERT INTO `product` VALUES (20, 140.00, 240.00, 100, '2026-04-24 12:00:00', '2026-04-24 17:20:16', 0, 6);
INSERT INTO `product` VALUES (21, 170.00, 290.00, 45, '2026-04-24 12:10:00', '2026-04-24 17:20:16', 0, 7);
INSERT INTO `product` VALUES (22, 205.00, 335.00, 28, '2026-04-24 12:20:00', '2026-04-24 17:20:16', 0, 1);

-- ----------------------------
-- Table structure for product_model
-- ----------------------------
DROP TABLE IF EXISTS `product_model`;
CREATE TABLE `product_model`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand_id` int NOT NULL COMMENT '所属品牌ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '型号名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_model
-- ----------------------------
INSERT INTO `product_model` VALUES (1, 1, '6-QW-45(390)-L');
INSERT INTO `product_model` VALUES (2, 1, '12V-100AH');
INSERT INTO `product_model` VALUES (3, 2, '6-QW-60(450)-L');
INSERT INTO `product_model` VALUES (4, 3, 'L2-400');
INSERT INTO `product_model` VALUES (5, 3, 'AGM-H6');
INSERT INTO `product_model` VALUES (6, 4, '6-DZM-12');
INSERT INTO `product_model` VALUES (7, 5, '6-EVF-45');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'staff' COMMENT '角色',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '超级管理员', '2026-03-01 22:44:23', 'admin');
INSERT INTO `sys_user` VALUES (2, 'xiaozhang', '123456', '店员小张', '2026-04-23 10:06:39', 'staff');
INSERT INTO `sys_user` VALUES (3, 'xiaoli', '123456', '店员小李', '2026-04-23 10:06:39', 'staff');
INSERT INTO `sys_user` VALUES (4, '小丰', '123456', '张三丰', '2026-04-23 12:05:01', 'staff');

SET FOREIGN_KEY_CHECKS = 1;
