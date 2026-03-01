package com.zjgs.backend.entity.vo;

import lombok.Data;

@Data
public class OrdersQueryVo {
    private Integer page = 1;
    private Integer size = 10;
    private String status; // 筛选：paid(已结) / debt(挂账)
}