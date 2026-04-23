import request from '@/utils/request'

// 获取所有品牌
export function getBrandList() {
    return request({
        url: '/category/brands',
        method: 'get'
    })
}

// 根据品牌ID获取型号
export function getModelsByBrand(brandId) {
    return request({
        url: `/category/models/${brandId}`,
        method: 'get'
    })
}