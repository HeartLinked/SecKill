<script setup lang="ts">
import type { Product } from '../stores/products'
import { ShoppingCartOutlined } from '@ant-design/icons-vue'

defineProps<{
  product: Product
}>()
</script>

<template>
  <a-card hoverable class="h-full">
    <template #cover>
      <img
          v-lazy="product.images[0]"
          :alt="product.name"
          class="h-48 w-full object-cover"
      />
    </template>
    <template #actions>
      <shopping-cart-outlined key="cart" />
<!--      <heart-outlined key="favorite" @click="addToCart(product as Product)"/>-->
    </template>
    <a-card-meta :title="product.name">
      <template #description>
        <div class="space-y-2">
          <div class="text-lg font-semibold text-primary">{{ product.price.toFixed(2) }} 元</div>
          <div class="text-gray-500 text-sm">已售 {{ product.sales }} </div>
        </div>
      </template>
    </a-card-meta>
  </a-card>
</template>
