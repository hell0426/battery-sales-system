import request from '@/utils/request'

// 提交订单
export function submitOrder(data) {
    return request({
        url: '/orders/submit',
        method: 'post',
        data: data
    })
}



// 2. 查询订单列表
export function getOrderList(data) {
    return request({
        url: '/orders/list',
        method: 'post',
        data: data
    })
}

// 3. 结账
export function settleOrder(id) {
    return request({
        url: `/orders/settle/${id}`,
        method: 'post'
    })
}

// 销售统计查询
export function getSalesStats(data) {
    return request({
        url: '/orders/stats/list',
        method: 'post',
        data
    })
}

//excel
export function exportSalesExcel(data) {
    return request({
        url: '/orderItem/stats/export',
        method: 'post',
        data,
        responseType: 'blob' // 告诉axios这是文件流
    })
}