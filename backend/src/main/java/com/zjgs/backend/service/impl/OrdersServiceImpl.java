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

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @Autowired
    private IProductService productService;
    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private IProductModelService productModelService;
    @Autowired
    private IBrandService brandService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderSubmitVo vo) {
        // 1. 初始化商品总原价为 0
        BigDecimal sumOfItems = BigDecimal.ZERO;
        List<OrderItem> saveItems = new ArrayList<>();

        for (OrderSubmitVo.OrderItemVo itemVo : vo.getItems()) {
            // 查产品信息
            Product product = productService.getById(itemVo.getProductId());
            if (product == null) {
                throw new RuntimeException("商品ID " + itemVo.getProductId() + " 不存在");
            }
            // 查库存
            if (product.getStock() < itemVo.getQuantity()) {
                throw new RuntimeException("商品(ID:" + product.getId() + ") 库存不足！");
            }

            // 关键修正：计算这一项的小计并累加到总原价
            BigDecimal price = BigDecimal.valueOf(itemVo.getPrice());
            BigDecimal quantity = new BigDecimal(itemVo.getQuantity());
            BigDecimal itemTotal = price.multiply(quantity); // 单价 * 数量
            sumOfItems = sumOfItems.add(itemTotal); // 累加到总额

            // 扣库存
            product.setStock(product.getStock() - itemVo.getQuantity());
            productService.updateById(product);

            // 获取名字快照
            ProductModel pm = productModelService.getById(product.getModelId());
            String fullName = "未知型号";
            if (pm != null) {
                Brand b = brandService.getById(pm.getBrandId());
                fullName = (b != null ? b.getName() : "") + " - " + pm.getName();
            }

            // 准备明细记录
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(fullName);
            orderItem.setQuantity(itemVo.getQuantity());
            orderItem.setPrice(price);
            saveItems.add(orderItem);
        }

        // 2. 计算折扣和最终实收
        BigDecimal discount = BigDecimal.valueOf(vo.getDiscountAmount() != null ? vo.getDiscountAmount() : 0.0);
        // 实收金额 = 商品原价总和 - 优惠金额
        BigDecimal finalPayAmount = sumOfItems.subtract(discount);

        // 3. 保存订单主表
        Orders orders = new Orders();
        orders.setCustomerId(vo.getCustomerId());
        orders.setUserId(vo.getUserId());
        orders.setTotalAmount(finalPayAmount); // 存入扣除折扣后的【实收金额】
        orders.setDiscountAmount(discount);    // 【折扣金额】也存进去
        orders.setStatus(vo.getStatus());
        this.save(orders);

        // 4. 保存订单明细表
        for (OrderItem item : saveItems) {
            item.setOrderId(orders.getId());
        }
        orderItemService.saveBatch(saveItems);
    }
}