FROM nginx:stable-alpine
COPY dist /usr/share/nginx/html
EXPOSE 8090
CMD ["nginx", "-g", "daemon off;"]