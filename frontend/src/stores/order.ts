// stores/order.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'
import type { Product } from './products'

export interface Order {
    id: number
    items: { product: Product; quantity: number }[]
    totalPrice: number
    createdAt: string
}

export const useOrderStore = defineStore('order', () => {
    const orders = ref<Order[]>([])

    // 从后端获取订单数据
    const fetchOrders = async () => {
        try {
            const response = await axios.get('/api/orders')
            if (Array.isArray(response.data.data.content)) {
                orders.value = response.data.data.content
            } else {
                console.error('Received orders data is not an array', response.data.data)
            }
            console.log('Fetched orders:', orders.value)
        } catch (error) {
            console.error('Failed to fetch orders:', error)
        }
    }

    // 添加订单（同步到后端）
    const addOrder = async (items: { product: Product; quantity: number }[]) => {
        try {
            const response = await axios.post('/api/orders', {
                items: items.map(item => ({
                    productId: item.product.id,
                    quantity: item.quantity,
                })),
            })
            const newOrder = response.data.data
            orders.value.push(newOrder)
        } catch (error) {
            console.error('Failed to add order:', error)
        }
    }

    // 获取所有订单
    const getOrders = () => {
        return orders.value
    }

    return {
        orders,
        fetchOrders,
        addOrder,
        getOrders,
    }
})