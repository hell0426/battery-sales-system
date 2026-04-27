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

    // 🔴 必须注入这两个服务，用来查出品牌名和型号名
    @Autowired
    private IProductModelService productModelService;
    @Autowired
    private IBrandService brandService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderSubmitVo vo) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> saveItems = new ArrayList<>();

        for (OrderSubmitVo.OrderItemVo itemVo : vo.getItems()) {
            // 1. 查库存
            Product product = productService.getById(itemVo.getProductId());
            if (product == null) {
                throw new RuntimeException("商品ID " + itemVo.getProductId() + " 不存在");
            }
            if (product.getStock() < itemVo.getQuantity()) {
                // 修正：原来这里用 getBrand()，现在改成用 ID 提示
                throw new RuntimeException("商品(ID:" + product.getId() + ") 库存不足！");
            }

            // 2. 扣库存
            product.setStock(product.getStock() - itemVo.getQuantity());
            productService.updateById(product);

            // 3. 关键步骤：获取型号名和品牌名做“快照”
            // 既然 product 表没名字了，我们就去型号表和品牌表里拿
            ProductModel pm = productModelService.getById(product.getModelId());
            String fullName = "未知型号";
            if (pm != null) {
                Brand b = brandService.getById(pm.getBrandId());
                fullName = (b != null ? b.getName() : "") + " - " + pm.getName();
            }

            // 4. 准备明细记录
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(fullName); // 使用组合好的完整名字
            orderItem.setQuantity(itemVo.getQuantity());

            // 修正：BigDecimal 的推荐转换方式
            BigDecimal salePrice = BigDecimal.valueOf(itemVo.getPrice());
            orderItem.setPrice(salePrice);
            saveItems.add(orderItem);

            // 5. 累加金额
            BigDecimal itemTotal = salePrice.multiply(new BigDecimal(orderItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 6. 保存订单主表
        Orders orders = new Orders();
        orders.setCustomerId(vo.getCustomerId());
        orders.setUserId(vo.getUserId());
        orders.setTotalAmount(totalAmount);
        orders.setStatus(vo.getStatus());
        this.save(orders);

        // 7. 保存订单明细表
        for (OrderItem item : saveItems) {
            item.setOrderId(orders.getId());
        }
        orderItemService.saveBatch(saveItems);
    }
}