<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth';
import { reactive } from 'vue';

const auth = useAuthStore();
const user = reactive({
    username: '',
    email: '',
    password: ''
});

let usernameMessage: string;
let emailMessage: string;
let passwordMessage: string;

async function registerOnSubmit(){

    document.getElementById('emailSmall').innerText = ' ';
    document.getElementById('usernameSmall').innerText = ' ';
    document.getElementById('passwordSmall').innerText = ' ';

    let usernameMessage = null;
    let emailMessage = null;
    let passwordMessage = null;

    console.log(user);
    if(user.username != '' && user.email != '' && user.password != ''){
        if (user.email.includes('@') && user.email.includes('.')){
            const message = await useAuthStore().register(user.username, user.email, user.password);
            if(message == 'Username already exists'){
                usernameMessage = message;
        } else if(message == 'Email already exists'){
            emailMessage = message;
        } else if(message == 'Password requires 6 or more characters.'){
            passwordMessage = message;
        } else if(message == 'Special characters required in Password'){
            passwordMessage = message;
        } else if(message == 'Numbers required in Password'){
            passwordMessage = message;
        } else if(message == 'Username requires 3 or more characters.'){
            usernameMessage = message;
        } else if(message == 'Special characters not allowed in Username (except for \'_\' and \'-\')'){
            usernameMessage = message;
        } else if(message == 'Forbidden Username') {
            usernameMessage = message;
        }
        
    } else{
        emailMessage = 'Email is not valid';
    }
}else{
    if(user.username == ''){
        highlightInput('registerUsernameInput')
    } if(user.email == ''){
        highlightInput('registerEmailInput')
    } if(user.password == ''){
        highlightInput('registerPasswordInput')
    }

}

    if(emailMessage){
            document.getElementById('emailSmall').innerText = emailMessage;
        }
        if(usernameMessage){
            document.getElementById('usernameSmall').innerText = usernameMessage;
        }
        if(passwordMessage){
            document.getElementById('passwordSmall').innerText = passwordMessage;
        }
}


async function loginOnSubmit(){
    document.getElementById('loginSmall').innerText = '';
    console.log(user);
    if(user.username != '' && user.password != ''){
      const message = await useAuthStore().login(user.username, user.password);
        if(message == 'Username or Password wrong'){
           document.getElementById('loginSmall').innerText = message;
        }
} else{
    if(user.username == ''){
        highlightInput('loginUsermailInput')
    } if(user.password == ''){
        highlightInput('loginPasswordInput')
    }
}
};

function highlightInput(id: string) {
        var inputField = document.getElementById(id);
        
        // Ändere die Hintergrundfarbe auf Rot
        inputField.style.backgroundColor = 'rgba(255, 182, 193, 0.9)'; // pastel red

        // Setze die Hintergrundfarbe nach 500 Millisekunden zurück (0,5 Sekunden)
        setTimeout(function() {
            inputField.style.backgroundColor = '';
        }, 500);
    }


</script>

<template>
    <input type="checkbox" id="go" aria-hidden="true">
    <div class="signup">
        <form id="registerForm" @submit.prevent="registerOnSubmit">
            <div class="formFrame">
                
            <label class="labellogin" for="go" aria-hidden="true">Registrieren</label>
            
            <small id="usernameSmall"></small>
            <input id="registerUsernameInput" type="text" name="user" placeholder="Username" v-model="user.username">
            <small id="emailSmall"></small>
            <input id="registerEmailInput" type="text" name="email" placeholder="Email" v-model="user.email">
            <small id="passwordSmall"></small>
            <input id="registerPasswordInput" type="password" name="pswd" placeholder="Password" v-model="user.password">
            <button class="buttonBig" type="submit">Registrieren</button>
            <small v-if="auth.logoutMessage" class="logout-message">{{ auth.logoutMessage }}</small>
            </div>
            
        </form>
    </div>
    <div class="login">
        <form id="loginForm" @submit.prevent="loginOnSubmit">
            <label class="labellogin" for="go" aria-hidden="true">Anmelden</label>

            <small id="loginSmall"></small>
            <input id="loginUsermailInput" type="text" name="usermail" placeholder="Username/Email" v-model="user.username">
            <small id="passwordSmall"></small>
            <input id="loginPasswordInput" type="password" name="pswd" placeholder="Password" v-model="user.password">
            <button class="buttonBig" type="submit">Anmelden</button>
            
        </form>

    </div>
</template>

<style>



</style>