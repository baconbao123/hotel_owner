// vite.config.ts
import { defineConfig } from "vite";
import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import tsconfigPaths from "vite-tsconfig-paths";
import path from "path";

export default defineConfig({
  server: {
    port: 5174,
  },
  plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./app"),
    },
    extensions: [".js", ".jsx", ".ts", ".tsx"], 
  },
  build: {
    cssCodeSplit: true,
  },
  optimizeDeps: {
    include: ["uuid"],
  },
});
