<script setup>
    import { onMounted, ref } from 'vue'; 
    import { useRoute } from 'vue-router';
    import { getDefaultStore } from '@/stores/DefaultStore';
    import router from '@/router';
    import { RouterView } from 'vue-router';
    // Packages
    import Emitter from 'tiny-emitter/instance';
    // Components
    import Menu from '@/components/organization/Menu.vue';


    let defaultStore = getDefaultStore();
    let loading = ref(true);
    let authFrame;

    onMounted(() => {
        Emitter.on('maintain', () => { console.log('Received authentication maintain event'); authFrame.contentWindow.postMessage('maintain', '*'); });
        Emitter.on('logout', () => { console.log('Received logout event'); authFrame.contentWindow.postMessage('logout', '*'); });
    });

    window.addEventListener('message', (event) => {
        let data = event.data.split('#');
        let status = parseInt(data[0]);
        let msg = data[1] + (data[2] ? data[2] : '');
        
        console.log('recv: status:' + status + '; msg: ' + msg);

        if (status === 0) {
            // Logout successfull remove idtoken
            localStorage.removeItem('idtoken')
            defaultStore.isAuthenticated = false;
        } else if (status === 200) {
            // Set idtoken
            localStorage.setItem('idtoken', msg);
            defaultStore.isAuthenticated = true;
        } else if (status === 500) {
            // Server error
            console.log('Unknown server error - you have been logged out for security reasons')
            defaultStore.isAuthenticated = false;
        } else {
            // Remove idtoken
            localStorage.removeItem('idtoken')
            defaultStore.isAuthenticated = false;
            // Set error message
            console.log(msg);
        }

        setTimeout(() => { loading.value = false; }, 3000);
    });

    function initialAuthentication() {
        authFrame = document.getElementById('authframe');
        authFrame.contentWindow.postMessage('maintain', '*');
    }
</script>

<template>
  <div class="h-screen w-screen bg-neutral font-poppins overflow-y-scroll">
    <div v-if="loading">
      <br>
      <br>
      <div class="w-full text-center">
        <div><span class="text-xl">Waiting for iframe to respond with authentication status</span></div>
        <br>
        <div class="inline-block h-10 w-10 animate-spin rounded-full border-4 border-solid border-current border-r-transparent align-[-0.125em] text-primary motion-reduce:animate-[spin_1.5s_linear_infinite]" role="status">
          <span class="!absolute !-m-px !h-px !w-px !overflow-hidden !whitespace-nowrap !border-0 !p-0 ![clip:rect(0,0,0,0)]"></span>
        </div>
      </div>
    </div>
    <div v-else>
      <Menu />
      <RouterView  />
    </div>
    <iframe src="http://10.0.1.103:8000/authframe?host=http://10.0.1.103:8001" id="authframe" @load="initialAuthentication()" class="hidden"></iframe>
  </div>
</template>