import request from '@/utils/request'

// 登录
export function login(data) {
    return request({
        url: '/sysUser/login',
        method: 'post',
        data
    })
}

// 获取所有用户
export function getUserList() {
    return request({
        url: '/sysUser/list-all',
        method: 'get'
    })
}

// 保存/更新用户
export function saveUser(data) {
    return request({
        url: '/sysUser/save',
        method: 'post',
        data
    })
}

// 删除用户
export function deleteUser(id) {
    return request({
        url: `/sysUser/${id}`,
        method: 'delete'
    })
}