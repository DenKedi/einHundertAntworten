<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router';
import { useAuthStore } from './stores/auth';

const auth = useAuthStore();

function logout() {
  auth.logout();
}
</script>

<template>
    <body>
      
    <main class="p-8 w-1/2">
      <RouterView />
    </main>
    </body>
    
</template>
