import { createRouter, createWebHistory } from 'vue-router'
import { getDefaultStore } from '@/stores/DefaultStore'; 
// Modules
import { showSuccessToast, showErrorToast } from '@/modules/Utils/ToastFunctions';
// Services
import AdministrationService from '@/services/AdministrationService';
// Components
import Login from '@/components/pages/Login/Login.vue';
import Home from '@/components/pages/Home/Home.vue';
import Profile from '@/components/pages/Profile/Profile.vue';
import Users from '@/components/pages/Users/Users.vue';
import AddUser from '@/components/pages/Users/AddUser.vue';
import Policies from '@/components/pages/Policies/Policies.vue';
import AuthFrame from '@/components/pages/AuthFrame/AuthFrame.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/profile/:uuid?',
      name: 'Profile',
      component: Profile
    },
    {
      path: '/users',
      name: 'Users',
      children: [
        {
          path: '/',
          name: 'UserList',
          component: Users
        },
        {
          path: 'new',
          name: 'UserAdd',
          component: AddUser
        }
      ]
    },
    {
      path: '/policies',
      name: 'Policies',
      component: Policies
    },
    { 
      path: '/authframe',
      name: 'AuthFrame',
      component: AuthFrame
    }
  ]
})

router.beforeEach((to, from, next) => {

  const defaultStore = getDefaultStore();

  if (to.name === 'AuthFrame') {
    next();
  } else {
    if (!userIsAuthenticated()) {
      console.log('user is set: ' + defaultStore.userIsSet);
      console.log('user is authenticated: ' + userIsAuthenticated());
      navigateToLogin(next, to);
    } 
    
    // Fetch user
    if (!defaultStore.userIsSet && to.name !== 'Login') {
      AdministrationService.getAuthenticatedUser()
      .then((res) => {
        // Save authenticated user
        let user = res.data;
        user.attributes = JSON.parse(user.attributes);
        defaultStore.user = user;
        
        console.log('user is set: ' + defaultStore.userIsSet);
        console.log('user is authenticated: ' + userIsAuthenticated());
        defaultStore.menu = to.name;
        next();
      }).catch((err) => { 
        if (err.response) {
            console.log(err.response.data.message);
            if (err.response.data === 400) {
                showErrorToast('You are not logged in');
            } else if (err.response.status === 401) {
                showErrorToast('Missing permissions');
            } else if (err.response.status === 404) {
                showErrorToast('Your account has been deleted');
            } else {
                showErrorToast('Internal server error');
            }
        } else {
            showErrorToast('Unable to reach SSO servers');
        }
        navigateToLogin(next, to);
      });
    } else {
      console.log('user is set: ' + defaultStore.userIsSet);
      console.log('user is authenticated: ' + userIsAuthenticated());
      defaultStore.menu = to.name;
      next();
    }
  }  
});


function userIsAuthenticated() {
  return getClientCookie('idtoken') ? true : false;
}

function navigateToLogin(next, to) {
  if (to.name !== 'Login') {
    next({ name: 'Login' });
  } else {
    next();
  }
}

function getClientCookie(name) {
  let cookie = {};
  document.cookie.split(';').forEach(function(item) {
    let [key, value] = item.split('=');
    cookie[key.trim()] = value;
  });
  return cookie[name];
}

export default router;
