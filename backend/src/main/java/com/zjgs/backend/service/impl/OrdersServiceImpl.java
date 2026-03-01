package com.zjgs.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjgs.backend.entity.OrderItem;
import com.zjgs.backend.entity.Orders;
import com.zjgs.backend.entity.Product;
import com.zjgs.backend.entity.vo.OrderSubmitVo;
import com.zjgs.backend.mapper.OrdersMapper;
import com.zjgs.backend.service.IOrderItemService;
import com.zjgs.backend.service.IOrdersService;
import com.zjgs.backend.service.IProductService;
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

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务：只要报错，全部回滚
    public void createOrder(OrderSubmitVo vo) {
        // 1. 算出总金额 & 准备明细数据
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> saveItems = new ArrayList<>();

        for (OrderSubmitVo.OrderItemVo itemVo : vo.getItems()) {
            // 查库存够不够
            Product product = productService.getById(itemVo.getProductId());
            if (product == null) {
                throw new RuntimeException("商品ID " + itemVo.getProductId() + " 不存在");
            }
            if (product.getStock() < itemVo.getQuantity()) {
                throw new RuntimeException("商品 " + product.getBrand() + " 库存不足！");
            }

            // 扣库存
            product.setStock(product.getStock() - itemVo.getQuantity());
            productService.updateById(product);

            // 准备明细记录
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getBrand() + " - " + product.getModel());
            orderItem.setQuantity(itemVo.getQuantity());
            orderItem.setPrice(new BigDecimal(itemVo.getPrice())); // 使用实际售价
            saveItems.add(orderItem);

            // 累加金额：单价 * 数量
            BigDecimal itemTotal = orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 2. 保存订单主表
        Orders orders = new Orders();
        orders.setCustomerId(vo.getCustomerId());
        orders.setTotalAmount(totalAmount);
        orders.setStatus(vo.getStatus()); // paid 或 debt
        this.save(orders); // 保存后，orders.getId() 就有值了

        // 3. 保存订单明细表
        for (OrderItem item : saveItems) {
            item.setOrderId(orders.getId()); // 设置刚刚生成的主订单ID
        }
        orderItemService.saveBatch(saveItems);
    }
}