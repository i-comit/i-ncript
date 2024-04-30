/** @type {import('tailwindcss').Config}*/
const config = {
  content: ["./src/**/*.{html,js,svelte,ts}"],
  darkMode: 'selector',
  theme: {
    extend: {
      colors: {
        primary: {
          100: '#111111', //Dark text color
          200: '#dddddd', //Light text color
          300: '#656565', //Dark BG color
          400: '#eeeeee', //Light BG color
          500: '#95c7db', //Default accent color
          600: '#606060', //Dark Modal BG color
          700: '#e1e1e1', //Light Modal BG color
        }
      },
      zIndex: {
        '60': '60',
        '100': '100',
      },
      backgroundImage: {
        'radial-gradient-100': 'radial-gradient(rgb(200, 200, 200) 5%, transparent 0)',
        'radial-gradient-200': 'radial-gradient(rgb(50, 50, 50) 5%, transparent 0)',
      },
    },
  },

  plugins: []
};

module.exports = config;