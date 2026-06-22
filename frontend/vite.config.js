import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import * as path from "path";
// Element Plus按需导入
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

export default defineConfig({
  server: {
    port: 8088,
    host: "0.0.0.0",
    proxy: {
      "^/api": {
        // 代理地址
        target: "http://localhost:8080/api",
        // 是否改变源
        changeOrigin: true,
        // 重写路径
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
  css: {
    //  css预处理
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/styles/element-plus/index.scss" as *;`,
      },
    },
  },
  resolve: {
    // 设置路径别名
    alias: {
      "@": path.join(__dirname, "./src"),
    },
  },
  plugins: [vue()],

  // 👇 关键配置：解决第一次打开卡顿、自动刷新、点不动
  optimizeDeps: {
    // 一次性提前把常用依赖全优化好，不再动态编译
    include: [
      'vue',
      'element-plus',
      'element-plus/es/components/radio/style/index',
      'element-plus/es/components/date-picker/style/index',
      '@wangeditor/editor-for-vue'
    ],
    // 关闭依赖变化监听，杜绝自动刷新
    watch: false,
  },


});
