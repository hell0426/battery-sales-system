import request from '@/utils/request'

// 提交订单
export function submitOrder(data) {
    return request({
        url: '/orders/submit',
        method: 'post',
        data: data
    })
}