/** @type {import('tailwindcss').Config}*/
const config = {
  content: ["./src/**/*.{html,js,svelte,ts}"],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          100: '#111111', //Dark text color
          200: '#dddddd', //Light text color
          300: '#757575', //Dark BG color
          400: '#eeeeee', //Light BG color
          500: '#95c7db', //accent color (light blue)
          600: '#CF33ED', //accent color (purple)
          700: '#E9C456', //accent color (orange)
          800: '#D43820', //accent color (maroon red)
          900: '#31A51C', //accent color (forest green)
          1000: '#F8C9F4' //accent color (pink)
        }
      },
    },
  },

  plugins: []
};

module.exports = config;