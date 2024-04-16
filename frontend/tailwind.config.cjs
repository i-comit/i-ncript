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
          500: '#95c7db', //Default accent color
          600: '#707070', //Dark Modal BG color
          700: '#e1e1e1', //Light Modal BG color
        }
      },
      zIndex: {
        '60': '60',
        '100': '100',
      }
    },
  },

  plugins: []
};

module.exports = config;