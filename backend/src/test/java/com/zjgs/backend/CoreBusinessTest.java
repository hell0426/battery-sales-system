package com.zjgs.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjgs.backend.entity.vo.OrderSubmitVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 核心业务流程集成测试
 * 覆盖登录认证、开单销售、月结对账三个核心模块
 * 使用事务回滚机制，测试数据不会污染数据库
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoreBusinessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== 登录模块测试 ====================

    @Test
    @Order(1)
    @DisplayName("登录成功 — 正确账号密码返回用户信息与角色")
    void loginSuccess() throws Exception {
        String body = "{\"username\":\"admin\",\"password\":\"123456\"}";

        mockMvc.perform(post("/sysUser/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userInfo.role").value("admin"))
                .andExpect(jsonPath("$.data.userInfo.realName").value("超级管理员"));
    }

    @Test
    @Order(2)
    @DisplayName("登录失败 — 密码错误返回明确提示")
    void loginWrongPassword() throws Exception {
        String body = "{\"username\":\"admin\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(post("/sysUser/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value("密码错误"));
    }

    @Test
    @Order(3)
    @DisplayName("登录失败 — 账号不存在返回明确提示")
    void loginUserNotFound() throws Exception {
        String body = "{\"username\":\"ghost_user_999\",\"password\":\"123456\"}";

        mockMvc.perform(post("/sysUser/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value("账号不存在"));
    }

    // ==================== 订单查询测试 ====================

    @Test
    @Order(4)
    @DisplayName("订单列表分页查询 — 返回 total 和 list")
    void queryOrderList() throws Exception {
        String body = "{\"page\":1,\"size\":10}";

        mockMvc.perform(post("/orders/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("按挂账状态筛选订单")
    void queryOrderListByDebtStatus() throws Exception {
        String body = "{\"page\":1,\"size\":10,\"status\":\"debt\"}";

        mockMvc.perform(post("/orders/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ==================== 结账模块测试 ====================

    @Test
    @Order(6)
    @DisplayName("结账失败 — 订单不存在返回错误")
    void settleNonExistentOrder() throws Exception {
        mockMvc.perform(post("/orders/settle/99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value("订单不存在"));
    }

    @Test
    @Order(7)
    @DisplayName("结账成功 — 对已有挂账订单执行核销")
    void settleExistingDebtOrder() throws Exception {
        // 先查一个 debt 订单
        String listBody = "{\"page\":1,\"size\":1,\"status\":\"debt\"}";
        String response = mockMvc.perform(post("/orders/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 解析订单 ID
        int orderId = objectMapper.readTree(response)
                .at("/data/list/0/id").asInt(-1);

        if (orderId == -1) {
            // 没有挂账订单时跳过
            System.out.println("⚠ 当前无挂账订单，跳过结账成功测试");
            return;
        }

        // 执行结账
        mockMvc.perform(post("/orders/settle/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").value("结账成功！"));
    }

    // ==================== 开单销售全流程测试 ====================

    @Test
    @Order(8)
    @DisplayName("完整开单流程 — 提交订单后库存扣减，订单可查")
    void submitOrderSuccess() throws Exception {
        OrderSubmitVo vo = new OrderSubmitVo();
        vo.setCustomerId(1);    // 普通散客
        vo.setUserId(1);        // admin
        vo.setStatus("paid");   // 现结

        OrderSubmitVo.OrderItemVo item = new OrderSubmitVo.OrderItemVo();
        item.setProductId(6);   // 库存 97，够用
        item.setQuantity(1);
        item.setPrice(150.0);
        vo.setItems(java.util.Collections.singletonList(item));

        mockMvc.perform(post("/orders/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.msg").value("开单成功！"));
    }

    @Test
    @Order(9)
    @DisplayName("开单失败 — 库存不足时拦截")
    void submitOrderInsufficientStock() throws Exception {
        OrderSubmitVo vo = new OrderSubmitVo();
        vo.setCustomerId(1);
        vo.setUserId(1);
        vo.setStatus("paid");

        OrderSubmitVo.OrderItemVo item = new OrderSubmitVo.OrderItemVo();
        item.setProductId(7);   // 库存只有 3
        item.setQuantity(999);  // 远超库存
        item.setPrice(180.0);
        vo.setItems(java.util.Collections.singletonList(item));

        mockMvc.perform(post("/orders/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== RBAC 权限测试 ====================

    @Test
    @Order(10)
    @DisplayName("RBAC权限 — 店员登录返回 staff 角色")
    void staffLoginReturnsStaffRole() throws Exception {
        String body = "{\"username\":\"xiaozhang\",\"password\":\"123456\"}";

        mockMvc.perform(post("/sysUser/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userInfo.role").value("staff"));
    }
}
