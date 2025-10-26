import { defineStore } from 'pinia';

export interface Question {
  id: string;
  text: string;
  match: string;
  category: string;
}

export interface Answer {
  id: string;
  text: String;
  matches: [string];
  filler: [string];
  category: string;
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
      ServerIP: import.meta.env.VITE_API_URL || 'http://localhost:3000',
    };
  },
  actions: {
    async getQuestions(): Promise<String> {
      const response = await fetch(`${this.ServerIP}/game/getAllQuestions`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
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
      const response = await fetch(`${this.ServerIP}/game/getAllAnswers`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
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
      const response = await fetch(`${this.ServerIP}/game/createAnswer`, {
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
    async removeMatchesAndFillerFromAnswer(
      answerId: string,
      matches: string[],
      filler: string[]
    ): Promise<Answer> {
      const response = await fetch(
        `${this.ServerIP}/game/answer/removeFillerAndMatches/` + answerId,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + this.token,
          },
          body: JSON.stringify({ filler, matches }),
        }
      );
      const data = await response.json();
      if (response.ok) {
        return data;
      }
    },
    async addMatchesToAnswer(
      answerId: string,
      matches: string[],
      filler: string[]
    ): Promise<Answer> {
      const response = await fetch(`${this.ServerIP}/game/answer/${answerId}`, {
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
      const response = await fetch(`${this.ServerIP}/game/createQuestion`, {
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
    async addMatchToQuestion(
      questionId: string,
      match: string
    ): Promise<Question> {
      const response = await fetch(
        `${this.ServerIP}/game/question/${questionId}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + this.token,
          },
          body: JSON.stringify({ match }),
        }
      );
      const data = await response.json();
      if (response.ok) {
        return data;
      } else {
        return data.message.toString();
      }
    },
    async getQuestionByCategory(category: string): Promise<Question[]> {
      const response = await fetch(
        `${this.ServerIP}/game/getQuestions?category=` + category,
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
        return data;
      } else {
        return data.message.toString();
      }
    },
    async getAnswerByCategory(category: string): Promise<Answer[]> {
      const response = await fetch(
        `${this.ServerIP}/game/getAnswers?category=` + category,
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
        this.answers = data;
        localStorage.removeItem('answers');
        localStorage.setItem('answers', JSON.stringify(this.answers));
        return data;
      } else {
        return data.message.toString();
      }
    },
    async getQuestionById(id: string): Promise<Question> {
      const response = await fetch(`${this.ServerIP}/game/getQuestion/` + id, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
      });
      const data = await response.json();
      if (response.ok) {
        return data;
      } else {
        return data.message.toString();
      }
    },
    async getAnswerById(id: string): Promise<Answer> {
      const response = await fetch(`${this.ServerIP}/game/getAnswer/` + id, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
      });
      const data = await response.json();
      if (response.ok) {
        return data;
      } else {
        return data.message.toString();
      }
    },
    async getFillerOfAnswer(id: string): Promise<String[]> {
      const response = await fetch(
        '${this.ServerIP}/game/Answer/getFiller?id=' + id,
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
      const response = await fetch(`${this.ServerIP}/game/deleteAnswer/` + id, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + this.token,
        },
      });
      const data = await response.json();
      if (response.ok) {
        return data;
      }
    },
    clear() {
      this.questions = [];
      this.answers = [];
      localStorage.removeItem('questions');
      localStorage.removeItem('answers');
    },
  },
});
