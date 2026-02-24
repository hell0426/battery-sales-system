package com.zjgs.backend.service.impl;

import com.zjgs.backend.entity.Customer;
import com.zjgs.backend.mapper.CustomerMapper;
import com.zjgs.backend.service.ICustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户信息表 服务实现类
 * </p>
 *
 * @author Luke
 * @since 2026-02-24
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

}
