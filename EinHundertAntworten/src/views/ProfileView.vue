<script lang="ts" setup>
import NavbarForm from '../components/NavbarForm.vue';
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';

const auth = useAuthStore();
const token = auth.token;
const data = ref<any>();

</script>

<template>
  <NavbarForm />

  <div class="center" >
    <div class="card">
      <div class="additional">
        <div class="user-card" v-if="data">
          <div class="level center">Level 13</div>
          <div class="points center">Score: {{ data.value }}</div>
        </div>
        <div class="more-info">
          <h1>{{ data?.firstName }} {{ data?.lastName }}</h1>
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
        <h1>{{ data?.firstName }} {{ data?.lastName }}</h1>
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
