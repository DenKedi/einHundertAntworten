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
      if (response.ok) {
        this.answers = data;
        localStorage.removeItem('answers');
        localStorage.setItem('answers', JSON.stringify(this.answers));
      } else {
        return data.message.toString();
      }
    },
    async addAnswer(text: string, category: string): Promise<Answer> {
      const response = await fetch('http://localhost:8080/game/createAnswer', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
        body: JSON.stringify({ text, category }),
      });
      if (response.ok) {
        return await response.json();
      }
    },
    async addMatchesToAnswer(answerId: string, matches: string[], filler: string[]): Promise<Answer> {
      const response = await fetch(`http://localhost:8080/game/answer/${answerId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
        body: JSON.stringify({ filler, matches }),
      });

      const data = await response.json();
      if (response.ok) {
        return data;
      }
    },
    async addQuestion(text: string, category: string): Promise<Question> {
      const response = await fetch('http://localhost:8080/game/createQuestion', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
        body: JSON.stringify({ text, category }),
      });
      if (response.ok) {
        return await response.json();
      }
    },
    async addMatchToQuestion(questionId: string, match: string): Promise<Question> {
      const response = await fetch(`http://localhost:8080/game/question/${questionId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
        body: JSON.stringify({ match }),
      });
      const data = await response.json();
      if (response.ok) {
        return data;
      } else {
        return data.message.toString();
      }
    },
    async getQuestionByCategory(category: string): Promise<Question[]> {
      const response = await fetch(
        'http://localhost:8080/game/getQuestions?category=' + category,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + this.token,
          },
        }
      );
      const data = await response.json();
      if (response.ok) {
        return data;
      } else {
        return data.message.toString();
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
      if (response.ok) {
        return data;
      } else {
        return data.message.toString();
      }
    },
    async getFillerOfAnswer(id: string): Promise<String[]> {
      const response = await fetch(
        'http://localhost:8080/game/Answer/getFiller?id=' + id,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + this.token,
          },
        }
      );
      const data = await response.json();
      if (response.ok) {
        return data;
      }
    },
    async deleteAnswerById(id: string): Promise<Answer> {
      const response = await fetch(
        'http://localhost:8080/game/deleteAnswer/' + id,
        {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + this.token,
          },
        }
      );
      const data = await response.json();
      if (response.ok) {
        return data;
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
