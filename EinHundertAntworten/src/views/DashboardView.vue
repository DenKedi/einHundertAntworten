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


window.addEventListener('DOMContentLoaded', function() {
            var mainbody = document.querySelector('.mainbody-blur');
            var rect = mainbody.getBoundingClientRect();
            var windowHeight = window.innerHeight || document.documentElement.clientHeight;
            if (rect.top <= windowHeight && rect.bottom >= 0) {
                mainbody.classList.add('in-viewport');
            }
        });
</script>
<template>
    <NavbarForm />
    <div class="mainbody-wrapper">
        <body class="mainbody">
    <div class="main">
    <h1>Moin {{ data }}! Willkommen bei 100 Antworten!</h1>

    <div class="home-button-container">
        <a href="/quiz" class="home-button">Quiz Mich!</a>
        <!--<a href="/login" class="home-button">Registrieren/Anmelden</a>-->
        <a v-if="auth.role === 'ADMIN'" href="/gameManager" class="home-button">Admin Panel</a>
        </div>
</div>
</body>
    </div>

</template>

<style>



</style>

