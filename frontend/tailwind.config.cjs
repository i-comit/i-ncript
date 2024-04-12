/** @type {import('tailwindcss').Config}*/
const config = {
  content: ["./src/**/*.{html,js,svelte,ts}"],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          100: '#111111',//Dark text color
          200: '#dddddd',//Light text color
          300: '#757575',//Dark BG color
          400: '#eeeeee',//Light BG color
          500: '#95c7db',//accent color
          600: '#EF562F',
          700: '#EB4F27',
          800: '#CC4522',
          900: '#A5371B'
        }
      },
    },
  },

  plugins: []
};

module.exports = config;