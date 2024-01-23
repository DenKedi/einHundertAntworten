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

let fillerIds: string[] = [];
let currentSelectedAnswerId: string;
let answerIdToDelete: string;
let removedFillerIds: string[] = [];

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
    let answerId: string;
    if (i % 2 == 0) {
      answerId = 'answers-left';
    } else {
      answerId = 'answers-right';
    }

    $(`<div class="answer " id="${answers.value[i].id}"><p>${answers.value[i].text}</p><i class="fa-solid fa-close answer-delete"></i></div>`).appendTo(`#${answerId}`);
  }

  let elements = document.getElementsByClassName('answer');
  for (let element of elements) {
    element.addEventListener('click', function () {
      fillTable(element.id);
    });
  }

  let closeButtons = document.getElementsByClassName('answer-delete');
  for (let close of closeButtons) {
    close.addEventListener('click', function () {
      document.getElementById('confirm-modal').style.display = 'flex';
      document.getElementsByClassName('deleted-answer')[0].innerHTML = close.parentElement.innerText;
      answerIdToDelete = close.parentElement.id;
    });
  }
}

async function deleteAnswer(id: string) {
  await game.deleteAnswerById(id);
  document.getElementById(id).remove();
  clearTable();
}

function clearTable() {
  let table = $('#tableBody').get(0);
  table.innerHTML = '<tr></tr>';
  (document.getElementsByClassName('current-answer')[0] as HTMLParagraphElement).innerHTML = 'Antwort: ';
}

async function removeFillerAndMatches() {
  await game.removeMatchesAndFillerFromAnswer(currentSelectedAnswerId, [], removedFillerIds);
  removedFillerIds = [];
}

