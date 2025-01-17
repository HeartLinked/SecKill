<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useCartStore } from '../stores/cart'
import { Swiper, SwiperSlide } from 'swiper/vue'
import 'swiper/css'
import { message } from "ant-design-vue";

// const route = useRoute()
// const productStore = useProductStore()
const cartStore = useCartStore()

// 获取商品信息
const product = ref({
  id: 1,
  name: '秒杀商品示例',
  price: 99.99,
  description: '这是一个秒杀商品示例',
  stock: 3,
  images: [
    'https://picsum.photos/id/1/800/600', // 示例图片
  ],
  category: "seckill",
  sales: 0,
  brand: "apple",
})

// 倒计时相关逻辑
const countdown = ref(0) // 倒计时时间（秒）
const isCountdownOver = ref(false) // 倒计时是否结束

// 模拟秒杀结束时间（假设秒杀结束时间为当前时间 + 10分钟）
const seckillEndTime = ref(new Date().getTime() + 15 * 1000)

// 更新倒计时
const updateCountdown = () => {
  const now = new Date().getTime()
  const remainingTime = seckillEndTime.value - now

  if (remainingTime > 0) {
    countdown.value = Math.floor(remainingTime / 1000)
  } else {
    countdown.value = 0
    isCountdownOver.value = true
    clearInterval(intervalId) // 倒计时结束后清除定时器

    // 如果库存为 0，直接显示秒杀已结束
    if (product.value.stock === 0) {
      isCountdownOver.value = false
    }
  }
}

let intervalId: ReturnType<typeof setInterval>; // 动态获取 setInterval 的返回类型

onMounted(() => {
  intervalId = setInterval(updateCountdown, 1000); // 每秒更新一次倒计时
});

onUnmounted(() => {
  clearInterval(intervalId); // 组件卸载时清除定时器
});


const addToCart = () => {
  console.log('加入购物车按钮被点击') // 调试日志
  if (product.value && product.value.stock > 0) {
    console.log('当前商品:', product.value) // 调试日志

    // 库存减一，已售加一
    product.value.stock -= 1
    product.value.sales += 1

    cartStore.addToCart(product.value)
    message.success('秒杀成功！')

    // 如果库存为 0，禁用按钮并显示秒杀已结束
    if (product.value.stock === 0) {
      isCountdownOver.value = false // 这里可以根据需求调整
    }
  } else {
    console.log('商品不存在或库存不足') // 调试日志
    message.error('秒杀已结束或库存不足')
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

          <!-- 倒计时显示 -->
          <div class="mb-6">
            <span class="font-semibold">秒杀倒计时 :</span>
            <span :class="isCountdownOver ? 'text-green-600' : 'text-red-600'">
              {{ isCountdownOver ? '秒杀已开始' : ` ${Math.floor(countdown / 60)} 分 ${countdown % 60} 秒` }}
            </span>
          </div>

          <!-- 购买按钮 -->
          <button
              @click="addToCart"
              class="w-full bg-indigo-600 text-white px-6 py-3 rounded-md hover:bg-indigo-700 disabled:bg-gray-400"
              :disabled="product.stock === 0 || !isCountdownOver"
          >
            {{ product.stock === 0 ? '秒杀已结束' : (isCountdownOver ? '立即购买' : '秒杀未开始') }}
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