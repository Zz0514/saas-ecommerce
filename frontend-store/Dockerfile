# 多阶段构建：第一阶段用 Node 构建出静态文件 dist，第二阶段用 nginx 托管
FROM node:18-alpine AS build
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# nginx:alpine 会在启动时用环境变量替换模板里的 ${PORT}，生成 default.conf
COPY nginx.conf.template /etc/nginx/templates/default.conf.template
EXPOSE 80
