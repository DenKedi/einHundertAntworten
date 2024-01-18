import router from '@/router';
import { defineStore } from 'pinia';

export interface Question {
  id: string;
  text: string;
  match: string;
  category: string
}

export interface Answer {
  id: string;
  text: String;
  matches: [string];
  filler: [string];
  category: string
}

export const useGameStore = defineStore({
  id: 'game',
  state: () => {
    return {
      questions: [] as Question[],
      answers: [] as Answer[],
      token: localStorage.getItem('token')
        ? JSON.parse(localStorage.getItem('token')!)
        : '',
    };
  },
  actions: {
    async getQuestions(): Promise<String> {
      const response = await fetch(
        'http://localhost:8080/game/getAllQuestions',
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + this.token,
          },
        }
      );
      const data = await response.json();
      console.log(response.status);
      if (response.ok) {
        this.questions = data;
        localStorage.removeItem('questions');
        localStorage.setItem('questions', JSON.stringify(this.questions));
      } else {
        return data.message.toString();
      }
    },
    async getAnswers(): Promise<String> {
      const response = await fetch('http://localhost:8080/game/getAllAnswers', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
      });
      const data = await response.json();
      console.log(response.status);
      if (response.ok) {
        this.answers = data;
        localStorage.removeItem('answers');
        localStorage.setItem('answers', JSON.stringify(this.answers));
      } else {
        return data.message.toString();
      }
    },
    async addAnswer(text: string, category: string): Promise<void> {
      const response = await fetch('http://localhost:8080/game/createAnswer', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
        body: JSON.stringify({ text, category }),
      });
      if (response.ok) {
        console.log('Success');
      } else {
        console.log((await response.json()).message.toString());
      }
    },
    async addQuestion(text: string, category: string): Promise<void> {
      const response = await fetch('http://localhost:8080/game/createQuestion', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
        body: JSON.stringify({ text, category }),
      });
      if (response.ok) {
        console.log('Success');
      } else {
        console.log((await response.json()).message.toString());
      }
    },
    async getQuestionById(id: string): Promise<Question> {
      const response = await fetch(
        'http://localhost:8080/game/getQuestion/' + id,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + this.token,
          },
        }
      );
      const data = await response.json();
      console.log(response.status);
      console.log("test");
      if (response.ok) {
        return data;
      } else {
        return data.message.toString();
      }
    },
    async getAnswerById(id: string): Promise<Answer> {
      const response = await fetch(
        'http://localhost:8080/game/getAnswer/' + id,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + this.token,
          },
        }
      );
      const data = await response.json();
      console.log(response.status);
      if (response.ok) {
        return data;
      } else {
        return data.message.toString();
      }
    },
    getQ() {
      console.log(this.questions);
    },
    getA() {
      console.log(this.answers);
    },
    clear() {
      this.questions = [];
      this.answers = [];
      localStorage.removeItem('questions');
      localStorage.removeItem('answers');
    }
  },
});
