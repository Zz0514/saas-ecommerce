<template>
  <div class="login">
    <el-card class="box">
      <h2>登录</h2>
      <el-input v-model="form.username" placeholder="用户名" style="margin-bottom: 12px;" />
      <el-input v-model="form.password" type="password" placeholder="密码" style="margin-bottom: 12px;" />
      <el-button type="primary" @click="handleLogin">登录</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

// 表单用 reactive 持有，v-model 双向绑定到输入框
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  try {
    const { data } = await login(form)
    // 登录成功：把后端返回的 JWT 存到 localStorage，后续请求自动带上
    localStorage.setItem('token', data.token)
    ElMessage.success('登录成功')
    location.href = '/'
  } catch (e) {
    ElMessage.error('登录失败')
  }
}
</script>

<style>
.login { display: flex; justify-content: center; padding: 80px; }
.box { width: 320px; }
</style>
