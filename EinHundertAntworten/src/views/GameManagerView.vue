<script setup lang="ts">
import { useGameStore } from '@/stores/game';
import NavbarForm from '../components/NavbarForm.vue';
import $ from 'jquery';
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';


onMounted(async () => {
    const response = await fetch('http://localhost:8080/', {
        headers: {
            'Authorization': `Bearer ${token}`
        },
    });
    if (response.status === 401) {
        auth.logout();
    }
});

const auth = useAuthStore();
const game = useGameStore();
const token = auth.token;
const storedQuestions = localStorage.getItem('questions');
const storedAnswers = localStorage.getItem('answers');
const questions = ref<Question[]>(storedQuestions ? JSON.parse(storedQuestions) : []);
const answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);


interface Answer {
  id: string;
  text: String;
  matches: [string];
  filler: [string];
  category: string
}

interface Question {
  id: string;
  text: string;
  match: string;
  category: string
}

function printa() {
  game.getAnswers();
  game.getQuestions();
}

function expand(value: string) {
  console.log(value);
}

function fillAnswers() {
  for (let i = 0; i < answers.value.length; i++) {

    // Create the div element and append it to #answers
    const $answerDiv: HTMLElement = $(`<div class="answer" id="${answers.value[i].id}"><p>${answers.value[i].text}</p></div>`).appendTo('#answers');

    // Create the a tag and append it to the div element
    // $(`<a @click="expand"><i class="fa-solid fa-square-plus"></i></a>`).appendTo($answerDiv);
    $(`<i class="fa-solid fa-square-plus answer-tag"></i>`).appendTo($answerDiv);
  }
}

function addListeners() {
  let elements = document.getElementsByClassName('answer-tag');

  for (let element of elements) {
    element.addEventListener('click', function () {
      fillTable(element.parentElement.id);
    });
  }
}

async function fillTable(id: string) {
  /*
  var domElem = $(elem).get(0);
  var answerID:string = domElem.id;
  */
  console.log("entered");
  var table = $('#tableBody').get(0);
  var answer = answers.value.find(answer => answer.id === id);
  var fillerIDs = [];
  var matchesIDs = [];

  for (let i = 0; i < answer.filler.length; i++) {
    fillerIDs.push(answer.filler[i]);
  }
  for (let i = 0; i < answer.matches.length; i++) {
    matchesIDs.push(answer.matches[i]);
  }
  var filler: Question[] = [];
  var matches: Question[] = [];

  for (let i = 0; i < fillerIDs.length; i++) {
    if (fillerIDs[i] != '') {
      filler.push(await game.getQuestionById(fillerIDs[i]));
    }
  }
  for (let i = 0; i < matchesIDs.length; i++) {
    if (matchesIDs[i] != '') {
      matches.push(await game.getQuestionById(matchesIDs[i]));
    }
  }
  var length;
  if (filler.length > matches.length) {
    length = filler.length;
  } else {
    length = matches.length;
  }
  for (let i = 0; i < length; i++) {
    var row = table.insertRow();
    var cell1 = row.insertCell();
    var cell2 = row.insertCell();
    cell1.innerHTML = matches[i].text;
    cell2.innerHTML = filler[i].text;
  }


}
onMounted(() => {
  printa();
  fillAnswers();
  addListeners();
});

</script>
<template>
  <div>
    <NavbarForm />
    <div class="main">
      <div class="answers-container">
        <h1 class="heading">Answers</h1>
        <div id="answers"></div>
      </div>
      <table>
        <thead>
          <tr>
            <th>Filler</th>
            <th>Matches</th>
          </tr>
        </thead>
        <tbody id="tableBody">
          <tr>
            <td class="fillerRow">Question 1</td>
            <td class="matchesRow">Answer 1</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style>
/* Your styles go here */
.main {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  margin: 0 10%;
}

.answers-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: left;
  margin: 0 10%;
}


.heading {
  font-size: 30px;
  font-weight: bold;
  padding: 10px;
  margin: 10px 0;
  width: 100%;
  border-radius: 5px;
}

.fa-square-plus {
  color: #272727;
  cursor: pointer;
}

.answer {
  display: flex;
  background-color: #f1f1f1;
  border: 2px solid #ddd;
  border-radius: 5px;
  padding: 10px;
  margin: 10px 0;
  font-size: 20px;
  cursor: default;
  max-width: 100%;
}

.table-container {
  display: block;
  /* Change this line */
  width: 100%;
}


table {
  width: 50%;
  border-collapse: collapse;
  margin-top: 10px;
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

th,
td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: center;
  font-size: 16px;
}

th {
  background-color: #007bff;
  color: #fff;
}
</style>
