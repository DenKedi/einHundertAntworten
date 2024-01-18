<script setup lang="ts">
import NavbarForm from '../components/NavbarForm.vue';
import $ from 'jquery';
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useGameStore } from '@/stores/game';
import type { UserProfile } from '@/stores/auth';
//Setup on mounted
onMounted(async () => {
    try{
        console.log(getRandomAnswer());
  const response = await fetch('http://localhost:8080/', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  if (response.status === 401) {
    auth.logout();
  }
  if (response.status === 200) {
    game.getAnswers();
    game.getQuestions();
    startNewGame();
  }
}catch(error){
    console.log(error);
}
});

  

// Constants and Variables
const auth = useAuthStore();
const game = useGameStore();
const token = auth.token;
var storedQuestions = localStorage.getItem('questions');
var storedAnswers = localStorage.getItem('answers');
var questions = ref<Question[]>(storedQuestions ? JSON.parse(storedQuestions) : []);
var answers = ref<Answer[]>(storedAnswers ? JSON.parse(storedAnswers) : []);
var currentGameObject: GameObject;
var currentTurn = 1;
const maxTurn = 10;
var playerScore = 0;
var wrongAttempt = 0;
var remark = '';
var remarkColor = '';
var playerGrade = 0;

//Types und Interfaces
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

//Sample Objects
currentGameObject = {
  answer: {
    id: '1',
    text: 'Wie viele Menschen leben in Deutschland?',
    matches: ['1'],
    filler: ['2', '3', '4'],
    category: 'Bevölkerung',
  },
  optionA: {
    id: '10',
    text: '80 Millionen',
    match: '1',
    category: 'Bevölkerung',
  },
  optionB: {
    id: '11',
    text: '90 Millionen',
    match: '5645',
    category: 'Bevölkerung',
  },
  optionC: {
    id: '12',
    text: '70 Millionen',
    match: '245645',
    category: 'Bevölkerung',
  },
  optionD: {
    id: '13',
    text: '60 Millionen',
    match: '136767',
    category: 'Bevölkerung',
  },
  correctOption: {
    id: '10',
    text: '80 Millionen',
    match: '1',
    category: 'Bevölkerung',
  },
};


function getRandomAnswer(): Answer {
    var answer:Answer = answers.value[Math.floor(Math.random() * answers.value.length)];
    if (answer.matches.length <= 1 && answer.filler.length <= 3) {
        return answer;
    }
  return null;
}

function getRandomFiller(answer: Answer): Question[] {
  var fillerIDs = [];
  var fillerQuestions: Question[] = [];
  //Get 3 random filler Questions from answer.filler
  for (let i = 0; i < 3; i++) {
    fillerIDs.push(
      answer.filler[Math.floor(Math.random() * answer.filler.length)]
    );
  }

  try {
    fillerIDs.forEach(fillerID => {
      var filler = questions.value.find(question => question.id === fillerID);
      fillerQuestions.push(filler);
      return fillerQuestions;
    });
  } catch (error) {
    return [];
  }
}
function getRandomMatch(answer: Answer): Question {
  var matchID: string =
    answer.matches[Math.floor(Math.random() * answer.matches.length)];
  try {
    var question:Question = questions.value.find(question => question.id === matchID);
  } catch (error) {
    return { id: '', text: '', match: '', category: '' }; //Some other way of handling errors
  }
}

function packRandomGameObject(): GameObject {
    
  var answer:Answer = getRandomAnswer();
  if(!answer){
    return null;
  }
  console.log(answer);
  var filler:Question[] = getRandomFiller(answer);
  var match:Question = getRandomMatch(answer);
  var options = [match, filler[0], filler[1], filler[2]].sort(
    () => Math.random() - 0.5
  );
  var go = {
    answer: answer,
    optionA: options[0],
    optionB: options[1],
    optionC: options[2],
    optionD: options[3],
    correctOption: match,
  };

  document.getElementById('OptionA').id = go.optionA.id;
  document.getElementById('OptionB').id = go.optionB.id;
  document.getElementById('OptionC').id = go.optionC.id;
  document.getElementById('OptionD').id = go.optionD.id;

  return go;
}



/*To Delete?
function getRandomQuestion():Question{
    return questions.value[Math.floor(Math.random() * questions.value.length)];
}
*/

function checkForAnswer() {
  var matchingQuestionID = currentGameObject.correctOption.id; //bekommt die ID der richtigen Antwort
  var userSelection:HTMLInputElement = document.querySelector('input[type=radio]:checked'); //bekommt die ausgewählte Antwort
  if(!userSelection){
    document.getElementById('option-modal').style.display = "flex";
    return false;
  };
  var selectionID = userSelection.nextElementSibling.id; //bekommt die ID der ausgewählten Antwort
  
  if(selectionID === matchingQuestionID){
    document.getElementById(selectionID).style.backgroundColor = '#6fff18';  
    playerScore++;
    return true;
  } else{
    document.getElementById(selectionID).style.backgroundColor = '#f12727';
    document.getElementById(matchingQuestionID).style.backgroundColor = '#6fff18';
    wrongAttempt++;
    return true;
  }
}
function nextTurn() {
  currentGameObject = packRandomGameObject();
    currentTurn++;
}
//nächste Frage button:
function handleNextQuestion() {
  if (!checkForAnswer()) {
    return;
  } //checkt, ob die Antwort richtig oder falsch
  unCheckRadioButtons();
  //verzögert die Frage um eine Sekunde, um es smoother zu gestalten
  setTimeout(() => {
    if (currentTurn != 11) {
      //erzeugt die nächste Frage, wenn der Index nicht höher als neun ist - 10 Fragen insgesamt
      nextTurn();
    } else {
      handleEndGame(); //beendet das Spiel nach der 10. frage
    }
    resetOptionBackground();
  }, 1000);
}

//resettet alle hintergrund optionen zu null
function resetOptionBackground() {
    document.getElementById(currentGameObject.optionA.id).style.backgroundColor = '';
    document.getElementById(currentGameObject.optionB.id).style.backgroundColor = '';
    document.getElementById(currentGameObject.optionC.id).style.backgroundColor = '';
    document.getElementById(currentGameObject.optionD.id).style.backgroundColor = '';
}

// resettet alle radio buttons für die nächste frage
function unCheckRadioButtons() {
const optionsNodeList: NodeListOf<HTMLElement> = document.getElementsByName('option');
const options: HTMLInputElement[] = Array.from(optionsNodeList) as HTMLInputElement[];
  for (let i = 0; i < options.length; i++) {
    options[i].checked = false;
  }
}

//wenn alle Fragen fertig beantwortet sind:
function handleEndGame() {
  
  // checkt die Antworten und gibt farbiges Feedback :)
  if (playerScore <= 3) {
    remark = 'ähm ja das war schlecht...';
    remarkColor = '#f12727';
  } else if (playerScore >= 4 && playerScore < 8) {
    remark = 'ach komm das kannst du besser!';
    remarkColor = '#ff7520';
  } else if (playerScore >= 8) {
    remark = 'Supi gemacht!';
    remarkColor = '#6fff18';
  }
  playerGrade = (playerScore / 10) * 100;

  //für die Anzeige des score boards
  document.getElementById('remarks').style.color = remarkColor;
  document.getElementById('score-modal').style.display = 'flex';

  //Saving Data for User
    var user = auth.user;
    var userProfile:UserProfile = auth.userProfile;
    userProfile.gamesPlayed++;
    userProfile.score += playerScore;
    auth.updateUserProfile(token, userProfile, user.id);    
}

//resettet das Spiel, mischt wieder die Fragen und schließt natürlich das Score board
function resetScoreModal() {
  currentTurn = 1;
  playerScore = 0;
  wrongAttempt = 0;
  remark = '';
  remarkColor = '';
  playerGrade = 0;
  currentGameObject = null;
  document.getElementById('score-modal').style.display = 'none';
}

//Funktion um die Close warnung zu schließen
function closeOptionModal() {
  document.getElementById('option-modal').style.display = 'none';
}

function startNewGame() {
    resetScoreModal();
    closeOptionModal();
    //resetOptionBackground();
    nextTurn();
}

</script>

<template>
  <main>
    <NavbarForm />

    <!-- Ende des Spiels -->
    <div class="modal-container" id="score-modal">
      <div class="modal-content-container">
        <h1>ENDEEEE.</h1>

        <div class="grade-details">
          <p>Anzahl der Fragen : {{ maxTurn }}</p>
          <p>Falsche Antworten : {{ wrongAttempt }}<span id="wrong-answers"></span></p>
          <p>Richtige Antworten : {{ playerScore }}<span id="right-answers"></span></p>
          <p>Prozent : {{ playerGrade }}%<span id="grade-percentage"></span>%</p>
          <p><span id="remarks">{{ remark }}</span></p>
        </div>
        <div class="modal-button-container">
          <button @click="startNewGame">ich will noch einmal</button>
        </div>
        <!--Button mit hauptmenü verbinden!!!!1-->
        <div class="modal-button-container">
          <button @click="resetScoreModal">Haumptmenü</button>
        </div>
      </div>
    </div>

    <!--Spieloberfläche-->
    <div class="game-quiz-container">
      <div class="game-details-container">
        <h1>Score : {{ playerScore }}<span id="player-score"></span> / {{maxTurn}}</h1>
        <h1>Frage : {{ currentTurn }}<span id="question-number"></span> / {{maxTurn}}</h1>
      </div>

      <div class="game-question-container">
        <h1 id="display-question">{{ currentGameObject.answer.text }}</h1>
      </div>

      <!--wenn man auf weiter geht ohne eine Antwort auszuwählen-->
      <div class="game-options-container">
        <div class="modal-container" id="option-modal">
          <div class="modal-content-container">
            <h1>Bitte such dir doch noch eine Antwort aus!</h1>
            <h2>Ich meine, es ist ein Quiz, entscheide dich!</h2>

            <div class="modal-button-container">
              <button @click="closeOptionModal">Okay,okay...</button>
            </div>
          </div>
        </div>

        <!--Antwortoptionen:-->
        <span>
          <input
            type="radio"
            id="optionA"
            name="option"
            class="radio"
            value="optionA"
          />
          <label for="optionA" class="option optionA-label" :id="currentGameObject.optionA.id">{{ currentGameObject.optionA.text }}</label>
        </span>

        <span>
          <input
            type="radio"
            id="optionB"
            name="option"
            class="radio"
            value="optionB"
          />
          <label for="optionB" class="option optionB-label" :id="currentGameObject.optionB.id">{{ currentGameObject.optionB.text }}</label>
        </span>

        <span>
          <input
            type="radio"
            id="optionC"
            name="option"
            class="radio"
            value="optionC"
          />
          <label for="optionC" class="option optionC-label" :id="currentGameObject.optionC.id">{{ currentGameObject.optionC.text }}</label>
        </span>

        <span>
          <input
            type="radio"
            id="optionD"
            name="option"
            class="radio"
            value="optionD"
          />
          <label for="optionD" class="option optionD-label" :id="currentGameObject.optionD.id">{{ currentGameObject.optionD.text }}</label>
        </span>
      </div>

      <div class="next-button-container">
        <button @click="handleNextQuestion">Weiter hihi</button>
      </div>
    </div>
  </main>
</template>
