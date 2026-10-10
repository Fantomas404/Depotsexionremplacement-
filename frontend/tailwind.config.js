/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        opti: {
          // Primary: Lilas doux (Dracula style)
          primary: '#cba6f7',
          primaryHover: '#b4befe',
          primaryLight: '#f5e0dc',
          
          // Accent: Bleu Ciel pastel
          accent: '#89b4fa',
          accentHover: '#74c7ec',
          
          // Secondary: Soft Pink
          secondary: '#f5c2e7',
          secondaryHover: '#f38ba8',

          // Backgrounds and Surfaces (Glassmorphism)
          dark: '#1e1e2e', // Fond violet très sombre
          surface: 'rgba(255, 255, 255, 0.05)',
          surfaceLight: 'rgba(255, 255, 255, 0.1)',
          
          card: 'rgba(255, 255, 255, 0.7)',
          cardDark: 'rgba(24, 24, 27, 0.6)',
          
          // Borders & text
          border: 'rgba(255, 255, 255, 0.2)',
          borderDark: 'rgba(255, 255, 255, 0.1)',
          muted: '#a1a1aa',
          textDark: '#fafafa',
          textLight: '#09090b',
        }
      },
      fontFamily: {
        sans: ['Outfit', 'Inter', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      boxShadow: {
        card: '0 8px 32px 0 rgba(31, 38, 135, 0.15)',
        cardHover: '0 8px 32px 0 rgba(31, 38, 135, 0.3)',
        cardDark: '0 8px 32px 0 rgba(0, 0, 0, 0.5)',
        glass: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
      },
      backdropBlur: {
        glass: '12px',
      }
    },
  },
  plugins: [],
}
