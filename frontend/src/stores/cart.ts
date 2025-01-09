// stores/cart.ts
import { defineStore } from 'pinia'
import {computed, ref} from 'vue'
import type { Product } from './products'

export const useCartStore = defineStore('cart', () => {
    const cartItems = ref<{ product: Product; quantity: number }[]>([])

    // 添加商品到购物车
    const addToCart = (product: Product, quantity: number = 1) => {
        const existingItem = cartItems.value.find(item => item.product.id === product.id)
        if (existingItem) {
            existingItem.quantity += quantity
        } else {
            cartItems.value.push({ product, quantity })
        }
    }

    // 从购物车移除商品
    const removeFromCart = (productId: number) => {
        cartItems.value = cartItems.value.filter(item => item.product.id !== productId)
    }

    // 更新商品数量
    const updateQuantity = (productId: number, quantity: number) => {
        const item = cartItems.value.find(item => item.product.id === productId)
        if (item) {
            item.quantity = quantity
        }
    }

    // 计算购物车总价
    const totalPrice = computed(() => {
        return cartItems.value.reduce((total, item) => total + item.product.price * item.quantity, 0)
    })

    // 清空购物车
    const clearCart = () => {
        cartItems.value = []
    }

    return {
        cartItems,
        addToCart,
        removeFromCart,
        updateQuantity,
        totalPrice,
        clearCart
    }
})