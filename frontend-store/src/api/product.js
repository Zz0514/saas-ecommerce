// 商品相关接口封装
import request from './request'

export function getProducts() {
  return request.get('/products')
}

export function getProduct(id) {
  return request.get(`/products/${id}`)
}
