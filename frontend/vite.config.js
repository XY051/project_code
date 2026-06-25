import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import * as path from "path";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

export default defineConfig({
  server: {
    port: 8088,
    host: "0.0.0.0",
    proxy: {
      "^/api": {
        target: "http://localhost:8080/api",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/styles/element-plus/index.scss" as *;`,
      },
    },
  },
  resolve: {
    alias: {
      "@": path.join(__dirname, "./src"),
    },
  },
  plugins: [vue()],
  optimizeDeps: {
    include: [
      "vue",
      "element-plus",
      "element-plus/es/components/radio/style/index",
      "element-plus/es/components/date-picker/style/index",
      "@wangeditor/editor-for-vue"
    ],
    watch: false,
  },
});
