<template>
  <div class="app-container">
    <NavbarForm />

    <main class="app-main">
      <div class="p-8 w-1/2">
        <RouterView />
      </div>
    </main>

    <BleckitFooterBanner
      tagline="Mit ♥ gemacht"
      description="Schau dir doch meine anderen Projekte an!"
      link-text="Bleck IT"
      link-url="https://bleck.it"
      link-target="_blank"
      density="compact"
      position="static"
      :credits="credits"
      credits-label="Team:"
      :text-color="colors.primaryBlue"
      :secondary-text-color="colors.lightGray"
      :bg="'rgba(255, 255, 255, 0.02)'"
      :border="'rgba(59, 130, 246, 0.3)'"
      :blur-amount="'8px'"
      :btn="colors.primaryBlue"
      :btn-hover="colors.primaryBlueHover"
      :btn-bg="colors.primaryBlue"
      :btn-text="'white'"
      button-style="filled"
      :append-utm="true"
      utm-source="bleckit"
      utm-medium="ref-banner"
      utm-campaign="network"
    />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router';
import { useAuthStore } from './stores/auth';
import NavbarForm from './components/NavbarForm.vue';
import BleckitFooterBanner from './components/BleckitFooterBanner.vue';
import { colors } from '@/utils/colors';

const auth = useAuthStore();

const credits = [
  { name: 'Cedric Bleck', url: 'https://github.com/DenKedi', target: '_blank' },
  { name: 'Tommy Ngo', url: 'https://github.com/tommyngo98', target: '_blank' },
  { name: 'Ida Cyriax', url: 'https://github.com/ideycy', target: '_blank' },
] as const;
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-main {
  flex: 1;
  padding: 2rem 0;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}
</style>
