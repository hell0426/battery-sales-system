import request from '@/utils/request'

export function getItemsByOrderId(orderId) {
    return request({
        url: `/orderItem/list/${orderId}`,
        method: 'get'
    })
}