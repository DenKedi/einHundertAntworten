import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
const app = createApp(App)

app.use(createPinia())
app.use(router)

// Import Font Awesome script
const script = document.createElement('script');
script.src = 'https://kit.fontawesome.com/dd8ea2651b.js';
script.crossOrigin = 'anonymous';
document.head.appendChild(script);

// Import Style
const style = document.createElement('link');
style.rel = 'stylesheet';
style.type = 'text/css';
style.href = '/src/assets/css/style.css';
document.head.appendChild(style);

app.mount('#app')
