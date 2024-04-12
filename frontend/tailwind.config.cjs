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
        }
      },
    },
  },

  plugins: []
};

module.exports = config;