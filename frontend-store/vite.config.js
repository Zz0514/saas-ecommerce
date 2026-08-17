import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 开发时通过代理把 /api 转发到后端（:8080），避免跨域
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
