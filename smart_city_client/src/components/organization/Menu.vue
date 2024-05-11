<script setup>
    import { onMounted, ref } from 'vue';
    import { getDefaultStore } from '@/stores/DefaultStore';
    import router from '@/router/';
    // Packages
    import Emitter from 'tiny-emitter/instance';
    // Components
    import Button from '@/components/input/Button.vue';


    const defaultStore = getDefaultStore();
    
    onMounted(() => {
        if (localStorage.getItem('idtoken') !== null) {
            defaultStore.isLoggedIn = true;
        } else {
            defaultStore.isLoggedIn = false;
        }
    });

    function login() {
        window.location.href = 'http://10.0.1.103:8000/login?host=10.0.1.103:8001';
    }

    function logout() {
        Emitter.emit('logout');
    }

</script>

<template>
    <div class="fixed top-0 left-0 px-5 flex justify-between h-16 w-full bg-white cstm-shadow">
        <!-- Left -->
        <div class="h-full flex justify-start items-center">
            <div @click="router.push({ name: 'Home'});" class="h-10 w-10 mr-4 rounded-full border-4 border-dashed border-tertiary hover:border-secondary"></div>
            <button @click="router.push({ name: 'Home' });" class="relative h-full w-20 bg-transparent text-tertiary hover:bg-primary-content hover:cstm-shadow">
                <div class="absolute top-0 h-full w-full flex justify-center items-center">Sensors</div>
                <div class="absolute bottom-0 h-2 w-full rounded-t bg-primary"></div>
            </button>
        </div>
        <!-- Right -->
        <div class="h-full flex justify-start items-center gap-4">
            <Button v-if="!defaultStore.isAuthenticated" @click="login" class="bg-primary text-white hover:bg-primary-focus hover:text-white focus:bg-primary-focus">Login</Button>
            <Button v-if="defaultStore.isAuthenticated" @click="logout" class="bg-primary text-white hover:bg-primary-focus hover:text-white focus:bg-primary-focus">Logout</Button>
        </div>
    </div>
</template>