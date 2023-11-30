<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';
import NavbarForm from '../components/NavbarForm.vue'
const auth = useAuthStore();
const token = auth.token;
const data = ref('');
interface GameObject{
    answer: string;
    questions: string[];
    category: string;
}
async function selectCategoryListener() {
    const options = document.getElementsByClassName('category-option') as HTMLCollectionOf<HTMLElement>;

    for (const elem of options) {
        elem.addEventListener('click', async function (e) {
            await showGameObjects(this.innerHTML, this.innerHTML === 'All');
        });
    }
}

async function showGameObjects(category: string, isAllSelected: boolean) {
    const gameObjects:GameObject = await getGameObjects(category, isAllSelected);
    
    const answerContainer = document.getElementById('answer-container');
    if (answerContainer) {
        answerContainer.innerHTML = '';
        const answerHeading = document.createElement('h2');
        answerHeading.innerText = 'Antwort';
        answerContainer.appendChild(answerHeading);
    }

    const questionContainer = document.getElementById('questions-container');
    if (questionContainer) {
        questionContainer.innerHTML = '';
        const questionHeading = document.createElement('h2');
        questionHeading.innerText = 'Frage(n)';
        questionContainer.appendChild(questionHeading);
    }

    for (const object of gameObjects) {
        for (const question of object.questions) {
            const el = document.createElement('p');
            el.innerText = question;
            const qContainer = document.getElementById('questions-container');
            if (qContainer) qContainer.appendChild(el);
        }

        const answer = document.createElement('p');
        answer.innerText = object.answer;
        if (answerContainer) answerContainer.appendChild(answer);

        for (let i = 0; i < object.questions.length - 1; i++) {
            const empty = document.createElement('p');
            empty.innerText = '';
            empty.classList.add('empty');
            if (answerContainer) answerContainer.appendChild(empty);
        }
    }
}

async function getGameObjects(category: string, isAllSelected: boolean) {
    const url = isAllSelected ? '/game/getAll' : '/game/get?category=' + category;
    let responseData;

    // Use Token from LocalStorage after Login
    await fetch(url, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`, // You need to define 'token' somewhere in your code.
        },
    })
        .then(res => res.json())
        .then(data => {
            responseData = data;
        })
        .catch(error => {
            console.error('Error during fetch operation:', error);
        });

    return responseData;
}
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

