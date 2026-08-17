<template>
  <div class="products">
    <h2>商品列表</h2>
    <el-row :gutter="20">
      <el-col v-for="p in products" :key="p.id" :span="6" style="margin-bottom: 20px;">
        <el-card>
          <template #header>{{ p.name }}</template>
          <p>价格：¥{{ p.price }}</p>
          <p>库存：{{ p.stock }}</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProducts } from '../api/product'

const products = ref([])

onMounted(async () => {
  const { data } = await getProducts()
  products.value = data
})
</script>

<style>
.products { padding: 20px; }
</style>
