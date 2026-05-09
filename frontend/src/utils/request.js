/**
 * axios 统一请求实例
 *
 * 作用：给所有 API 请求提供统一的配置（baseURL、超时、拦截器）
 * 类比：一个配置好的快递员 —— 所有包裹都自动贴 /api 前缀、自动查收、自动验货
 *
 * 完整请求链路（以前端点击开单为例）：
 * Vue 组件 → api/orders.js submitOrder() → request({ url, method, data })  ← 你在这里
 *   → axios 发 HTTP 请求到 /api/orders/submit
 *   → Vite 代理匹配到 /api 前缀 → 转发到 localhost:8080/orders/submit（去掉 /api）
 *   → Spring Boot OrdersController 接收
 *
 * axios 自动 JSON 处理：
 *   - 发送请求时：自动把 data 对象 JSON.stringify() 序列化成 JSON 字符串
 *   - 收到响应时：自动把响应体 JSON.parse() 反序列化成 JS 对象
 *   - 所以我们的拦截器里 response.data 已经是 JS 对象，可以直接 .code 访问
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建 axios 实例（所有请求都用这个实例，配置统一管理）
const request = axios.create({
    // baseURL: 所有请求 URL 自动加上 /api 前缀
    // 例如 url: '/orders/submit' → 实际发出: /api/orders/submit
    baseURL: '/api',
    timeout: 5000 // 5 秒超时，超时未响应则进入 catch
})

// 2. 请求拦截器 —— 在请求发出前的统一处理
request.interceptors.request.use(
    config => {
        // 设置请求头，告诉后端请求体是 JSON 格式
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
        return config; // 必须 return config，否则请求不会发出
    },
    error => {
        return Promise.reject(error);
    }
)

// 3. 响应拦截器 —— 在收到响应后的统一处理
// 所有后端返回的 RespBean 统一在这里判断成功/失败
request.interceptors.response.use(
    response => {
        // 特殊处理：如果后端返回的是 Excel 文件流（Blob），直接返回不做 JSON 判断
        if (response.data instanceof Blob) {
            return response.data;
        }

        // 此时 axios 已经把 HTTP 响应体 JSON 字符串自动 JSON.parse() 成 JS 对象了
        // 所以 response.data 是 JS 对象，可以直接 .code 访问属性
        let res = response.data; // res = { code: 200, msg: "成功", data: {...} }

        // 判断后端返回的业务状态码
        if (res.code === 200) {
            return res; // 成功 → 把 JS 对象返回给调用者（如 .then((res) => { ... })）
        } else {
            // 失败 → 弹错误提示，返回拒绝的 Promise（调用者进 .catch() ）
            ElMessage.error(res.msg ? res.msg : '系统异常');
            return Promise.reject(res.msg);
        }
    },
    error => {
        // 网络层面的错误（后端没启动、超时、断网等），请求根本没到达后端
        console.log('err' + error)
        ElMessage.error('网络请求失败，请检查后端是否启动');
        return Promise.reject(error)
    }
)

export default request;
