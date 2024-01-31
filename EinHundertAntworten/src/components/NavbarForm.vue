<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth';

const auth = useAuthStore();

function logout() {
  auth.logout();
}

</script>

<template>
  <nav>
    <RouterLink to="/home" class="title">100 Antworten</RouterLink>
    <div>
      <RouterLink class="navIcon" to="/profile"><i class="fa-regular fa-user"></i></RouterLink>
      <a @click="logout" class="navIcon" href="#"><i class="fa-solid fa-arrow-right-from-bracket"></i></a>
    </div>
  </nav>
</template>
