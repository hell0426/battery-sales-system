import request from '@/utils/request'

// 1. 分页查询
export function getProductList(data) {
    return request({
        url: '/product/list',
        method: 'post',
        data: data
    })
}

// 2. 新增
export function addProduct(data) {
    return request({
        url: '/product/add',
        method: 'post',
        data: data
    })
}

// 3. 更新 (之前漏了这个)
export function updateProduct(data) {
    return request({
        url: '/product/update',
        method: 'post',
        data: data
    })
}

// 4. 删除
export function deleteProduct(id) {
    return request({
        url: `/product/delete/${id}`,
        method: 'delete'
    })
}