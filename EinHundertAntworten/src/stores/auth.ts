import router from '@/router';
import { defineStore } from 'pinia';

export const useAuthStore = defineStore({
  id: 'auth',
  state: () => {
    return {
      user: localStorage.getItem('user')
        ? JSON.parse(localStorage.getItem('user')!)
        : null,
      token: localStorage.getItem('token')
        ? JSON.parse(localStorage.getItem('token')!)
        : '',
      userID: localStorage.getItem('userID')
        ? JSON.parse(localStorage.getItem('userID')!)
        : '',
      returnUrl: '/home',
    };
  },
  actions: {
    async login(emailOrUsername: string, password: string) {
      const response = await fetch('http://localhost:8080/user/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ emailOrUsername, password }),
      });
      const data = await response.json();
      console.log(response.status);
      console.log(data);
      if (response.ok) {
        const token = await data.token;
        localStorage.setItem('user', JSON.stringify(emailOrUsername));
        localStorage.setItem('token', JSON.stringify(token));
        this.getUserID(emailOrUsername, token);
        this.user = emailOrUsername;
        this.token = token;
        router.push(this.returnUrl || '/');
      } else {
        throw new Error(data.message);
      }
    },
    async register(username: string, email: string, password: string) {
      const response = await fetch('http://localhost:8080/user/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
      });
      const data = await response.json();
      console.log(response.status);
      console.log(data);
      if (response.ok) {
        const token = await data.token;
        localStorage.setItem('user', JSON.stringify(username));
        localStorage.setItem('token', JSON.stringify(token));
        this.getUserID(username, token);
        this.user = username;
        this.token = token;
        router.push(this.returnUrl || '/');
      } else {
        throw new Error(data.message);
      }
    },
    async getUserID(username: string, bearer: string) {
      const url = new URL('http://localhost:8080/user/userID');
      url.searchParams.append('username', username);

      const response = await fetch(url.toString(), {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + bearer,
        },
      });
      const data = await response.json();
      console.log(response.status);
      console.log(data);
      if (response.ok) {
        const userID = await data.userID;
        localStorage.setItem('userID', JSON.stringify(userID));
        this.userID = userID;
        return userID;
      } else {
        throw new Error(data.message);
      }
    },
    logout() {
      this.user = null;
      this.token = '';
      this.userID = '';
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      localStorage.removeItem('userID');
      router.push('/login');
    },
  },
});