async function addQuizSet() {
  const questionValue = (document.getElementById('question-input') as HTMLInputElement);
  const answerValue = (document.getElementById('answer-input') as HTMLInputElement);
  const category = (document.getElementById('category') as HTMLSelectElement).value;
  const messageElement = (document.getElementsByClassName('message'))[0] as HTMLParagraphElement;
  messageElement.classList.remove('success');

  if (questionValue.value == '' || answerValue.value == '' || category == '') {
    messageElement.innerText = 'Bitte füllen Sie alle Felder aus.'
    return;
  }

  let hasAnswer = false;
  let hasQuestion = false;
  var answerFound: Answer;
  var newAnswer: Answer;

  answers.value.forEach(function (answer) {
    if (answerValue.value.toLocaleLowerCase() == answer.text.toLocaleLowerCase()) {
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

  if (hasQuestion) {
    messageElement.innerText = 'Die Frage existiert bereits.';
    return;
  }

  const question = await game.addQuestion(questionValue.value, category);
  const matchesIds: string[] = [];
  matchesIds.push(question.id);

  if (hasAnswer) {
    await game.addMatchesToAnswer(answerFound.id, matchesIds, []);
    await game.addMatchToQuestion(question.id, answerFound.id);
  } else {
    newAnswer = await game.addAnswer(answerValue.value, category);
    await game.addMatchesToAnswer(newAnswer.id, matchesIds, []);
    await game.addMatchToQuestion(question.id, newAnswer.id);
  }

  if (!messageElement.classList.contains('success')) {
    messageElement.classList.add('success');
  }

  messageElement.innerText = 'Quizset erfolgreich angelegt.';
  questionValue.value = '';

  await reset(category);
}

async function reset(category: string) {
  await clearAnswerContainer();
  let filter = document.getElementById('filter') as HTMLSelectElement;
  if (filter.options[filter.selectedIndex].value != 'all') {
    await getGameobjectsByCategory(category);
  } else {
    await getGameobjects();
  }
  fillAnswers();
}

async function addFillerOptions(answer: Answer) {
  let select = document.getElementById('filler');
  select.innerHTML = '';
  let defaultValue = document.createElement('option') as HTMLOptionElement;
  defaultValue.selected = true;
  defaultValue.disabled = true;
  defaultValue.innerHTML = 'Wähle Filler für die Antwort aus.';
  select.appendChild(defaultValue);

  const questions = await game.getQuestionByCategory(answer.category);

  for (let i = 0; i < questions.length; i++) {
    if (answer.filler.includes(questions[i].id) || answer.matches.includes(questions[i].id)) {
      continue;
    }

    let opt = document.createElement('option') as HTMLOptionElement;
    opt.value = questions[i].id;
    let fillerAnswer = await game.getAnswerById(questions[i].match);
    if (fillerAnswer) {
      opt.innerHTML = '(' + fillerAnswer.text + ') ' + questions[i].text;
    } else {
      opt.innerHTML = questions[i].text;
    }
    select.appendChild(opt);
  }
}

async function clearAnswerContainer() {
  let answersLeft = document.getElementById('answers-left');
  let answersRight = document.getElementById('answers-right');

  answersLeft.innerHTML = '';
  answersRight.innerHTML = '';
}

function addSelectFillerListener() {
  let select = document.getElementById('filler') as HTMLSelectElement;
  select.addEventListener("change", function () {
    addFillerTableRow(select.options[select.selectedIndex].text, select.options[select.selectedIndex].value);
    fillerIds.push(this.value);
    for (let i = 0; i < select.length; i++) {
      if (select.options[i].value == this.value)
        select.remove(i);
    }
    select.selectedIndex = 0;
    (document.getElementsByClassName('save-filler')[0] as HTMLButtonElement).disabled = false;
  });
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

  let saveFiller = document.getElementsByClassName('save-filler')[0];
  saveFiller.addEventListener('click', async function () {
    await removeFillerAndMatches();
    await addFillerToAnswer();
  });

  let filter = document.getElementById('filter') as HTMLSelectElement;
  filter.addEventListener('change', async function () {
    reset(filter.options[filter.selectedIndex].value);
    clearTable();
  });
}

async function addFillerToAnswer() {
  const res = await game.addMatchesToAnswer(currentSelectedAnswerId, [], fillerIds);
  let saveFillerMessageElem = document.getElementsByClassName('save-filler-message')[0];
  if (!res) {
    saveFillerMessageElem.innerHTML = 'Etwas lief schief.'
  }
  (document.getElementsByClassName('save-filler')[0] as HTMLButtonElement).disabled = true;

  let filter = document.getElementById('filter') as HTMLSelectElement;
  reset(filter.options[filter.selectedIndex].value);

  fillerIds = [];
  saveFillerMessageElem.innerHTML = 'Filler erfolgreich aktualisiert.'
  saveFillerMessageElem.classList.add('success');
}

async function getGameobjectsByCategory(category: string) {
  await game.getQuestionByCategory(category);
  await game.getAnswerByCategory(category);
  storedQuestions = localStorage.getItem('questions');
  storedAnswers = localStorage.getItem('answers');
  questions = ref<Question[]>(storedQuestions ? JSON.parse(storedQuestions) : []);
  answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);
}

async function getGameobjects() {
  await game.getQuestions();
  await game.getAnswers();
  storedQuestions = localStorage.getItem('questions');
  storedAnswers = localStorage.getItem('answers');
  questions = ref<Question[]>(storedQuestions ? JSON.parse(storedQuestions) : []);
  answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);
}

function addFillerTableRow(question: string, id: string) {
  let table = $('#tableBody').get(0);
  let hasEmptyRow = false;
  for (let i = 0; i < table.children.length; i++) {
    for (let j = 0; j < table.children[i].children.length; j++) {
      if (j == 0) {
        continue;
      }
      if ((table.children[i].children[j] as HTMLTableCellElement).innerText == '') {
        hasEmptyRow = true;
        (table.children[i].children[j] as HTMLTableCellElement).innerHTML = `<span>${question}</span>` + '<i class="fa-solid fa-close remove-filler"></i>';;
        return;
      };
    }
  }
  if (!hasEmptyRow) {
    let row = table.insertRow();
    row.insertCell();
    let cell2 = row.insertCell();
    cell2.innerHTML = `<span>${question}</span>` + '<i class="fa-solid fa-close remove-filler"></i>';;
  }

  let button = Array.from(document.querySelectorAll('.remove-filler')).pop();
  button.addEventListener("click", function () {
    button.parentElement.innerHTML = '';
    (document.getElementsByClassName('save-filler')[0] as HTMLButtonElement).disabled = false;
    removedFillerIds.push(id);
  })
}

async function confirmDeleteAnswer() {
  document.getElementById('confirm-modal').style.display = 'none';
  if (answerIdToDelete != '') {
    deleteAnswer(answerIdToDelete);
    answerIdToDelete = ''
  }
}

async function cancelDeleteAnswer() {
  document.getElementById('confirm-modal').style.display = 'none';
  answerIdToDelete = ''
}

async function fillTable(id: string) {
  (document.getElementsByClassName('save-filler')[0] as HTMLButtonElement).disabled = true;
  let saveFillerMessageElem = document.getElementsByClassName('save-filler-message')[0];
  saveFillerMessageElem.innerHTML = ''
  saveFillerMessageElem.classList.remove('success');
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
    let row = table.insertRow();
    let cell1 = row.insertCell();
    let cell2 = row.insertCell();
    if (matches[i] != undefined) {
      cell1.innerHTML = matches[i].text;
    }
    if (filler[i] != undefined) {
      let fillerAnswer = await game.getAnswerById(filler[i].match);
      if (fillerAnswer) {
        cell2.innerHTML = `<span>(${fillerAnswer.text}) ${filler[i].text}</span>` + '<i class="fa-solid fa-close remove-filler"></i>';
      } else {
        cell2.innerHTML = `<span>${filler[i].text}</span>` + '<i class="fa-solid fa-close remove-filler"></i>';
      }
    }
  }
  let removeButtons = document.getElementsByClassName('remove-filler');
  for (let i = 0; i < removeButtons.length; i++) {
    removeButtons[i].addEventListener('click', function () {
      (document.getElementsByClassName('save-filler')[0] as HTMLButtonElement).disabled = false;
      removedFillerIds.push(filler[i].id)
      removeButtons[i].parentElement.innerHTML = '';
    });
  }

  (document.getElementsByClassName('current-answer')[0] as HTMLParagraphElement).innerHTML = 'Antwort: ' + answer.text.toString();

  addFillerOptions(answer);
  currentSelectedAnswerId = answer.id;
}

