<template>
  <div class="home">
    <h1>欢迎来到商城</h1>
    <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
    <el-button @click="$router.push('/login')">登录</el-button>
  </div>
</template>

<script setup>
// 首页：展示欢迎语，提供「去逛逛」「登录」两个入口（通过 $router 跳转）
</script>

<style>
.home { text-align: center; padding: 60px; }
</style>
