import {
  vitePreprocess
} from "@sveltejs/vite-plugin-svelte";
import sveltePreprocess from 'svelte-preprocess';


const config = {
  preprocess: [
    vitePreprocess({}),
    sveltePreprocess({
      scss: true,
      less: true
    }),
  ],
};

export default config;