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

const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  try {
    const { data } = await login(form)
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
