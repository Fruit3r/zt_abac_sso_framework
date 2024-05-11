<script lang="ts" setup>

  import { RouterView } from 'vue-router';
  // Components
  import Menu from '@/components/organization/Menu.vue';

</script>

<template>
  <div class="h-screen w-screen bg-neutral font-poppins overflow-y-scroll">
    <Menu />
    <RouterView />
  </div>
</template>