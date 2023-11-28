<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth';
import { reactive } from 'vue';


const user = reactive({
    username: '',
    email: '',
    password: ''
});

function registerOnSubmit(){
    console.log(user);
    if(user.username != '' && user.email != '' && user.password != ''){
        useAuthStore().register(user.username, user.email, user.password);
    }
}

function loginOnSubmit(){
    console.log(user);
    if(user.username != '' && user.password != ''){
        useAuthStore().login(user.username, user.password);
}};

</script>

<template>
    <input type="checkbox" id="go" aria-hidden="true">
    <div class="signup">
        <form id="registerForm" @submit.prevent="registerOnSubmit">
            <label for="go" aria-hidden="true">Registrieren</label>
            <div id="notification" style="display: none;"></div>
            <input type="text" name="user" placeholder="Username" v-model="user.username">
            <input type="email" name="email" placeholder="Email" v-model="user.email">
            <input type="password" name="pswd" placeholder="Password" v-model="user.password">
            <button  class="buttonSubmit" type="submit">Registrieren</button>
        </form>
    </div>
    <div class="login">
        <form id="loginForm" @submit.prevent="loginOnSubmit">
            <label for="go" aria-hidden="true">Anmelden</label>
            <input type="text" name="usermail" placeholder="Username/Email" v-model="user.username">
            <input type="password" name="pswd" placeholder="Password" v-model="user.password">
            <button class="buttonSubmit" type="submit">Anmelden</button>
            <button class="buttonSmall" type="submit">Passwort vergessen</button>
        </form>

    </div>
</template>

<style>



</style>