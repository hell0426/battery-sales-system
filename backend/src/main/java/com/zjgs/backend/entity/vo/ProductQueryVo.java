package com.zjgs.backend.entity.vo;

import lombok.Data;

@Data
public class ProductQueryVo {
    // 当前页码 (默认第1页)
    private Integer page = 1;
    // 每页显示几条 (默认10条)
    private Integer size = 10;
    // 搜索关键词：品牌
    private String brand;
    // 搜索关键词：型号
    private String model;
}
