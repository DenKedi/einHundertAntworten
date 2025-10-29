<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useGameStore } from '@/stores/game';
import type { UserProfile } from '@/stores/auth';

onMounted(async () => {
  try {
    await fetch(`${auth.serverIP}/`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    await game.getAnswers();
    await game.getQuestions();
    startNewGame();
  } catch (error) {
    console.log(error);
  }
});

// Constants and Variables
const auth = useAuthStore();
const game = useGameStore();
const token = auth.token;
const maxTurn = 10;

let storedQuestions = localStorage.getItem('questions');
let storedAnswers = localStorage.getItem('answers');
let questions = ref<Question[]>(
  storedQuestions ? JSON.parse(storedQuestions) : []
);
let answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);
let currentGameObject = ref<GameObject>(packRandomGameObject());
let currentTurn = ref(0);
let playerScore = ref(0);
let wrongAttempt = ref(0);
let remark = '';
let remarkColor = '';
let playerGrade = ref(0);

// Custom Types and Interfaces
interface Answer {
  id: string;
  text: String;
  matches: string[];
  filler: string[];
  category: string;
}

interface Question {
  id: string;
  text: string;
  match: string;
  category: string;
}

type GameObject = {
  answer: Answer;
  optionA: Question;
  optionB: Question;
  optionC: Question;
  optionD: Question;
  correctOption: Question;
};

function getRandomAnswer(): Answer | null {
  // Filter answers that meet requirements first
  const validAnswers = answers.value.filter(
    a =>
      a &&
      a.matches &&
      a.filler &&
      a.matches.length >= 1 &&
      a.filler.length >= 3
  );

  if (validAnswers.length === 0) {
    return null;
  }

  return validAnswers[Math.floor(Math.random() * validAnswers.length)];
}

function getRandomFiller(answer: Answer): Question[] | null {
  if (!answer.filler || answer.filler.length < 3) {
    return null;
  }

  let fillerIDs: string[] = [];
  let fillerQuestions: Question[] = [];
  let fillerIDs2: string[] = [...answer.filler];

  // Get 3 random filler Questions from answer.filler
  for (let i = 0; i < 3; i++) {
    if (fillerIDs2.length === 0) break;
    let fillerID = fillerIDs2[Math.floor(Math.random() * fillerIDs2.length)];
    fillerIDs.push(fillerID);
    // Remove fillerID from fillerIDs2
    fillerIDs2 = fillerIDs2.filter(id => id !== fillerID);
  }

  try {
    fillerIDs.forEach(fillerID => {
      let filler = questions.value.find(
        question => question && question.id === fillerID
      );
      if (filler) {
        fillerQuestions.push(filler);
      }
    });

    if (fillerQuestions.length < 3) {
      console.warn('Not enough valid filler questions found');
      return null;
    }

    return fillerQuestions;
  } catch (error) {
    console.error('Error in getRandomFiller:', error);
    return null;
  }
}

function getRandomMatch(answer: Answer): Question | null {
  if (!answer.matches || answer.matches.length === 0) {
    return null;
  }

  let matchID: string =
    answer.matches[Math.floor(Math.random() * answer.matches.length)];
  try {
    let question: Question | undefined = questions.value.find(
      question => question && question.id === matchID
    );
    return question || null;
  } catch (error) {
    console.error('Error finding matching question:', error);
    return null;
  }
}

function packRandomGameObject(): GameObject | null {
  try {
    let answer: Answer | null = getRandomAnswer();
    if (!answer) {
      console.warn('No valid answers available');
      return null;
    }

    let filler: Question[] = getRandomFiller(answer);
    if (!filler || filler.length < 3) {
      console.warn('Not enough filler questions for answer', answer.id);
      return null;
    }

    let match: Question = getRandomMatch(answer);
    if (!match) {
      console.warn('No matching question found for answer', answer.id);
      return null;
    }

    let options = [match, filler[0], filler[1], filler[2]].sort(
      () => Math.random() - 0.5
    );

    let go: GameObject = {
      answer: answer,
      optionA: options[0],
      optionB: options[1],
      optionC: options[2],
      optionD: options[3],
      correctOption: match,
    };

    if (
      go.answer &&
      go.optionA &&
      go.optionB &&
      go.optionC &&
      go.optionD &&
      go.correctOption
    ) {
      answers.value = answers.value.filter(a => a.id !== go.answer.id);
      return go;
    } else {
      console.warn('Incomplete game object, missing properties');
      return null;
    }
  } catch (error) {
    console.error('Error in packRandomGameObject:', error);
    return null;
  }
}

