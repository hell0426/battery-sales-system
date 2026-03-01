import request from '@/utils/request'

export function getCustomerList(data) {
    return request({ url: '/customer/list', method: 'post', data })
}

export function addCustomer(data) {
    return request({ url: '/customer/add', method: 'post', data })
}

export function updateCustomer(data) {
    return request({ url: '/customer/update', method: 'post', data })
}

export function deleteCustomer(id) {
    return request({ url: `/customer/delete/${id}`, method: 'delete' })
}