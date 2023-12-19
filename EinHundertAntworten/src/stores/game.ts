import router from '@/router';
import { defineStore } from 'pinia';

export interface Question {
  id: string;
  text: string;
  match: string; //category: string
}

export interface Answer {
  id: string;
  text: String;
  matches: [string];
  filler: [string];
  //,category: string
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
    async getQuestions(): Promise<Question[]> {
      const response = await fetch(
        'http://localhost:8080/game/getAllQuestions',
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.token,
          },
        }
      );
      const data = await response.json();
      console.log(response.status);
      console.log(data);
      if (response.ok) {
        this.questions = data;
        console.log(this.questions);
      } else {
        return data as Question[];
      }
    },
    async getAnswers() {
      const response = await fetch('http://localhost:8080/game/getAllAnswers', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + this.token,
        },
      });
      const data = await response.json();
      console.log(response.status);
      console.log(data);
      if (response.ok) {
        this.answers = data;
        console.log(this.answers);
      } else {
        return data.message.toString();
      }
    },
    getQ(){
        console.log(this.questions);
    },
    getA(){
        console.log(this.answers);
    }
  },
});
