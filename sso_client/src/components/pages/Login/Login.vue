<script setup>
    import { ref, onMounted } from 'vue';
    import { useRoute } from 'vue-router'
    import router from '@/router';
    import { getDefaultStore } from '@/stores/DefaultStore';
    // Modules
    import settings from '@/config/settings.json';
    import { showSuccessToast, showErrorToast } from '@/modules/Utils/ToastFunctions';
    // Services
    import AuthenticationService from '@/services/AuthenticationService';
    import AdministrationService from '@/services/AdministrationService';
    // Components
    import TextInput from '@/components/input/TextInput.vue';
    import Button from '@/components/input/Button.vue';


    const input = ref({
        username: '',
        password: ''
    })

    let defaultStore;
    let targetHost;

    onMounted(() => {
        defaultStore = getDefaultStore();
        const route = useRoute();

        targetHost = route.query.host ? route.query.host : '';
    });

    function authenticate(event) {
        event.preventDefault();

        AuthenticationService.authenticate(input.value.username, input.value.password)
        .then((res) => {
            //let idToken = res.data;
            // Store client side cookie
            //document.cookie = 'idtoken=' + idToken;
            // Route to target
            if (targetHost) {
                window.location.href = 'http://' + targetHost;
            } else {
                router.push({ name: 'Home' });
            }
        }).catch((err) => {
            let msg = '';
            console.log(err);
            if (err.response) {
                if (err.response.status === 401) {
                    // Bad credentials
                    msg = 'Your username or password is wrong';
                } else if (err.response.status === 403) {
                    // User blocked
                    msg = 'Your account is blocked';
                } else {
                    msg = 'Internal server error';
                }
            } else {
                msg = 'Unable to reach SSO servers';
            }

            if (msg.length > 0)  {
                showErrorToast(msg);
            } 
        });
    }

</script>

<template>
    <div class="h-screen w-screen flex justify-center items-center">
        <div class="h-[30rem] w-[25rem] px-12 rounded-lg cstm-shadow bg-white">
            <div class="flex justify-center w-full mt-10 mb-10">
                <div class="h-32 w-32 rounded-full border-4 border-dashed border-tertiary"></div>
            </div>
            <form @submit="authenticate($event)">
                <TextInput name="username" label="Username" v-model:value="input.username" placeholder="Your Username" class="block w-full mb-4"/>
                <TextInput name="password" label="Password" v-model:value="input.password" placeholder="Your Password" password="true" class="block w-full mb-10" />
                <div class="mt-3 text-center">
                    <Button class="w-28 text-white bg-primary hover:bg-primary-focus focus:bg-primary">Sign-In</Button>
                </div>
            </form>
        </div>
    </div>
</template>