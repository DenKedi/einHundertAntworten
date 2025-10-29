<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';

const auth = useAuthStore();
const token = auth.token;
const data = ref('');

onMounted(async () => {
  const response = await fetch(`${auth.serverIP}/`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  data.value = await response.text();
});
</script>
<template>
  <div class="main">
    <h1>Moin {{ data }}! Willkommen bei 100 Antworten!</h1>
    <div class="home-button-container">
      <h2 style="text-align: center" v-if="!auth.token">
        Bitte erstelle kurz ein Profil oder melde dich an, <br />damit wir deine
        Erfolge speichern können.
      </h2>
      <a href="/quiz" class="home-button">Quiz Mich!</a>
      <a v-if="!auth.token" href="/login" class="home-button"
        >Registrieren/Anmelden</a
      >
      <a v-if="auth.role === 'ADMIN'" href="/gameManager" class="home-button"
        >Admin Panel</a
      >
    </div>
  </div>
</template>
