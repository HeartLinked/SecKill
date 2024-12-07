FROM node:18-alpine AS build-stage

# 设置工作目录
WORKDIR /app

# 将 package.json 和 package-lock.json 复制到容器中
COPY package*.json ./

RUN npm config set registry https://registry.npmmirror.com

# 安装项目依赖
RUN npm install --verbose


# 将应用代码复制到容器中
COPY . .

# 构建项目
RUN npm run build

# 暴露端口
EXPOSE 3000

# 启动应用
CMD ["npm", "run", "dev"]

