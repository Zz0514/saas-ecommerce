// 路由表：不同路径渲染对应页面；`*` 兜底把未知路径重定向到首页
import { Routes, Route, Navigate } from 'react-router-dom'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import Products from './pages/Products'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Dashboard />} />
      <Route path="/products" element={<Products />} />
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  )
}
