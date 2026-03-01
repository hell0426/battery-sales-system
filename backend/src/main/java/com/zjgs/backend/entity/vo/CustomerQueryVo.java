package com.zjgs.backend.entity.vo;

import lombok.Data;

@Data
public class CustomerQueryVo {
    private Integer page = 1;
    private Integer size = 10;
    private String name;  // 搜客户名
    private String phone; // 搜电话
}