onMounted(() => {
  getGameobjects();
  fillAnswers();
  addButtonListener();
  addSelectFillerListener();
});

</script>
<template>
  <div class="game-manager">
    <NavbarForm />
    <div class="main">

      <div class="delete-confirmation">
        <div class="modal-container" id="confirm-modal">
          <div class="modal-content-container">
            <h2>Bist Du sicher, dass du die Antwort</h2>
            <p class="deleted-answer"></p>
            <h2>löschen möchtest?</h2>
            <div class="modal-button-container">
              <button @click="confirmDeleteAnswer">Bestätigen</button>
              <button @click="cancelDeleteAnswer">Abbrechen</button>
            </div>
          </div>
        </div>
      </div>

      <div class="left">
        <div class="answers-container">
          <h1 class="heading">Alle Antworten</h1>
          <select name="filter" id="filter">
            <option value="all">Alle</option>
            <option value="number">Zahlen</option>
            <option value="person">Personen</option>
            <option value="place">Orte</option>
          </select>
          <div class="answers">
            <div id="answers-left"></div>
            <div id="answers-right"></div>
          </div>
        </div>
      </div>
      <div class="right">
        <p class="current-answer">Antwort: </p>
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
        <select name="filler" id="filler">
          <option value="" disabled selected>Wähle Filler für die Antwort aus.</option>
        </select>
        <button class="save-filler" disabled>Speichern</button>
        <p class="save-filler-message"></p>

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
                <option disabled selected value>Wähle eine Kategorie aus.</option>
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
  </div>
</template>

<style>
.game-manager {
  .main {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: flex-start;

    .modal-content-container {
      justify-content: center;
      height: 15rem;

      h2 {
        margin: 0;
      }

      p {
        margin-top: 0;
        color: white;
        font-weight: bold;
      }

      .modal-button-container {
        gap: 10px;

        button {
          width: 10rem;
        }
      }
    }

    .left {
      width: 60%;

      .answers-container {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        margin: 0 10%;

        .heading {
          font-size: 30px;
          font-weight: bold;
          padding: 10px;
          margin: 10px 0;
          width: 100%;
          border-radius: 5px;
        }

        .answers {
          display: flex;
          gap: 10px;

          #answers-left,
          #answers-right {
            flex: 45%;
          }

          .answer {
            position: relative;
            flex: 50%;
            grid-column: auto;
            display: flex;
            align-items: center;
            background-color: #f1f1f1;
            border: 2px solid #ddd;
            border-radius: 5px;
            padding: 10px;
            margin: 10px 0;
            font-size: 20px;
            cursor: default;
            max-width: 100%;
            min-height: 20%;

            .fa-solid {
              position: absolute;
              top: 10px;
              right: 10px;
            }

            p {
              margin: 0;
            }

            &:hover {
              cursor: pointer;
              background-color: #bebcbc;
            }
          }
        }
      }
    }

    .right {
      margin-top: 70px;
      display: flex;
      align-items: center;
      flex-direction: column;
      width: 40%;

      table {
        width: 80%;
        border-collapse: collapse;
        margin-top: 10px;
        background-color: #fff;
        border-radius: 10px;
        overflow: hidden;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }

      #filler {
        margin-top: 5%;
        width: 80%;
      }

      .save-filler {
        margin-top: 3%;
      }

      .save-filler-message {
        color: red;
      }

      th,
      td {
        border: 1px solid #ddd;
        padding: 24px 24px 12px 12px;
        text-align: center;
        font-size: 16px;
        width: 50%;
        position: relative;

        span:hover {
          background-color: white;
        }

        .fa-solid {
          position: absolute;
          top: 10px;
          right: 10px;
          cursor: pointer;
        }
      }

      th {
        background-color: #007bff;
        color: #fff;
      }

      .success {
        color: green !important;
      }

      .add-gameobjects {
        width: 80%;
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
              font-size: 2rem;
            }

            .message {
              font-size: 1rem;
              color: red;
            }

            .answer-label,
            .question-label,
            .category {
              margin: 3% 0;
              font-size: 1.25rem;
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
    }

    .table-container {
      display: block;
      /* Change this line */
      width: 100%;
    }
  }
}
</style>
