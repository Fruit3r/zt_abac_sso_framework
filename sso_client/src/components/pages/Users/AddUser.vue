<script setup>
    import { ref } from 'vue';
    // Modules
    import { showSuccessToast, showErrorToast } from '@/modules/Utils/ToastFunctions';
    // Services
    import AdministrationService from '@/services/AdministrationService';
    // Components
    import ContentWrapper from '@/components/wrapper/ContentWrapper.vue';
    import TextInput from '@/components/input/TextInput.vue';
    import Button from '@/components/input/Button.vue';
import router from '../../../router';

    const addUserForm = ref({
        username: '',
        email: '',
        password: '',
    });

    function onSubmit(event) {
        event.preventDefault();
        
        let newUser = {
            username: addUserForm.value.username,
            email: addUserForm.value.email,
            password: addUserForm.value.password
        }

        console.log(newUser);

        AdministrationService.createUser(newUser)
        .then((res) => {
            showSuccessToast('created new user ' + newUser.username);
            addUserForm.value = {
                username: '',
                email: '',
                password: ''
            }
            router.push({ name: 'Users' });
        }).catch((err) => {
            console.log(err);
            let status = err.response.data.status;
            
            if (status === 406) {
                showErrorToast('invalid new user data');
            } else if (status === 409) {
                showErrorToast('user already exists');
            } else {
                showErrorToast('Unkown error occured. Please try again later');
            }
        })
    }
</script>

<template>
    <ContentWrapper>
        <div class="w-full"><span class="text-gray-500 mr-2">Location:</span><span class="h-8 px-2 text-secondary bg-primary-content rounded">SSO > Users</span></div>
        <br>
        <br>
        <div class="flex justify-center">
            <div class="w-[45rem] bg-white py-4 px-6 rounded-lg">
            <span class="text-xl text-gray-500">New User</span>
            <div class="h-4"></div>
            <form @submit="onSubmit($event)">
                <TextInput v-model:value="addUserForm.username" label="Username:" info="Max. 100 characters" class="block w-full" />
                <br>
                <TextInput v-model:value="addUserForm.email" label="E-Mail:" info="Bsp.: username@example.com" class="block w-full" />
                <br>
                <TextInput v-model:value="addUserForm.password" label="Password:" info=" " class="block w-full" />
                <br>
                <div class="text-center">
                    <Button class="h-8 w-8 bg-tertiary hover:bg-tertiary-focus active:bg-tertiary text-white rounded-full">
                        <span class="flex justify-center items-center"><i class="fa fa-plus" /></span>
                    </Button>
                </div>
            </form>
        </div>
        </div>
    </ContentWrapper>
</template>