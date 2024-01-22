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
        </div>
            <div class="stats">
        </div>
    </div>
    <div class="general">
        <h1>{{ obj.username }} </h1>
      <div class="container">
        <div class="avatar avatar--green">
          <div class="avatar-body body--green">
            <div class="avatar-eye eye--left">
              <div class="avatar-eye-pupil pupil--purple">
          <span class="avatar-eye-pupil-blackThing">
            <span class="avatar-eye-pupil-lightReflection"></span>
          </span>
              </div>
            </div>
            <div class="avatar-eye eye--right">
              <div class="avatar-eye-pupil pupil--purple">
          <span class="avatar-eye-pupil-blackThing">
            <span class="avatar-eye-pupil-lightReflection"></span>
          </span>
              </div>
            </div>
            <div class="avatar-smile"></div>
            <div class="avatar-tooth tooth--left"></div>
            <div class="avatar-tooth tooth--right"></div>
          </div>
        </div>
        </div>
      <div class="coords">
            <span>Email: </span>
            <span>{{ obj.email }}</span>
        </div>
      <div class="coords">
        <span>Mitglied seit: {{ obj.createdOn }}</span>
      </div>

    </div>
</div>
</div>
</template>