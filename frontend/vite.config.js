import {
  defineConfig
} from 'vite'
import {
  svelte
} from '@sveltejs/vite-plugin-svelte'
import pkg from './package.json'; // Make sure the path to package.json is correct

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [svelte()],
  define: {
    '__APP_VERSION__': JSON.stringify(pkg.version),
    '__APP_NAME__': JSON.stringify(pkg.name),
  },
})