import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import router from './router'
import App from './App.vue'
import axios from 'axios'
import VueLazyload from 'vue3-lazyload'
import 'ant-design-vue/dist/reset.css'
import './style.css'

// 配置 Axios 全局默认后端地址
// 替换为后端地址，例如 http://localhost:3000/api
// axios.defaults.baseURL = 'http://39.99.238.81:8080'
axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.timeout = 30000 // 可选，设置请求超时时间（单位：毫秒）

// // 设置请求拦截器
axios.interceptors.request.use(
    config => {
        // 优先从 sessionStorage 获取 token，因为它通常用于短期会话
        let token = sessionStorage.getItem('authToken');

        // 如果没有找到，尝试从 localStorage 获取
        if (!token) {
            console.log("no token!")
            token = localStorage.getItem('authToken');
            if(!token){
                console.log("no token2!")
            }
        }

        console.log('Token being sent with request:', token);

        // 如果有 token，设置请求头
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }

        return config;
    },
    error => {
        // 请求出错时的处理
        return Promise.reject(error);
    }
);

const app = createApp(App)

// 全局配置 Axios
app.config.globalProperties.$axios = axios

// 配置 vue-lazyload 插件
app.use(VueLazyload, {
    preLoad: 1.3, // 预加载高度（默认 1.3）
    error: '/assets/error.png', // 图片加载失败时的占位图
    loading: '/assets/loading.png', // 加载中的占位图
    attempt: 1, // 尝试加载次数
})
app.use(createPinia())
app.use(router)
app.use(Antd)
app.mount('#app')