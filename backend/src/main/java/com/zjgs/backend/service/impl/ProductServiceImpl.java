package com.zjgs.backend.service.impl;

import com.zjgs.backend.entity.Product;
import com.zjgs.backend.mapper.ProductMapper;
import com.zjgs.backend.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 电瓶商品表 服务实现类
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
