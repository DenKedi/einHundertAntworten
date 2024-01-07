<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { useGameStore } from '@/stores/game';
import NavbarForm from '../components/NavbarForm.vue';

interface Answer {
  id: string;
  text: String;
  matches: [string];
  filler: [string];
  //,category: string
}

export interface Question {
  id: string;
  text: string;
  match: string; //category: string
}

const game = useGameStore();
const storedQuestions = localStorage.getItem('questions');
const storedAnswers = localStorage.getItem('answers');

let questions: Question[];
let answers: Answer[];

if (storedQuestions) {
  questions = JSON.parse(storedQuestions);
} else {
  questions = [];
}
if (storedAnswers) {
  answers = JSON.parse(storedAnswers);
} else {
  answers = [];
}

function printa() {
  game.getAnswers();
  game.getQuestions();
  console.log(questions);
  console.log(answers);
}
function reload() {
  
  if (storedQuestions) {
  questions = JSON.parse(storedQuestions);
} else {
  questions = [];
}
if (storedAnswers) {
  answers = JSON.parse(storedAnswers);
} else {
  answers = [];
}
printa();
window.location.reload();
}
</script>

<template>
  <div>
    <NavbarForm />
    <div class="main">
      <button @click="reload">RELOAD</button>
      <h1>Questions</h1>
      <ul>
        <li v-for="question in questions" :key="question.id">{{ question.text }}</li>
      </ul>

      <h1>Answers</h1>
      <ul>
        <li v-for="answer in answers" :key="answer.id">{{ answer.text }}</li>
      </ul>
     

    </div>
  </div>
</template>

<style>
  /* Your styles go here */
</style>
