// 统一的 axios 实例：
// - 本地：baseURL 为 /api，由 vite 代理或 nginx 转发到后端
// - 部署（如 Railway）：通过环境变量 REACT_APP_API_BASE_URL 指定后端公网地址，例如 https://xxx.railway.app/api
// 并自动附带 JWT、在收到 401 时清掉本地 token
import axios from 'axios'

const service = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || '/api',
  timeout: 10000
})

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
