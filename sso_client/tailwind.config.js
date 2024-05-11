/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        'primary': '#1D5E98', 
        'primary-content': 'rgba(29, 94, 152, 0.1)', // rgba(219, 186, 148, 0.25)
        'primary-focus': '#0D4E88',
        'secondary': '#375563',
        'secondary-content': 'rgba(55, 85, 99, 0.25)',
        'secondary-focus': '#274553',
        'tertiary': '#0C5D81',
        'tertiary-content': 'rgba(12, 93, 129, 0.25)',
        'tertiary-focus': '#0C4D71',
        'neutral': '#EBEBEB',
        'neutral-light': '#6b7280',
        'neutral-dark': '#000000' 
      },
      fontFamily: {
        'poppins': ['Poppins', 'sans-serif'],
        'source-code-pro': ['Source Code Pro', 'sans-serif']
      },
    },
  },
  plugins: [],
}

