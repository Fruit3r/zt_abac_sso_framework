<script setup>
    import { onMounted, ref } from 'vue';
    import { getDefaultStore } from '@/stores/DefaultStore';
    import router from '@/router';
    // Services
    import AdministrationService from '@/services/AdministrationService';
    // Components
    import ContentWrapper from '@/components/wrapper/ContentWrapper.vue';
import Profile from '../Profile/Profile.vue';


    let defaultStore = getDefaultStore();

    onMounted(() => {
    });

    function isAdministrator() {
        return defaultStore.user && defaultStore.user.attributes && (defaultStore.user.attributes.role === 'root' ||defaultStore.user.attributes.administrator === true);
    }

</script>

<template>
    <ContentWrapper v-if="isAdministrator()">
        <div class="w-full"><span class="text-gray-500 mr-2">Location:</span><span class="h-8 px-2 text-secondary bg-primary-content rounded">SSO > Home</span></div>
        <br>
        <br>
        <br>
        <br>
        <div class="w-[30rem] p-3 rounded bg-white">
            <p class="flex items-center"><span class="mr-2 text-gray-500">Welcome</span><span class="text-[18px]">{{  defaultStore.user.username }}</span></p>
            <p><span class="mr-2 text-gray-500">Your UUID:</span><span>{{  defaultStore.user.uuid }}</span></p>
        </div>

        <br>
        <br>
        <div class="flex flex-col justify-center gap-5">
            <div class="flex flex-row justify-center gap-10">    
                <div @click="router.push({ name: 'UserList' })" class="h-52 w-full cursor-pointer bg-secondary-content text-tertiary hover:bg-tertiary-content rounded cstm-shadow ease-in duration-100">
                    <div class="h-full w-full flex justify-center items-center p-4">
                        <i class="fa fa-user h-12 w-12 mr-4" />
                        <span class="text-2xl">Manage SSO Users</span>
                    </div>
                </div>
                <div @click="router.push({ name: 'Policies' })" class="h-52 w-full cursor-pointer bg-secondary-content text-tertiary hover:bg-tertiary-content rounded cstm-shadow ease-in duration-100">
                    <div class="h-full w-full flex justify-center items-center p-4">
                        <i class="fa fa-landmark h-12 w-12 mr-4" />
                        <span class="text-2xl">Manage SSO Policies</span>
                    </div>
                </div>
                <div @click="router.push({ name: 'Profile' })" class="h-52 w-full cursor-pointer bg-secondary-content text-tertiary hover:bg-tertiary-content rounded cstm-shadow ease-in duration-100">
                    <div class="h-full w-full flex justify-center items-center p-4">
                        <i class="fa fa-id-card h-12 w-12 mr-4" />
                        <span class="text-2xl">Show Profile</span>
                    </div>
                </div>
            </div>
            
        </div>
    </ContentWrapper>
    <Profile v-else />
</template>