// 商品管理接口封装（管理后台使用：含新增、删除）
import request from './request'

export function getProducts() {
  return request.get('/products')
}

export function createProduct(data) {
  return request.post('/products', data)
}

export function updateProduct(id, data) {
  return request.put(`/products/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/products/${id}`)
}