function startNewGame() {
  resetScoreModal();
  closeOptionModal();
  nextTurn();
}

function nextTurn() {
  currentGameObject.value = packRandomGameObject();
  if (currentGameObject.value == null) {
    console.error('No more valid game objects available. Game Over.');
    handleEndGame();
    return;
  }
  currentTurn.value++;
}

function checkForAnswer() {
  let matchingQuestionID = currentGameObject.value.correctOption.id; // gets the ID of the correct answer
  let userSelection: HTMLInputElement = document.querySelector(
    'input[type=radio]:checked'
  ); // gets the selected answer
  if (!userSelection) {
    document.getElementById('option-modal').style.display = 'flex';
    return false;
  }
  let selectionID = userSelection.nextElementSibling.id; // gets the ID of the selected response

  if (selectionID === matchingQuestionID) {
    document.getElementById(selectionID).style.backgroundColor =
      'rgba(175,255,142,0.71)';
    playerScore.value++;
    return true;
  } else {
    document.getElementById(selectionID).style.backgroundColor =
      'rgba(252,125,125,0.81)';
    document.getElementById(matchingQuestionID).style.backgroundColor =
      'rgba(175,255,142,0.71)';
    wrongAttempt.value++;
    return true;
  }
}

// next question button
function handleNextQuestion() {
  if (!checkForAnswer()) {
    return;
  }
  toggleNextButton(true);
  unCheckRadioButtons();
  setTimeout(() => {
    if (currentTurn.value < 10) {
      toggleNextButton(false);
      nextTurn();
    } else {
      handleEndGame();
    }
    resetOptionBackground();
  }, 1000);
}

function toggleNextButton(isDisabled: boolean) {
  let button = document.getElementsByClassName(
    'next-button'
  )[0] as HTMLButtonElement;
  button.disabled = isDisabled;
}

// reset all background options to zero
function resetOptionBackground() {
  let maxAttempts = 100;
  let delay = 200;

  function tryReset() {
    try {
      document.getElementById(
        currentGameObject.value.optionA.id
      ).style.backgroundColor = '';
      document.getElementById(
        currentGameObject.value.optionB.id
      ).style.backgroundColor = '';
      document.getElementById(
        currentGameObject.value.optionC.id
      ).style.backgroundColor = '';
      document.getElementById(
        currentGameObject.value.optionD.id
      ).style.backgroundColor = '';
    } catch (error) {
      if (maxAttempts > 0) {
        setTimeout(tryReset, delay);
        maxAttempts--;
      }
    }
  }

  // Start the first attempt
  tryReset();
}

// reset all radio buttons for the next question
function unCheckRadioButtons() {
  const optionsNodeList: NodeListOf<HTMLElement> =
    document.getElementsByName('option');
  const options: HTMLInputElement[] = Array.from(
    optionsNodeList
  ) as HTMLInputElement[];
  for (let i = 0; i < options.length; i++) {
    options[i].checked = false;
  }
}

// when all questions have been answered
function handleEndGame() {
  if (playerScore.value <= 3) {
    remark = 'ähm ja das war schlecht...';
    remarkColor = '#f12727';
  } else if (playerScore.value >= 4 && playerScore.value < 8) {
    remark = 'ach komm das kannst du besser!';
    remarkColor = '#ff7520';
  } else if (playerScore.value >= 8) {
    remark = 'Supi gemacht!';
    remarkColor = '#6fff18';
  }
  playerGrade.value = (playerScore.value / 10) * 100;

  document.getElementById('remarks').style.color = remarkColor;
  document.getElementById('score-modal').style.display = 'flex';

  //Saving Data for User
  if (auth.token) {
    let userProfile: UserProfile = auth.userProfile;
    userProfile.gamesPlayed++;
    userProfile.score += playerScore.value;
    auth.updateUserProfile(token, userProfile, auth.userID);
  }
}

