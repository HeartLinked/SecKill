<script setup lang="ts">
import { onMounted } from 'vue'
import { useCartStore } from '../stores/cart'
import { useProductStore } from '../stores/products'
import ProductCard from '../components/ProductCard.vue'

const cartStore = useCartStore()
const productStore = useProductStore()

onMounted(async () => {
  // 确保商品数据已加载
  await productStore.fetchProducts()
})
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold mb-6">购物车</h1>

    <!-- 购物车为空时的提示 -->
    <div v-if="cartStore.cartItems.length === 0" class="text-center mt-8">
      <p>您的购物车是空的。</p>
    </div>

    <!-- 购物车商品列表 -->
    <div v-else>
      <div v-for="item in cartStore.cartItems" :key="item.product.id" class="mb-6 border-b pb-4">
        <div class="flex items-center">
          <!-- 商品信息 -->
          <div class="flex-1">
            <ProductCard :product="item.product" />
          </div>

          <!-- 商品数量调整 -->
          <div class="ml-4">
            <input
                type="number"
                v-model.number="item.quantity"
                min="1"
                class="w-20 px-2 py-1 border rounded"
                @change="cartStore.updateQuantity(item.product.id, item.quantity)"
            />
          </div>

          <!-- 移除商品按钮 -->
          <button
              @click="cartStore.removeFromCart(item.product.id)"
              class="ml-4 text-red-500 hover:text-red-700"
          >
            移除
          </button>
        </div>
      </div>

      <!-- 购物车总价 -->
      <div class="mt-8 text-right">
        <p class="text-xl font-bold">总价: ¥{{ cartStore.totalPrice.toFixed(2) }}</p>
      </div>

      <!-- 清空购物车按钮 -->
      <div class="mt-8 text-right">
        <button
            @click="cartStore.clearCart"
            class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-700"
        >
          清空购物车
        </button>

        <!-- 购买按钮 -->
        <button
            @click="cartStore.purchase"
            class="ml-4 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-700"
        >
          购买
        </button>
      </div>
    </div>
  </div>
</template>