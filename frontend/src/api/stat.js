import request from '@/utils/request'

export function getStatData() {
    return request({
        url: '/stat/data',
        method: 'get'
    })
}