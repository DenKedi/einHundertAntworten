<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';
import NavbarForm from '../components/NavbarForm.vue'
const auth = useAuthStore();
const token = auth.token;
const data = ref('');

</script>
<template>
    <NavbarForm />

  <div class="main">
            <a href="/quizpanel" class="home-button">Show Quiz Panel Draft</a>

            <div class="dropdown">
                <button class="dropdown-button">Category</button>
                <div class="dropdown-content">
                    <span class="category-option">All</span>
                    <span class="category-option">Location</span>
                    <span class="category-option">Celebrity</span>
                    <span class="category-option">Number</span>
                </div>
            </div>
            <div class="gameobject-container">
                <div id="answer-container">
                    <h2>Antwort</h2>
                </div>
                <div id="questions-container">
                    <h2>Frage(n)</h2>
                </div>
            </div>
        </div>
</template>

<style>



</style>

