// stores/order.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Product } from './products'

export interface Order {
    id: number
    items: { product: Product; quantity: number }[]
    totalPrice: number
    createdAt: string
}

export const useOrderStore = defineStore('order', () => {
    const orders = ref<Order[]>([])

    // 添加订单（模拟购买）
    const addOrder = (items: { product: Product; quantity: number }[]) => {
        const newOrder: Order = {
            id: orders.value.length + 1,
            items,
            totalPrice: items.reduce((total, item) => total + item.product.price * item.quantity, 0),
            createdAt: new Date().toLocaleString(),
        }
        orders.value.push(newOrder)
    }

    // 获取所有订单
    const getOrders = () => {
        return orders.value
    }

    return {
        orders,
        addOrder,
        getOrders,
    }
})