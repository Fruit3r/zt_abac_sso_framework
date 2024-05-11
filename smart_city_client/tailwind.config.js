/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        'primary': '#AFC957', 
        'primary-content': 'rgba(175, 201, 87, 0.25)',
        'primary-focus': '#9FB947',
        'secondary': '#809147',
        'secondary-content': 'rgba(128, 145, 71, 0.25)',
        'secondary-focus': '#708137',
        'tertiary': '#4F920C',
        'tertiary-content': 'rgba(79, 146, 12, 0.25)',
        'tertiary-focus': '#3F820C',
        'neutral': '#EBEBEB',
        'neutral-light': '#6b7280',
        'neutral-dark': '#000000' ,
        'error': '#FF0000'
      },
      fontFamily: {
        'poppins': ['Poppins', 'sans-serif'],
        'source-code-pro': ['Source Code Pro', 'sans-serif']
      },
    },
  },
  plugins: [],
}

