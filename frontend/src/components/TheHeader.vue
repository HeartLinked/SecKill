<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  ShoppingCartOutlined,
  UserOutlined,
  LogoutOutlined,
  FireOutlined // 引入秒杀图标
} from '@ant-design/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const searchQuery = ref('')

const handleSearch = (value: string) => {
  router.push({
    path: '/products',
    query: { search: value }
  })
}

const gotoCart = () => {
  router.push('/cart')
}

// 跳转到秒杀商品界面
const gotoSeckill = () => {
  router.push('/seckill') // 假设秒杀商品的路由是 /seckill
}
</script>

<template>
  <a-layout-header style="background-color: #e9ecef;" class="shadow-sm px-4">
    <div class="max-w-7xl mx-auto flex items-center justify-between h-16">
      <div class="flex items-center">
        <router-link to="/" class="text-2xl font-bold text-primary">电商秒杀系统</router-link>
      </div>

      <div class="flex items-center space-x-6">
        <a-input-search
            v-model:value="searchQuery"
            placeholder="输入商品关键词搜索商品..."
            class="w-64"
            @search="handleSearch"
        />

        <!-- 秒杀商品按钮 -->
        <a-button type="link" class="align-middle" @click="gotoSeckill">
          <fire-outlined class="text-xl align-middle" />
          <span class="ml-2">秒杀商品</span>
        </a-button>

        <a-badge :count="0">
          <a-button type="link" class="align-middle" @click="gotoCart">
            <shopping-cart-outlined class="text-xl align-middle" />
          </a-button>
        </a-badge>

        <!-- 购买记录按钮 -->
        <router-link to="/orders">
          <a-button type="link" class="align-middle">
            <history-outlined class="text-xl align-middle" />
            <span class="ml-2">购买记录</span>
          </a-button>
        </router-link>

        <template v-if="authStore.isAuthenticated">
          <a-dropdown>
            <a-button type="link">
              <user-outlined class="text-xl" />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="authStore.logout">
                  <logout-outlined />
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>
        <template v-else>
          <router-link to="/login">
            <a-button type="link" class="align-middle">登录</a-button>
          </router-link>
          <router-link to="/register">
            <a-button type="primary" class="align-middle">注册</a-button>
          </router-link>
        </template>
      </div>
    </div>
  </a-layout-header>
</template>