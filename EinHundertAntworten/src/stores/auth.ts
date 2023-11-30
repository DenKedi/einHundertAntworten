import router from '@/router';
import { defineStore } from 'pinia';

interface UserProfile{
  userID: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  gamesPlayed: number;
  createdOn: string;
  score: number;
}

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
      userProfile: localStorage.getItem('userProfile') ? JSON.parse(localStorage.getItem('userProfile')!) : '',
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
        this.user = emailOrUsername;
        this.token = token;
        this.userID = await this.getUserID(emailOrUsername, token);
        await this.getUserProfile(token, this.userID);
        router.push(this.returnUrl || '/');
      } else {
        return data.message.toString();
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
        this.user = username;
        this.token = token;
        this.userID = await this.getUserID(username, token);

        await this.getUserProfile(token, this.userID);
        router.push(this.returnUrl || '/');
      } else {
        console.log(data.message)
        return data.message.toString();
      }
    }, async getUserProfile(bearer: string, userID: string) {
        const response = await fetch(`http://localhost:8080/user/getUser/${userID}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + bearer,
          },
        });
        const data = await response.json() as UserProfile;
        if (response.ok) {
          localStorage.setItem('userProfile', JSON.stringify(data));
          this.userProfile = data;
          return data;
        } else {
          console.log('error');
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
      localStorage.removeItem('userProfile')
      router.push('/login');
    },
  },
});
