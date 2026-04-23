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

 Date: 23/04/2026 12:26:33
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
INSERT INTO `brand` VALUES (2, '骆驼 (Camel)');
INSERT INTO `brand` VALUES (3, '瓦尔塔 (Varta)');
INSERT INTO `brand` VALUES (4, '天能 (Tianneng)');
INSERT INTO `brand` VALUES (5, '超威 (Chilwee)');

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户名/修理厂名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'individual' COMMENT '类型: individual(散客), shop(修理厂)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除(1:已删, 0:未删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES (1, '京东养车', '123456789', 'shop', '2026-03-01 21:44:01', '2026-03-01 21:44:01', 0);
INSERT INTO `customer` VALUES (2, '途虎养车', '456789123', 'shop', '2026-03-01 21:44:01', '2026-03-01 21:44:01', 0);
INSERT INTO `customer` VALUES (3, '11', '11', 'individual', '2026-04-21 18:07:37', '2026-04-21 18:07:41', 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 2, '骆驼 - 6-QW-45', 1, 300.00, '2026-03-01 22:05:17');
INSERT INTO `order_item` VALUES (2, 2, 3, '风帆 - 6-QW-45', 2, 450.00, '2026-03-01 22:17:51');
INSERT INTO `order_item` VALUES (3, 3, 2, '骆驼 - 6-QW-45', 2, 300.00, '2026-04-21 11:00:20');
INSERT INTO `order_item` VALUES (4, 4, 3, '风帆 - 6-QW-45', 3, 450.00, '2026-04-21 11:20:20');
INSERT INTO `order_item` VALUES (5, 5, 2, '骆驼 - 6-QW-45', 4, 300.00, '2026-04-21 18:07:25');
INSERT INTO `order_item` VALUES (6, 6, 1, '风帆 (Fengfan) - 6-QW-45(390)-L', 3, 200.00, '2026-04-23 12:10:04');

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '销售订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 1, 300.00, 'paid', '2026-03-01 22:05:16', '2026-03-01 22:05:16', 0);
INSERT INTO `orders` VALUES (2, 1, 900.00, 'paid', '2026-03-01 22:17:51', '2026-03-01 22:17:51', 0);
INSERT INTO `orders` VALUES (3, 2, 600.00, 'paid', '2026-04-21 11:00:20', '2026-04-21 11:00:20', 0);
INSERT INTO `orders` VALUES (4, 2, 1350.00, 'paid', '2026-04-21 11:20:20', '2026-04-21 11:20:20', 0);
INSERT INTO `orders` VALUES (5, 2, 1200.00, 'paid', '2026-04-21 18:07:25', '2026-04-21 18:07:25', 0);
INSERT INTO `orders` VALUES (6, 2, 600.00, 'debt', '2026-04-23 12:10:04', '2026-04-23 12:10:04', 0);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `brand` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品牌 (风帆/骆驼)',
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '型号 (6-QW-45)',
  `cost_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '进价',
  `selling_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '售价',
  `stock` int NULL DEFAULT 0 COMMENT '库存数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除(1:已删, 0:未删)',
  `model_id` int NULL DEFAULT NULL COMMENT '关联型号表ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '电瓶商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '风帆 (Fengfan)', '6-QW-45(390)-L', 100.00, 200.00, 17, '2026-04-23 11:50:15', '2026-04-23 11:50:15', 0, NULL);
INSERT INTO `product` VALUES (2, '瓦尔塔 (Varta)', 'L2-400', 300.00, 350.00, 10, '2026-04-23 12:09:43', '2026-04-23 12:09:43', 0, NULL);

-- ----------------------------
-- Table structure for product_model
-- ----------------------------
DROP TABLE IF EXISTS `product_model`;
CREATE TABLE `product_model`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand_id` int NOT NULL COMMENT '所属品牌ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '型号名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_model
-- ----------------------------
INSERT INTO `product_model` VALUES (1, 1, '6-QW-45(390)-L');
INSERT INTO `product_model` VALUES (2, 1, '12V-100AH');
INSERT INTO `product_model` VALUES (3, 2, '6-QW-60(450)-L');
INSERT INTO `product_model` VALUES (4, 2, '12V-36AH');
INSERT INTO `product_model` VALUES (5, 3, 'L2-400');
INSERT INTO `product_model` VALUES (6, 3, 'AGM-H6');

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
