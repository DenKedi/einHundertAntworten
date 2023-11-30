<script lang="ts" setup>
import NavbarForm from '../components/NavbarForm.vue';
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';

const auth = useAuthStore();
const userID = auth.userID;
const token = auth.token;
const data = ref<UserProfile>();

/*
String userID, String username, String firstName, String lastName, String email, int gamesPlayed, String createdOn, int score

*/
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
onMounted(async () => {
  try {
    
    const response = await fetch(`http://localhost:8080/user/getUser/${userID}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
  });
  data.value = await response.json() as UserProfile;
  if (response.ok) {
    console.log(data.value);
  } else {
    console.log('Error');
  }
} catch (error) {
  console.log(error);
  
}
 
});
  
  

</script>

<template>
  <NavbarForm />

  <div class="center">

<div class="card">
    <div class="additional">
        <div class="user-card">
            <div class="level center">
                Level 13
            </div>
            <div class="points center">
                5,312 Points
            </div>
            <svg width="110" height="110" viewBox="0 0 250 250" xmlns="http://www.w3.org/2000/svg" role="img" aria-labelledby="title desc" class="center">
                <title id="title">Teacher</title>
            </svg>
        </div>
        <div class="more-info">
            <h1>Jane Doe</h1>
            <div class="coords">
                <span>Mitglied seit: November 2023</span>
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
        <h1>Jane Doe</h1>
        <div class="coords">
            <span>Email:</span>
            <span>Jane.doe@justfun.de</span>
        </div>
        <div class="coords">
            <span>Passwort:</span>
            <span>*********</span>
        </div>
        <span class="more">Maus swipe für mehr Info</span>
    </div>
</div>
</div>
</template>

<style></style>
