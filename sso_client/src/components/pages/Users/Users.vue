<script setup>
    import { onMounted, ref } from 'vue';
    import router from '@/router';
    import { getDefaultStore } from '@/stores/DefaultStore';
    // Services
    import AdministrationService from '@/services/AdministrationService';
    // Components
    import ContentWrapper from '@/components/wrapper/ContentWrapper.vue';


    let defaultStore = getDefaultStore();

    const users = ref([]);

    onMounted(async () => {
        try {
            let res = await AdministrationService.getAllUsers();
            users.value = res.data.sort((a, b) => { return a.username.localeCompare(b.username); });
        } catch (err) {
            console.log(err);
        }
    });

    async function deleteUser(user) {
        try {
            let res = await AdministrationService.deleteUserByUUID(user.uuid);
            users.value = users.value.filter(item => item.uuid !== res.data.uuid);
        } catch (err) {
            console.log(err);
        }
    }

    async function toggleUserBlocking(user) {
        let updateUser = {
            uuid: user.uuid,
            blocked: !user.blocked
        }

        try {
            let res = await AdministrationService.updateUser(updateUser);
            user.blocked = res.data.blocked;
        } catch (err) {
            console.log(err);
        }
    }

    function openProfile(user) {
        router.push({ name: 'Profile', params: { uuid: user.uuid }});
    }
</script>

<template>
    <ContentWrapper>
        <div class="flex flex-col justify-center">
            <div class="w-full"><span class="text-gray-500 mr-2">Location:</span><span class="h-8 px-2 text-secondary bg-primary-content rounded">SSO > Users</span></div>
            <br>
            <table class="border-separate border-spacing-y-2.5 text-black text-center">
                <thead>
                    <tr class="text-tertiary bg-tertiary-content rounded">
                        <th class="p-2 rounded-l">UUID</th>
                        <th class="p-2">Username</th>
                        <th class="p-2">E-Mail</th>
                        <th class="p-2">Created</th>
                        <th class="p-2">Access</th>
                        <th class="p-2 rounded-r">Delete</th>
                    </tr>
                </thead>
                <tbody v-if="users.length > 0">
                    <tr v-for="user in users" @click="openProfile(user)" class="bg-white hover:bg-primary-content cursor-pointer">
                        <td class="p-2 rounded-l font-source-code-pro">{{ user.uuid }}</td>
                        <td class="p-2">{{ user.username }}</td>
                        <td class="p-2">{{ user.email }}</td>
                        <td class="p-2">{{ user.created }}</td>
                        <td class="p-2 flex justify-center">
                            <button v-if="user.blocked" @click="$event.stopPropagation(); toggleUserBlocking(user);" class="h-8 w-8 flex justify-center items-center rounded bg-tertiary hover:bg-tertiary-focus active:bg-tertiary text-white rounded-full cstm-shadow">
                                <i class="fa fa-ban" />
                            </button>
                            <button v-if="!user.blocked" @click="$event.stopPropagation(); toggleUserBlocking(user);" class="h-8 w-8 flex justify-center items-center rounded bg-primary hover:bg-primary-focus active:bg-primary text-white rounded-full cstm-shadow">
                                <i class="fa fa-check" />
                            </button>
                        </td>
                        <td class="p-2 rounded-r">
                            <button @click="$event.stopPropagation(); deleteUser(user);" class="h-8 w-8 rounded bg-secondary hover:bg-secondary-focus active:bg-secondary text-white rounded-full cstm-shadow">
                                <i class="fa fa-trash" />
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan=6 class="text-center">
                            <button @click="router.push({ name: 'UserAdd' })" class="h-8 w-8 text-white bg-tertiary rounded-full text-secondary hover:bg-tertiary-focus active:bg-tertiary">
                                <span class="flex justify-center items-center"><i class="fa fa-plus" /></span>
                            </button>
                        </td>
                    </tr>
                </tbody>
                <tbody v-else>
                    <tr>
                        <td colspan=6 class="text-center">
                            <span>No Users Found</span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </ContentWrapper>
</template>