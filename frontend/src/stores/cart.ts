// // stores/cart.ts
// import { defineStore } from 'pinia'
// import {computed, ref} from 'vue'
// import type { Product } from './products'
//
// export const useCartStore = defineStore('cart', () => {
//     const cartItems = ref<{ product: Product; quantity: number }[]>([])
//
//     // 添加商品到购物车
//     const addToCart = (product: Product, quantity: number = 1) => {
//         const existingItem = cartItems.value.find(item => item.product.id === product.id)
//         if (existingItem) {
//             existingItem.quantity += quantity
//         } else {
//             cartItems.value.push({ product, quantity })
//         }
//     }
//
//     // 从购物车移除商品
//     const removeFromCart = (productId: number) => {
//         cartItems.value = cartItems.value.filter(item => item.product.id !== productId)
//     }
//
//     // 更新商品数量
//     const updateQuantity = (productId: number, quantity: number) => {
//         const item = cartItems.value.find(item => item.product.id === productId)
//         if (item) {
//             item.quantity = quantity
//         }
//     }
//
//     // 计算购物车总价
//     const totalPrice = computed(() => {
//         return cartItems.value.reduce((total, item) => total + item.product.price * item.quantity, 0)
//     })
//
//     // 清空购物车
//     const clearCart = () => {
//         cartItems.value = []
//     }
//
//     return {
//         cartItems,
//         addToCart,
//         removeFromCart,
//         updateQuantity,
//         totalPrice,
//         clearCart
//     }
// })

// stores/cart.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import type { Product } from './products'

export const useCartStore = defineStore('cart', () => {
    const cartItems = ref<{ product: Product; quantity: number }[]>([])

    // 从后端获取购物车数据
    const fetchCart = async () => {
        try {
            const response = await axios.get('/api/cart')
            if (Array.isArray(response.data.data)) {
                cartItems.value = response.data.data
            } else {
                console.error('Received cart data is not an array', response.data.data)
            }
            console.log('Fetched cart items:', cartItems.value)
        } catch (error) {
            console.error('Failed to fetch cart items:', error)
        }
    }

    // 添加商品到购物车（同步到后端）
    const addToCart = async (product: Product, quantity: number = 1) => {
        try {
            const existingItem = cartItems.value.find(item => item.product.id === product.id)
            if (existingItem) {
                // 如果商品已存在，更新数量
                await axios.put(`/api/cart/${product.id}`, { quantity: existingItem.quantity + quantity })
                existingItem.quantity += quantity
            } else {
                // 如果商品不存在，添加到购物车
                await axios.post('/api/cart', { productId: product.id, quantity })
                cartItems.value.push({ product, quantity })
            }
        } catch (error) {
            console.error('Failed to add item to cart:', error)
        }
    }

    // 从购物车移除商品（同步到后端）
    const removeFromCart = async (productId: number) => {
        try {
            await axios.delete(`/api/cart/${productId}`)
            cartItems.value = cartItems.value.filter(item => item.product.id !== productId)
        } catch (error) {
            console.error('Failed to remove item from cart:', error)
        }
    }

    // 更新商品数量（同步到后端）
    const updateQuantity = async (productId: number, quantity: number) => {
        try {
            const item = cartItems.value.find(item => item.product.id === productId)
            if (item) {
                await axios.put(`/api/cart/${productId}`, { quantity })
                item.quantity = quantity
            }
        } catch (error) {
            console.error('Failed to update item quantity:', error)
        }
    }

    // 清空购物车（同步到后端）
    const clearCart = async () => {
        try {
            await axios.delete('/api/cart/clear')
            cartItems.value = []
        } catch (error) {
            console.error('Failed to clear cart:', error)
        }
    }

    // 计算购物车总价
    const totalPrice = computed(() => {
        return cartItems.value.reduce((total, item) => total + item.product.price * item.quantity, 0)
    })

    // 初始化时获取购物车数据
    fetchCart()

    return {
        cartItems,
        fetchCart,
        addToCart,
        removeFromCart,
        updateQuantity,
        totalPrice,
        clearCart
    }
})