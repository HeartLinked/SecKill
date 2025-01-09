<script setup lang="ts">
import { onMounted } from 'vue'
import { useOrderStore } from '../stores/order'
import { useProductStore } from '../stores/products'

const orderStore = useOrderStore()
const productStore = useProductStore()

// 加载订单数据
onMounted(async () => {
  await productStore.fetchProducts() // 确保商品数据已加载
  // 模拟一些订单数据（实际项目中应从后端获取）
  if (orderStore.orders.length === 0) {
    orderStore.addOrder([
      { product: productStore.products[0], quantity: 2 },
      { product: productStore.products[1], quantity: 1 },
    ])
    orderStore.addOrder([
      { product: productStore.products[2], quantity: 3 },
    ])
  }
})
</script>

<template>
  <div class="min-h-screen bg-gray-100 py-8">
    <div class="max-w-7xl mx-auto px-4">
      <h1 class="text-2xl font-bold mb-6">我的购买记录</h1>

      <!-- 订单列表 -->
      <div v-if="orderStore.orders.length > 0">
        <div v-for="order in orderStore.orders" :key="order.id" class="bg-white rounded-lg shadow-md p-6 mb-6">
          <div class="flex justify-between items-center mb-4">
            <p class="text-lg font-semibold">订单号: #{{ order.id }}</p>
            <p class="text-gray-600">下单时间: {{ order.createdAt }}</p>
          </div>

          <!-- 商品列表 -->
          <div v-for="item in order.items" :key="item.product.id" class="border-b pb-4 mb-4">
            <div class="flex items-center">
              <img :src="item.product.images[0]" :alt="item.product.name" class="w-20 h-20 object-cover rounded-lg" />
              <div class="ml-4 flex-1">
                <p class="text-lg font-semibold">{{ item.product.name }}</p>
                <p class="text-gray-600">{{ item.product.description }}</p>
              </div>
              <div class="text-right">
                <p class="text-lg font-semibold">¥{{ (item.product.price * item.quantity).toFixed(2) }}</p>
                <p class="text-gray-600">x{{ item.quantity }}</p>
              </div>
            </div>
          </div>

          <!-- 订单总价 -->
          <div class="flex justify-end">
            <p class="text-xl font-bold">总价: ¥{{ order.totalPrice.toFixed(2) }}</p>
          </div>
        </div>
      </div>

      <!-- 没有订单时的提示 -->
      <div v-else class="text-center py-8">
        <p class="text-gray-600">暂无购买记录。</p>
      </div>
    </div>
  </div>
</template>