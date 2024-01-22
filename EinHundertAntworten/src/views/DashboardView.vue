<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';
import NavbarForm from '../components/NavbarForm.vue'
const auth = useAuthStore();
const token = auth.token;
const data = ref('');


onMounted(async () => {
    const response = await fetch('http://localhost:8080/', {
        headers: {
            'Authorization': `Bearer ${token}`
        },
    });
    data.value = await response.text();
    if (response.status === 401) {
        auth.logout();
    }
});


</script>
<template>
    <NavbarForm />
    <div class="main">
    <h1>Moin {{ data }}! Willkommen bei 100 Antworten!</h1>

    <div class="home-button-container">
        <a href="/quiz" class="home-button">Quiz Mich!</a>
        <!--<a href="/login" class="home-button">Registrieren/Anmelden</a>-->
        <a v-if="auth.role === 'ADMIN'" href="/gameManager" class="home-button">Admin Panel</a>
        </div>
</div>
</template>

<style>



</style>

