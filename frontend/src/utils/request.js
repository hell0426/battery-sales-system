import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建 axios 实例
const request = axios.create({
    // 注意：这里的 /api 会被 vite.config.js 里的代理转发到 http://localhost:8080
    baseURL: '/api', 
    timeout: 5000 // 请求超时时间
})

// 2. 请求拦截器 (可以在这里统一加 Token，我们以后再加)
request.interceptors.request.use(
    config => {
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
        return config;
    },
    error => {
        return Promise.reject(error);
    }
)

// 3. 响应拦截器 (统一处理你后端的 RespBean)
request.interceptors.response.use(
    response => {
        // response.data 就是你后端返回的 RespBean: {code: 200, msg: "成功", data: {...}}
        let res = response.data;
        
        // 如果后端返回状态码是 200，说明成功
        if (res.code === 200) {
            return res;
        } else {
            // 如果不是 200，直接用 Element Plus 弹出错误提示
            ElMessage.error(res.msg ? res.msg : '系统异常');
            return Promise.reject(res.msg);
        }
    },
    error => {
        console.log('err' + error)
        ElMessage.error('网络请求失败，请检查后端是否启动');
        return Promise.reject(error)
    }
)

export default request;