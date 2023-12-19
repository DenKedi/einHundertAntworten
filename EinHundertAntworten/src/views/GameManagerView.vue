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

const auth = useAuthStore();
const game = useGameStore();
let answers: Answer[] = [];
let questions: Question[] = [];

game.getAnswers();
game.getQuestions();

async function printa(){
  questions = await game.getQuestions() as Question[];
}


</script>

<template>
  <div>
    <NavbarForm />
    <div class="main">
      <button @click="printa">RELOAD</button>
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
