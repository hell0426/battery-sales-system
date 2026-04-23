import request from '@/utils/request'

// --- 品牌管理 ---
// 1. 获取所有品牌
export function getBrandList() {
    return request({
        url: '/category/brands',
        method: 'get'
    })
}

// 2. 保存/修改品牌 (假设后端对应 BrandController)
export function saveBrand(data) {
    return request({
        url: '/brand/save',
        method: 'post',
        data
    })
}

// 3. 删除品牌
export function deleteBrand(id) {
    return request({
        url: `/brand/${id}`,
        method: 'delete'
    })
}

// --- 型号管理 ---
// 4. 获取所有型号
export function getModels() {
    return request({
        url: '/productModel/list',
        method: 'get'
    })
}

// 5. 根据品牌ID获取型号 (库存页联动用)
export function getModelsByBrand(brandId) {
    return request({
        url: `/category/models/${brandId}`,
        method: 'get'
    })
}

// 6. 保存/修改型号
export function saveModel(data) {
    return request({
        url: '/productModel/save',
        method: 'post',
        data
    })
}

// 7. 删除型号
export function deleteModel(id) {
    return request({
        url: `/productModel/${id}`,
        method: 'delete'
    })
}