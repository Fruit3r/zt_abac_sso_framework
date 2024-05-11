import { createRouter, createWebHistory } from 'vue-router'
import { getDefaultStore } from '@/stores/DefaultStore'; 
// Modules
import { showSuccessToast, showErrorToast } from '@/modules/Utils/ToastFunctions';
// Services
import AdministrationService from '@/services/AdministrationService';
// Components
import Home from '@/components/pages/Home/Home.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    }
  ]
})

router.beforeEach((to, from, next) => {
  next();
});


export default router;
