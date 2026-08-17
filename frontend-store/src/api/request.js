// 统一的 axios 实例：所有请求走 /api 前缀（开发时由 vite 代理转发到后端 8080），
// 并自动附带 JWT、在收到 401 时清掉本地 token
import axios from 'axios'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 每次请求自动带上 JWT
service.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

service.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('token')
    }
    return Promise.reject(err)
  }
)

export default service
