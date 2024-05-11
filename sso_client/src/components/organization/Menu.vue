<script setup>
    import { onMounted, ref } from 'vue';
    import { getDefaultStore } from '@/stores/DefaultStore';
    import router from '@/router/';
    // Components
    import Button from '@/components/input/Button.vue';


    const defaultStore = getDefaultStore();
    

    function logout() {
        defaultStore.user = {};
        document.cookie = 'idtoken=; Max-Age=0';
        router.push({ name: 'Login' });
    }

    function isAdministrator() {
        return defaultStore.user && defaultStore.user.attributes && (defaultStore.user.attributes.role === 'root' ||defaultStore.user.attributes.administrator === true);
    }
</script>

<template>
    <div v-if="defaultStore.userIsSet" class="fixed top-0 left-0 px-5 flex justify-between h-16 w-full bg-white cstm-shadow">
        <!-- Left -->
        <div class="h-full flex justify-start items-center">
            <div @click="router.push({ name: 'Home'});" class="h-10 w-10 mr-4 rounded-full border-4 border-dashed border-tertiary hover:border-secondary"></div>
            <button @click="router.push({ name: 'Home' });" class="relative h-full w-20 bg-transparent text-tertiary hover:bg-primary-content hover:cstm-shadow">
                <div class="absolute top-0 h-full w-full flex justify-center items-center">Home</div>
                <div v-if="defaultStore.menu === 'Home'" class="absolute bottom-0 h-2 w-full rounded-t bg-primary"></div>
            </button>
            <button v-if="isAdministrator()" @click="router.push({ name: 'UserList' });" class="relative h-full w-20 bg-transparent text-tertiary hover:bg-primary-content hover:cstm-shadow">
                <div class="absolute top-0 h-full w-full flex justify-center items-center">Users</div>
                <div v-if="defaultStore.menu === 'UserList' || defaultStore.menu === 'UserAdd'" class="absolute bottom-0 h-2 w-full rounded-t bg-primary"></div>
            </button>
            <button v-if="isAdministrator()" @click="router.push({ name: 'Policies' });" class="relative h-full w-20 bg-transparent text-tertiary hover:bg-primary-content hover:cstm-shadow">
                <div class="absolute top-0 h-full w-full flex justify-center items-center">Policies</div>
                <div v-if="defaultStore.menu === 'Policies'" class="absolute bottom-0 h-2 w-full rounded-t bg-primary"></div>
            </button>
            <button v-if="isAdministrator()" @click="router.push({ name: 'Profile' });" class="relative h-full w-20 bg-transparent text-tertiary hover:bg-primary-content hover:cstm-shadow">
                <div class="absolute top-0 h-full w-full flex justify-center items-center">Profile</div>
                <div v-if="defaultStore.menu === 'Profile'" class="absolute bottom-0 h-2 w-full rounded-t bg-primary"></div>
            </button>
        </div>
        <!-- Right -->
        <div class="h-full flex justify-start items-center gap-4">
            <Button @click="logout" class="bg-primary text-white hover:bg-primary-focus hover:text-white focus:bg-primary-focus">Logout</Button>
        </div>
    </div>
</template>