// resets the game, shuffles the questions again and closes the score board
function resetScoreModal() {
  answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);
  questions = ref<Question[]>(
    storedQuestions ? JSON.parse(storedQuestions) : []
  );
  currentTurn.value = 0;
  playerScore.value = 0;
  wrongAttempt.value = 0;
  remark = '';
  remarkColor = '';
  playerGrade.value = 0;
  document.getElementById('score-modal').style.display = 'none';
  toggleNextButton(false);
}

// close the close warning
function closeOptionModal() {
  document.getElementById('option-modal').style.display = 'none';
}

function getCurrentGameObject() {
  return currentGameObject;
}

// Expose the function globally for testing purposes
declare global {
  interface Window {
    getCurrentGameObject: () => ReturnType<typeof getCurrentGameObject>;
  }
}

window.getCurrentGameObject = getCurrentGameObject;
</script>

<template>
  <main>
    <div class="modal-container" id="score-modal">
      <div class="modal-content-container">
        <h1>ENDEEEE.</h1>

        <div class="grade-details">
          <p>Anzahl der Fragen : {{ maxTurn }}</p>
          <p>
            Falsche Antworten : {{ wrongAttempt
            }}<span id="wrong-answers"></span>
          </p>
          <p>
            Richtige Antworten : {{ playerScore
            }}<span id="right-answers"></span>
          </p>
          <p>Prozent : {{ playerGrade }}%<span id="grade-percentage"></span></p>
          <p>
            <span id="remarks">{{ remark }}</span>
          </p>
          <small v-if="!auth.token" style="color: white">
            Melde dich an, um deine Ergebnisse zu speichern!
          </small>
        </div>
        <div class="modal-button-container">
          <button @click="startNewGame">Ich will noch einmal!</button>
        </div>
        <div class="modal-button-container">
          <a href="/home">
            <button @click="resetScoreModal">Hauptmenü</button>
          </a>
        </div>
      </div>
    </div>
    <div class="game-quiz-container">
      <div class="game-details-container">
        <h1>
          Score : {{ playerScore }}<span id="player-score"></span> /
          {{ maxTurn }}
        </h1>
        <h1>
          Frage : {{ currentTurn }}<span id="question-number"></span> /
          {{ maxTurn }}
        </h1>
      </div>

      <div class="game-question-container">
        <h1 id="display-question">
          {{ currentGameObject ? currentGameObject.answer.text : 'Loading...' }}
        </h1>
      </div>

      <div class="game-options-container">
        <div class="modal-container" id="option-modal">
          <div class="modal-content-container">
            <h1>Bitte such dir doch noch eine Antwort aus!</h1>
            <h2>Ich meine, es ist ein Quiz, entscheide dich!</h2>

            <div class="modal-button-container">
              <button @click="closeOptionModal">Okay, okay...</button>
            </div>
          </div>
        </div>

        <span>
          <input
            type="radio"
            id="optionA"
            name="option"
            class="radio"
            value="optionA"
          />
          <label
            for="optionA"
            class="option optionA-label"
            :id="currentGameObject ? currentGameObject.optionA.id : ''"
          >
            {{
              currentGameObject ? currentGameObject.optionA.text : 'Loading...'
            }}
          </label>
        </span>

        <span>
          <input
            type="radio"
            id="optionB"
            name="option"
            class="radio"
            value="optionB"
          />
          <label
            for="optionB"
            class="option optionB-label"
            :id="currentGameObject ? currentGameObject.optionB.id : ''"
          >
            {{
              currentGameObject ? currentGameObject.optionB.text : 'Loading...'
            }}
          </label>
        </span>

        <span>
          <input
            type="radio"
            id="optionC"
            name="option"
            class="radio"
            value="optionC"
          />
          <label
            for="optionC"
            class="option optionC-label"
            :id="currentGameObject ? currentGameObject.optionC.id : ''"
          >
            {{
              currentGameObject ? currentGameObject.optionC.text : 'Loading...'
            }}
          </label>
        </span>

        <span>
          <input
            type="radio"
            id="optionD"
            name="option"
            class="radio"
            value="optionD"
          />
          <label
            for="optionD"
            class="option optionD-label"
            :id="currentGameObject ? currentGameObject.optionD.id : ''"
          >
            {{
              currentGameObject ? currentGameObject.optionD.text : 'Loading...'
            }}
          </label>
        </span>
      </div>

      <div class="next-button-container">
        <button class="next-button" @click="handleNextQuestion">Weiter</button>
      </div>
    </div>
  </main>
</template>
