<script lang="ts" setup>
import NavbarForm from '../components/NavbarForm.vue';
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';

onMounted(async () => {
    const response = await fetch('http://localhost:8080/', {
        headers: {
            'Authorization': `Bearer ${token}`
        },
    });
    if (response.status === 401) {
        auth.logout();
    }
});

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

const auth = useAuthStore();
const userID = auth.userID;
const token = auth.token;
const storedData = localStorage.getItem('userProfile');

let obj: UserProfile;


if (storedData){
  obj = JSON.parse(storedData);
}else{
  obj = {
  userID: '',
  username: '',
  firstName: '',
  lastName: '',
  email: '',
  gamesPlayed: 0,
  createdOn: '',
  score: 0,
};
}
obj.createdOn = convertDateFormat(obj.createdOn);
  
function convertDateFormat(inputDate: string): string {
  const inputDateTime = new Date(inputDate);
  
  // Ensure the date is valid
  if (isNaN(inputDateTime.getTime())) {
    throw new Error("Invalid date format");
  }

  const day = inputDateTime.getUTCDate().toString().padStart(2, "0");
  const month = (inputDateTime.getUTCMonth() + 1).toString().padStart(2, "0");
  const year = inputDateTime.getUTCFullYear();

  return `${day}.${month}.${year}`;
}

</script>

<template>
  <NavbarForm />

  <div class="center">

<div class="card" >
    <div class="additional">
        <div class="user-card">
            <div class="level center">
                {{ obj.gamesPlayed }} Spiele gespielt
            </div>
            <div class="points center">
                {{ obj.score }} Punkte
            </div>
            <svg width="110" height="110" viewBox="0 0 250 250" xmlns="http://www.w3.org/2000/svg" role="img" aria-labelledby="title desc" class="center">
                <title id="title">Teacher</title>
            </svg>
        </div>
        <div class="more-info">
            <h1></h1>
            <div class="coords">
                <span>Mitglied seit: {{ obj.createdOn }}</span>
            </div>
            <div class="stats">
                <div>
                    <div class="title">Siege</div>
                    <i class="fa fa-trophy"></i>
                    <div class="value">2</div>
                </div>
                <div>
                    <div class="title">Spiele</div>
                    <i class="fa fa-gamepad"></i>
                    <div class="value">27</div>
                </div>
                <div>
                    <div class="title">Freunde</div>
                    <i class="fa fa-group"></i>
                    <div class="value">123</div>
                </div>
                <div>
                    <div class="title">Coffee</div>
                    <i class="fa fa-coffee"></i>
                    <div class="value infinity">∞</div>
                </div>
            </div>
        </div>
    </div>
    <div class="general">
        <h1>{{ obj.firstName }} {{ obj.lastName}} </h1>
        <div class="coords">
            <span>Email: </span>
            <span>{{ obj.email }}</span>
        </div>
        <span class="more">Swipe für mehr Info</span>
    </div>
</div>
</div>
</template>

<style></style>
