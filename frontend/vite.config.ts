import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',  // 让 Vite 监听所有网络接口
    port: 8080,       // 保持端口为3000
  },
  build: {
    outDir: 'dist' // 构建输出目录
  }
  
})
