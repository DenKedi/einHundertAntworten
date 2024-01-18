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
  if (response.status === 200) {
    game.getQuestions();
    game.getAnswers();
  }
});

const auth = useAuthStore();
const game = useGameStore();
const token = auth.token;
var storedQuestions = localStorage.getItem('questions');
var storedAnswers = localStorage.getItem('answers');
var questions = ref<Question[]>(storedQuestions ? JSON.parse(storedQuestions) : []);
var answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);


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

function fillAnswers() {
  for (let i = 0; i < answers.value.length; i++) {

    // Create the div element and append it to #answers
    const $answerDiv: HTMLElement = $(`<div class="answer" id="${answers.value[i].id}"><p>${answers.value[i].text}</p></div>`).appendTo('#answers');

    // Create the a tag and append it to the div element
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

async function addQuestion() {
  let question = (document.getElementById('question-input') as HTMLInputElement).value;
  let category = (document.getElementById('question-category') as HTMLSelectElement).value;

  if (question != "") {
    await game.addQuestion(question, category);
    game.getQuestions();
  }
}

async function addAnswer() {
  let answer = (document.getElementById('answer-input') as HTMLInputElement).value;
  let category = (document.getElementById('answer-category') as HTMLSelectElement).value;

  if (answer != "") {
    await game.addAnswer(answer, category);
    await game.getAnswers();
    fillAnswers();
  }
}

function addButtonListener() {
  let element = document.getElementsByClassName('add-question')[0];
  element.addEventListener('click', function () {
    document.getElementsByClassName('add-form')[0].classList.toggle('hide');
  });

  let questionButton = document.getElementsByClassName('add-question-button')[0];
  questionButton.addEventListener('click', function () {
    addQuestion();
  });

  let answerButton = document.getElementsByClassName('add-answer-button')[0];
  answerButton.addEventListener('click', function () {
    addAnswer();
  });
}

async function fillTable(id: string) {
  /*
  var domElem = $(elem).get(0);
  var answerID:string = domElem.id;
  */
  console.log("entered");
  var table = $('#tableBody').get(0);
  table.innerHTML = '<tr></tr>';
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
    console.log(matches);
    var row = table.insertRow();
    var cell1 = row.insertCell();
    var cell2 = row.insertCell();
    if (matches[i] != undefined) {
      cell1.innerHTML = matches[i].text;
    }
    if (filler[i] != undefined) {
      cell2.innerHTML = filler[i].text;
    }
  }
}

onMounted(() => {
  printa();
  fillAnswers();
  addListeners();
  addButtonListener();
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
            <th>Matches</th>
            <th>Filler</th>
          </tr>
        </thead>
        <tbody id="tableBody">
          <tr></tr>
        </tbody>
      </table>

      <div class="add-gameobjects">
        <div class="add-button">
          <button class="add-question">
            Fragenset hinzufügen
          </button>
        </div>

        <form class="add-form hide">
          <div class="form-container">
            <label class="heading" for="answer-input">Neue Antwort</label>
            <label class="category-label" for="answer-category">Kategorie</label>
            <select name="answer-category" id="answer-category">
              <option value="person">Personen</option>
              <option value="number">Zahlen</option>
              <option value="place">Orte</option>
            </select>

            <input type="text" id="answer-input" name="answer-input" required>
            <button type='button' class="add-answer-button">hinzufügen</button>
          </div>
          <div class="form-container">
            <label class="heading" for="question-inut">Neue Frage</label>
            <label class="category-label" for="question-category">Kategorie</label>
            <select name="question-category" id="question-category">
              <option value="person">Personen</option>
              <option value="number">Zahlen</option>
              <option value="place">Orte</option>
            </select>
            <input type="text" id="question-input" name="question-input">
            <button type='button' class="add-question-button">hinzufügen</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style>
.main {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  margin: 0 10%;
}

.add-gameobjects {
  width: 100%;
  margin-top: 10%;
  margin-bottom: 20%;

  .add-button {
    text-align: center;
  }

  .hide {
    display: none !important;
  }

  .add-form {
    display: flex;
    font-size: .5rem;
    justify-content: space-between;

    .form-container {
      .heading {
        margin: 10% 0 0;
      }

      .category-label {
        margin: 3% 0;
      }

      input {
        margin-top: 5%;
        width: 80%;
      }

      width: 50%;
      display: flex;
      flex-direction: column;
      align-items: center;
    }
  }
}

.answers-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;
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
