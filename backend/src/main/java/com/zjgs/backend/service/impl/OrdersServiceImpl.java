package com.zjgs.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjgs.backend.entity.*;
import com.zjgs.backend.entity.vo.OrderSubmitVo;
import com.zjgs.backend.mapper.OrdersMapper;
import com.zjgs.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单服务实现类
 * <p>
 * 继承 MyBatis-Plus 的 ServiceImpl<OrdersMapper, Orders> 基类，
 * 自动获得 selectById、updateById、page 等通用 CRUD 方法，无需手写 SQL。
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    /**
     * 商品 Service（接口注入）
     * <p>Spring 通过 @Autowired 自动找到 ProductServiceImpl 实例注入。
     * 调用 productService.getById(id) 时，MyBatis-Plus 自动生成 SELECT * FROM product WHERE id=?</p>
     */
    @Autowired
    private IProductService productService;

    @Autowired
    private IOrderItemService orderItemService;

    @Autowired
    private IProductModelService productModelService;

    @Autowired
    private IBrandService brandService;

    /**
     * 客户 Service —— 用于查询客户折扣率 discount_rate
     */
    @Autowired
    private ICustomerService customerService;

    /**
     * 开单核心业务方法（事务保护）
     *
     * <p>完整业务流程（6 步）：</p>
     * <ol>
     *   <li>遍历购物车，逐一校验商品存在性 & 库存充足性</li>
     *   <li>累计商品原价总和，同时扣减库存</li>
     *   <li>拼接商品快照名称（品牌 - 型号），供后续报表使用</li>
     *   <li>查询客户折扣率 customer.discount_rate，计算实收金额</li>
     *   <li>INSERT 订单主表 orders</li>
     *   <li>INSERT 订单明细表 order_item（关联主表 ID）</li>
     * </ol>
     *
     * <p>事务说明：</p>
     * <ul>
     *   <li>@Transactional(rollbackFor = Exception.class) 保证原子性：</li>
     *   <li>扣库存、保存订单主表、保存明细表，三个写操作要么全部成功，要么全部回滚。</li>
     *   <li>例如：A 扣库存成功但 B 库存不足抛异常 → 事务回滚 → A 的库存恢复原样。</li>
     * </ul>
     *
     * <p>价格计算链：</p>
     * <pre>
     *   商品原价总和 sumOfItems（各 item 单价 × 数量累加）
     *     × 客户折扣率 customerDiscountRate（默认 1.0 = 无折扣）
     *     = 实收金额 finalPayAmount（写入 orders.total_amount）
     *
     *   优惠金额 totalDiscount = sumOfItems - finalPayAmount（写入 orders.discount_amount）
     * </pre>
     *
     * @param vo 前端 Sales.vue 提交的 OrderSubmitVo，包含 customerId、userId、status(paid/debt)、items 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderSubmitVo vo) {
        // ========== 第 1 步：初始化 ==========
        // 使用 BigDecimal 而非 double，避免浮点数精度丢失（0.1 + 0.2 ≠ 0.3 的问题）
        BigDecimal sumOfItems = BigDecimal.ZERO; // 商品原价累加器
        List<OrderItem> saveItems = new ArrayList<>(); // 待批量插入的明细记录

        // ========== 第 2 步：遍历购物车，校验 + 扣库存 + 累加金额 ==========
        for (OrderSubmitVo.OrderItemVo itemVo : vo.getItems()) {

            // 2.1 查商品是否存在
            // MyBatis-Plus 的 getById() 底层生成 SQL：SELECT * FROM product WHERE id = ?
            Product product = productService.getById(itemVo.getProductId());
            if (product == null) {
                // 抛异常触发事务回滚，前端收到 "开单失败：商品ID xxx 不存在"
                throw new RuntimeException("商品ID " + itemVo.getProductId() + " 不存在");
            }

            // 2.2 校验库存是否充足
            if (product.getStock() < itemVo.getQuantity()) {
                // 抛异常触发事务回滚，前端收到 "开单失败：商品(ID:x) 库存不足！"
                throw new RuntimeException("商品(ID:" + product.getId() + ") 库存不足！");
            }

            // 2.3 累加这一项的原价小计：单价 × 数量
            BigDecimal price = BigDecimal.valueOf(itemVo.getPrice());
            BigDecimal quantity = new BigDecimal(itemVo.getQuantity());
            BigDecimal itemTotal = price.multiply(quantity); // 如 400 × 2 = 800
            sumOfItems = sumOfItems.add(itemTotal); // 累加到原价总和

            // 2.4 扣减库存（UPDATE product SET stock = stock - ? WHERE id = ?）
            product.setStock(product.getStock() - itemVo.getQuantity());
            productService.updateById(product);

            // 2.5 拼接商品快照名称（品牌 - 型号）
            // 存快照而非存外键，好处：即使后续品牌/型号被修改或删除，历史订单仍能正确显示
            ProductModel pm = productModelService.getById(product.getModelId());
            String fullName = "未知型号";
            if (pm != null) {
                Brand b = brandService.getById(pm.getBrandId());
                fullName = (b != null ? b.getName() : "") + " - " + pm.getName();
            }

            // 2.6 构建明细记录（暂不设 orderId，等主表插入后回填）
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(fullName); // 快照：如 "风帆 - 60Ah"
            orderItem.setQuantity(itemVo.getQuantity());
            orderItem.setPrice(price);
            saveItems.add(orderItem);
        }

        // ========== 第 3 步：查客户折扣率，计算实收金额 ==========
        // customerService.getById() 底层生成 SQL：SELECT * FROM customer WHERE id = ?
        Customer customer = customerService.getById(vo.getCustomerId());
        BigDecimal customerDiscountRate = BigDecimal.ONE; // 默认 1.00 = 原价不打折
        if (customer != null && customer.getDiscountRate() != null) {
            customerDiscountRate = customer.getDiscountRate(); // 如 0.90 表示打 9 折
        }

        // 最终实收 = 商品原价总和 × 客户折扣率（如 800 × 0.9 = 720）
        BigDecimal finalPayAmount = sumOfItems.multiply(customerDiscountRate);

        // 优惠减免金额 = 原价总和 - 实收金额（如 800 - 720 = 80）
        BigDecimal totalDiscount = sumOfItems.subtract(finalPayAmount);

        // ========== 第 4 步：保存订单主表 orders ==========
        Orders orders = new Orders();
        orders.setCustomerId(vo.getCustomerId());       // 客户 ID
        orders.setUserId(vo.getUserId());               // 开单员工 ID
        orders.setTotalAmount(finalPayAmount);           // 实收金额（折后）
        orders.setDiscountAmount(totalDiscount);         // 优惠减免金额
        orders.setStatus(vo.getStatus());               // paid（现结）或 debt（挂账）

        // MyBatis-Plus 的 save() 生成 INSERT INTO orders (...) VALUES (...)
        // 插入后自动回填自增主键 orders.id（靠 @TableId(type = IdType.AUTO)）
        this.save(orders);

        // ========== 第 5 步：保存订单明细表 order_item ==========
        // 把主表的自增 ID 赋给每一条明细，建立关联关系
        for (OrderItem item : saveItems) {
            item.setOrderId(orders.getId()); // 如 order_id = 8
        }
        // 批量插入：INSERT INTO order_item (order_id, product_name, quantity, price) VALUES (8, ...), (8, ...)
        orderItemService.saveBatch(saveItems);

        // 方法正常结束时，Spring 自动提交事务，所有数据库写入同时生效
    }
}