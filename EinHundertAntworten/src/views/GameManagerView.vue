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
let storedQuestions = localStorage.getItem('questions');
let storedAnswers = localStorage.getItem('answers');
let questions = ref<Question[]>(storedQuestions ? JSON.parse(storedQuestions) : []);
let answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);

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

function fillAnswers() {
  for (let i = 0; i < answers.value.length; i++) {

    // Create the div element and append it to #answers
    const $answerDiv: HTMLElement = $(`<div class="answer" id="${answers.value[i].id}"><p>${answers.value[i].text}</p></div>`).appendTo('#answers');

    // Create the a tag and append it to the div element
    $(`<i class="fa-solid fa-square-plus answer-tag"></i>`).appendTo($answerDiv);
  }

  let elements = document.getElementsByClassName('answer-tag');
  for (let element of elements) {
    element.addEventListener('click', function () {
      fillTable(element.parentElement.id);
    });
  }
}

async function addQuizSet() {
  const questionValue = (document.getElementById('question-input') as HTMLInputElement);
  const answerValue = (document.getElementById('answer-input') as HTMLInputElement);
  const category = (document.getElementById('category') as HTMLSelectElement).value;
  const messageElement = (document.getElementsByClassName('message'))[0] as HTMLParagraphElement;
  messageElement.classList.remove('success');

  if (questionValue.value == '' || answerValue.value == '') {
    messageElement.innerText = 'Bitte füllen Sie alle Felder aus.'
    return;
  }

  let hasAnswer = false;
  let hasQuestion = false;
  var answerFound: Answer;
  var newAnswer: Answer;

  answers.value.forEach(function (answer) {
    if (answerValue.value == answer.text || answerValue.value.toLocaleLowerCase() == answer.text.toLocaleLowerCase()) {
      hasAnswer = true;
      answerFound = answer;
      return;
    }
  });

  questions.value.forEach(function (question) {
    if (questionValue.value == question.text ||
     questionValue.value == question.text + '?' ||
     questionValue.value == question.text + ' ?' ||
     questionValue.value.toLocaleLowerCase() == question.text.toLocaleLowerCase() ||
     questionValue.value.toLocaleLowerCase() == question.text.toLocaleLowerCase() + '?' ||
     questionValue.value.toLocaleLowerCase() == question.text.toLocaleLowerCase() + ' ?' || questionValue.value.toLocaleLowerCase() + '?' == question.text.toLocaleLowerCase()) {
      hasQuestion = true;
      return;
    }
  });

  if (hasAnswer){
    messageElement.innerText = 'Eine Antwort kann viele Fragen haben ;)';
  }
  if (hasQuestion) {
    messageElement.innerText = 'Die Frage existiert bereits.';
    return;
  }
  if (!hasAnswer) {
    newAnswer = await game.addAnswer(answerValue.value, category);
  }
  const question = await game.addQuestion(questionValue.value, category);
  
  const matchesIds: string[] = [];
  matchesIds.push(question.id);

  if (hasAnswer){
    await game.addMatchToQuestion(question.id, answerFound.id);
    await game.addMatchesToAnswer(answerFound.id, matchesIds, []);
  }else{
    await game.addMatchesToAnswer(newAnswer.id, matchesIds, []);
    await game.addMatchToQuestion(question.id, newAnswer.id);
  }
  
  if (!messageElement.classList.contains('success')) {
    messageElement.classList.add('success');
  }

  messageElement.innerText = 'Quizset erfolgreich angelegt.';
  questionValue.value = '';
  answerValue.value = '';

  await clearAnswerContainer();
  await getGameobjects();
  fillAnswers();
}

async function clearAnswerContainer() {
  let answerContainer = document.getElementById('answers');
  answerContainer.innerHTML = '';
}

function addButtonListener() {
  let element = document.getElementsByClassName('add-question')[0] as HTMLButtonElement;
  element.addEventListener('click', function () {
    let form = document.getElementsByClassName('add-form')[0];
    form.classList.toggle('hide');

    if (form.classList.contains('hide')) {
      element.innerText = "Fragenset hinzufügen";
    } else {
      element.innerText = "Formular verstecken";
    }
  });

  let addQuizSetButton = document.getElementsByClassName('add-quizset-button')[0];
  addQuizSetButton.addEventListener('click', function () {
    addQuizSet();
  });
}

async function getGameobjects() {
  await game.getQuestions();
  await game.getAnswers();
  storedQuestions = localStorage.getItem('questions');
  storedAnswers = localStorage.getItem('answers');
  questions = ref<Question[]>(storedQuestions ? JSON.parse(storedQuestions) : []);
  answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);
}

async function fillTable(id: string) {
  let table = $('#tableBody').get(0);
  table.innerHTML = '<tr></tr>';
  let answer = answers.value.find(answer => answer.id === id);
  let fillerIDs = [];
  let matchesIDs = [];

  for (let i = 0; i < answer.filler.length; i++) {
    fillerIDs.push(answer.filler[i]);
  }
  for (let i = 0; i < answer.matches.length; i++) {
    matchesIDs.push(answer.matches[i]);
  }
  let filler: Question[] = [];
  let matches: Question[] = [];

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
  let length;
  if (filler.length > matches.length) {
    length = filler.length;
  } else {
    length = matches.length;
  }
  for (let i = 0; i < length; i++) {
    console.log(matches);
    let row = table.insertRow();
    let cell1 = row.insertCell();
    let cell2 = row.insertCell();
    if (matches[i] != undefined) {
      cell1.innerHTML = matches[i].text;
    }
    if (filler[i] != undefined) {
      cell2.innerHTML = filler[i].text;
    }
  }
}

onMounted(() => {
  getGameobjects();
  fillAnswers();
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
            <h2 class="heading">Neues Quizpaar</h2>
            <label class="category" for="category">Kategorie</label>
            <select name="category" id="category">
              <option value="number">Zahlen</option>
              <option value="person">Personen</option>
              <option value="place">Orte</option>
            </select>
            <div class="form">
              <div class="form-element">
                <label class="answer-label" for="answer-input">Antwort</label>
                <input type="text" id="answer-input" name="answer-input" placeholder="Hamburg">
              </div>
              <div class="form-element">
                <label class="question-label" for="question-input">Frage</label>
                <input type="text" id="question-input" name="question-input" required
                  placeholder="Wo steht die Elbphilharmonie?">
              </div>
            </div>
            <p class="message"></p>
            <button type='button' class="add-quizset-button">hinzufügen</button>
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

.success {
  color: green !important;
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
        text-align: center;
      }

      .message {
        font-size: 1rem;
        color: red;
      }

      .answer-label,
      .question-label,
      .category {
        margin: 3% 0;
      }

      .form {
        margin: 5% 0 5% 0;
        width: 100%;
        display: flex;
        justify-content: space-between;

        .form-element {
          display: flex;
          flex-direction: column;
          align-items: center;
          width: 50%;
        }

        input {
          margin: 0;
          width: 80%;
        }
      }

      width: 100%;
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
