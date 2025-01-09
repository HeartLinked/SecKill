<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useProductStore } from '../stores/products'
import { useCartStore } from '../stores/cart'
import { Swiper, SwiperSlide } from 'swiper/vue'
import 'swiper/css'
import {message} from "ant-design-vue";

const route = useRoute()
const productStore = useProductStore()
const cartStore = useCartStore()

// 获取商品信息
const product = computed(() => productStore.getProduct(Number(route.params.id)))

// 加入购物车
const addToCart = () => {
  console.log('加入购物车按钮被点击') // 调试日志
  if (product.value) {
    console.log('当前商品:', product.value) // 调试日志
    cartStore.addToCart(product.value)
    message.success('成功加入购物车！')
  } else {
    console.log('商品不存在') // 调试日志
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="max-w-4xl w-full bg-white p-8 rounded-lg shadow-lg">
      <div v-if="product" class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <swiper
              :slides-per-view="1"
              :space-between="50"
              class="rounded-lg overflow-hidden"
          >
            <swiper-slide v-for="(image, index) in product.images" :key="index">
              <img :src="image" :alt="product.name" class="w-full h-auto" />
            </swiper-slide>
          </swiper>
        </div>
        <div>
          <h1 class="text-3xl font-bold mb-4">{{ product.name }}</h1>
          <p class="text-2xl text-indigo-600 mb-4">{{ product.price.toFixed(2) }} 元</p>
          <p class="text-gray-600 mb-6">{{ product.description }}</p>

          <div class="mb-6">
            <span class="font-semibold">库存 :</span>
            <span :class="product.stock > 0 ? 'text-green-600' : 'text-red-600'">
              {{ ` ${product.stock} ` }}
            </span>
          </div>

          <div class="mb-6">
            <span class="font-semibold text-gray-600">已售 :</span>
            <span class="text-gray-600">
              {{ ` ${product.sales} ` }}
            </span>
          </div>

          <button
              @click="addToCart"
              class="w-full bg-indigo-600 text-white px-6 py-3 rounded-md hover:bg-indigo-700 disabled:bg-gray-400"
              :disabled="product.stock === 0"
          >
            加入购物车
          </button>
        </div>
      </div>
      <div v-else class="text-center">
        <p>商品不存在。</p>
      </div>
    </div>
  </div>
  <div>
    <pre>{{ product }}</pre> <!-- 调试：输出 product 的值 -->
  </div>
</template>