import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from "axios";
// import axios from "axios";

export interface Product {
  id: number
  name: string
  price: number
  description: string
  stock: number
  images: string[]
  category: string
  brand: string
  sales: number
}

export const useProductStore = defineStore('products', () => {

  const products = ref<Product[]>([])

  // 从后端获取商品数据
  const fetchProducts = async () => {
    try {
      const response = await axios.get('/api/products')
      if (Array.isArray(response.data.data.content)) {
        products.value = response.data.data.content
      } else {
        console.error('Received data.content is not an array', response.data.data.content)
      }
      console.log('Fetched products:', products.value)
    } catch (error) {
      console.error('Failed to fetch products:', error)
    }
  }

  const categories = computed(() => [...new Set(products.value.map(p => p.category))])
  const brands = computed(() => [...new Set(products.value.map(p => p.brand))])

  const getProduct = (id: number) => products.value.find(p => p.id === id)

  const searchProducts = (
      query: string = '',
      filters: {
        category?: string[],
        brand?: string[],
        priceRange?: [number, number],
        minRating?: number
      } = {}
  ) => {
    return products.value.filter(product => {
      const matchesQuery = !query ||
          product.name.toLowerCase().includes(query.toLowerCase()) ||
          product.description.toLowerCase().includes(query.toLowerCase())

      const matchesCategory = !filters.category?.length ||
          filters.category.includes(product.category)

      const matchesBrand = !filters.brand?.length ||
          filters.brand.includes(product.brand)

      const matchesPriceRange = !filters.priceRange ||
          (product.price >= filters.priceRange[0] && product.price <= filters.priceRange[1])

      return matchesQuery && matchesCategory && matchesBrand &&
          matchesPriceRange
    })
  }

  return {
    products,
    fetchProducts,
    categories,
    brands,
    getProduct,
    searchProducts
  }
})