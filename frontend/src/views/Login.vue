<style scoped>
/* 全局输入框样式 */
.input-large {
  height: 40px !important; /* 调整输入框高度 */
  padding: 10px 16px !important; /* 增加内边距，增强舒适性 */
  font-size: 16px !important; /* 增大字体，提升可读性 */
}

/* 按钮样式 */
.button-large {
  height: 42px; /* 与输入框保持一致的高度 */
  font-size: 16px;
  padding: 0 16px;
}
</style>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { publicKey } from '../config'
import { message } from 'ant-design-vue'
import axios from 'axios'
import JSEncrypt from 'jsencrypt'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const formState = ref({
  identifier: '', // 用户名、邮箱或手机号
  password: ''
})

// 页面加载时检查是否存储了用户名
onMounted(() => {
  const savedIdentifier = localStorage.getItem('rememberedUsername')
  if (savedIdentifier) {
    formState.value.identifier = savedIdentifier
    rememberMe.value = true // 恢复“记住我”选中状态
  }
})

const handleSubmit = async () => {
  try {
    loading.value = true
    await formRef.value.validate()

    // 使用 JSEncrypt 加密密码
    const encryptor = new JSEncrypt()
    encryptor.setPublicKey(publicKey)
    const encryptedPassword = encryptor.encrypt(formState.value.password)

    if (!encryptedPassword) {
      message.error('密码加密失败，请重试')
      return
    }

    // 登录请求
    const response = await axios.post('/api/login', {
      username: formState.value.identifier,
      password: encryptedPassword
    })

    // 检查 response.data.code 而不是 response.status
    if (response.data.code === 200) {
      const { token, user } = response.data.data

      // 更新 AuthStore
      authStore.user = user
      authStore.isAuthenticated = true

      // 处理“记住我”功能
      if (rememberMe.value) {
        localStorage.setItem('rememberedUsername', formState.value.identifier)
        localStorage.setItem('authToken', token)
      } else {
        localStorage.removeItem('rememberedUsername') // 未选中时移除
      }
      sessionStorage.setItem('authToken', token)

      message.success('登录成功！')
      await router.push('/products')
    } else {
      // 使用后端返回的错误信息
      message.error(response.data.message || '登录失败，请检查您的信息')
    }
  } catch (error) {
      message.error('登录失败：未知错误')
  } finally {
    loading.value = false
  }
}


const rules = {
  identifier: [
    { required: true, message: '请输入用户名/邮箱/手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="max-w-md w-full bg-white p-8 rounded-lg shadow-lg">
      <h1 class="text-3xl font-bold mb-8 text-center">用户登录</h1>
      <a-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          layout="vertical"
          @finish="handleSubmit"
      >
        <!-- 用户名、邮箱或手机号 -->
        <a-form-item label="用户名 / 邮箱 / 手机号" name="identifier">
          <a-input
              v-model:value="formState.identifier"
              class="input-large"
              placeholder="请输入用户名、邮箱或手机号"
          />
        </a-form-item>

        <!-- 密码 -->
        <a-form-item label="密码" name="password">
          <a-input-password
              v-model:value="formState.password"
              class="input-large"
              placeholder="请输入密码"
          />
        </a-form-item>

        <!-- 记住我 -->
        <a-form-item>
          <a-checkbox v-model:checked="rememberMe">记住我</a-checkbox>
        </a-form-item>

        <!-- 登录按钮 -->
        <a-form-item>
          <a-button
              type="primary"
              html-type="submit"
              :loading="loading"
              class="button-large w-full"
          >
            登录
          </a-button>
        </a-form-item>

        <!-- 忘记密码和注册 -->
        <div class="flex justify-between text-sm">
          <router-link to="/forgot-password">忘记密码？</router-link>
          <router-link to="/register">注册账户</router-link>
        </div>
      </a-form>
    </div>
  </div>
</template>
