<script setup>
    import { onMounted, ref } from 'vue';
    import { useRoute } from 'vue-router';
    import { getDefaultStore } from '@/stores/DefaultStore';
    // Modules
    import { showSuccessToast, showErrorToast } from '@/modules/Utils/ToastFunctions';
    // Services
    import AdministrationService from '@/services/AdministrationService';
    // Components
    import ContentWrapper from '@/components/wrapper/ContentWrapper.vue';
    import TextInput from '@/components/input/TextInput.vue';

    const route = useRoute();
    const store = getDefaultStore();

    let user = ref({});
    const addAttributeForm = ref({
        showTag: false,
        newAttribute: '',
        newValue: '',
        newType: 'string'
    });

    onMounted(() => {
        // Check if user is specified
        let uuid = route.params.uuid;
        if (uuid) {
            // Fetch user 
            AdministrationService.getUserByUUID(uuid)
            .then((res) => {
                let tmpUser = res.data;
                tmpUser.attributes = JSON.parse(tmpUser.attributes);
                user.value = tmpUser;
            }).catch((err) => { console.log(err); });
        } else {
            // Use authenticated user
            user.value = store.user;
            console.log(user.value.attributes);
        }

    });

    /**
     * Removes an attribute by the given attribute key (name)
     * 
     * @param key   Name of the attribute
     */
    function removeAttribute(key) {
        let delAttribute = {};
        delAttribute[key] = null;

        let updateUser = {
            uuid: user.value.uuid,
            attributes: JSON.stringify(delAttribute)
        }

        AdministrationService.updateUser(updateUser)
        .then((res) => {
            let tmpUser = res.data;
            tmpUser.attributes = JSON.parse(tmpUser.attributes);
            user.value = tmpUser;
        }).catch((err) => { console.log(err); });
    }

    /**
     * Add attribute 
     * 
     * @param event Submit event of the form for the new attribute 
     */
    function addAttribute(event) {
        event.preventDefault();

        let newAttribute = {};
        switch (addAttributeForm.value.newType) {
            case 'boolean':
                let boolValue = parseBool(addAttributeForm.value.newValue);
                console.log(boolValue)
                if (boolValue === undefined) {
                    showErrorToast('Invalid bool value used for attribute value. Please use "true" or "false"!');
                    return;
                } else {
                    newAttribute[addAttributeForm.value.newAttribute] = boolValue;
                }
                break;
            case 'number':
                let numValue = parseInt(addAttributeForm.value.newValue);
                if (!numValue) {
                    showErrorToast('Invalid number value used for attribute value. Please use a valid number!');
                    return;
                } else {
                    newAttribute[addAttributeForm.value.newAttribute] = numValue;
                }
                break;
            default:
                newAttribute[addAttributeForm.value.newAttribute] = addAttributeForm.value.newValue;
        }

        let updateUser = {
            uuid: user.value.uuid,
            attributes: JSON.stringify(newAttribute)
        }

        AdministrationService.updateUser(updateUser)
        .then((res) => {
            let tmpUser = res.data;
            tmpUser.attributes = JSON.parse(tmpUser.attributes);
            user.value = tmpUser;
            addAttributeForm.value = {
                showTag: false,
                newAttribute: '',
                newValue: '',
                newType: 'string'
            };
        }).catch((err) => { 
            console.log(err); 
            if (err.response) {
                if (err.response.status === 401) {
                    showErrorToast('Missing permissions');
                }
            } else {
                showErrorToast('Unable to reach SSO servers');
            }
        });
    }

    /**
     * Open or close add attribute form tag
     */
    function toggleAddAttributeTag() {
        addAttributeForm.value.showTag = !addAttributeForm.value.showTag;
    }

    function parseBool(value) {
        return value === 'true' ? true : (value === 'false' ? false : undefined)
    }

    /**
     * Returns if value is type of string
     * 
     * @param value     Value to check
     */
    function isString(value) {
        return typeof value === 'string';
    }

    /**
     * Returns if value is type of number
     * 
     * @param value     Value to check
     */
    function isNumber(value) {
        return typeof value === 'number';
    }

    /**
     * Returns if value is type of boolean
     * 
     * @param value     Value to check
     */
     function isBoolean(value) {
        console.log(typeof value);
        return typeof value === 'boolean';
    }
</script>

<template>
     <ContentWrapper>
        <div class="w-full"><span class="text-gray-500 mr-2">Location:</span><span class="h-8 px-2 text-secondary bg-primary-content rounded">SSO > Profile</span></div>
        <br>
        <br>
        <div class="relative w-full px-4 pb-6 bg-white rounded-lg">
            <div class="flex justify-between p-3 text-sm">
                <span class="text-xl text-gray-500">Profile</span>
                <div><span class="text-gray-500">UUID: </span><span>{{ user.uuid }}</span></div>
            </div>
            <div class="flex flex-row justify-start items-center gap-6">
                <div class="flex justify-center items-center h-36 w-36 bg-secondary-content rounded-full "><span class="text-tertiary"><i class="far fa-user h-20 w-20"/></span></div>
                <div class="text-center">
                    <div class="text-sm text-gray-500">Username</div>
                    <div>{{ user.username }}</div>
                </div>
                <div class="text-center">
                    <div class="text-sm text-gray-500">E-Mail</div>
                    <div>{{ user.email }}</div>
                </div>
                <div class="text-center">
                    <div class="text-sm text-gray-500">Created</div>
                    <div>{{ user.created }}</div>
                </div>
                <div class="text-center">
                    <div class="text-sm text-gray-500">Blocked</div>
                    <div>{{ user.blocked }}</div>
                </div>
            </div>
        </div>
        <br>
        <div class="relative w-full p-3 bg-white rounded-lg">
            <div class="text-xl text-gray-500">Attributes</div>
            <div class="flex flex-wrap py-4">
                <!-- Tag entries -->
                <div v-for="(item, key) in user.attributes" class="inline-block h-8 px-3 bg-primary-content mr-3 mb-3 rounded-full">
                    <div class="inline-block flex items-center h-full">
                        <span class="text-secondary mr-1">{{ key }} :</span>
                        <span v-if="isString(item)" class="text-tertiary mr-2">"{{ item }}"</span>
                        <span v-if="isNumber(item) || isBoolean(item)" class="text-tertiary mr-2">{{ item }}</span>
                        <button @click="removeAttribute(key)" class="text-primary hover:text-gray-600"><i class="fa fa-times"/></button>
                    </div>
                </div>
                <!-- Add tag entry -->
                <div class="inline-block h-8 bg-primary-content mr-3 rounded-full">
                    <div class="inline-block flex items-center h-full">
                        <span v-if="addAttributeForm.showTag" class="pl-3">
                            <form @submit="addAttribute($event)" class="flex items-center">
                                <TextInput v-model:value="addAttributeForm.newAttribute" class="w-28 bg-white"/>
                                <span class="mx-2">:</span>
                                <TextInput v-model:value="addAttributeForm.newValue" class="w-28 bg-white"/>
                                <select v-model="addAttributeForm.newType" class="h-7 p-0 px-1 rounded ml-2">
                                    <option value="string" class="pt-0">String</option>
                                    <option value="number" class="pt-0">Number</option>
                                    <option value="boolean" class="pt-0">Boolean</option>
                                </select>
                                <button type="submit" class="text-primary hover:text-tertiary mx-3"><i class="fa fa-check"/></button>
                            </form>
                        </span>
                        <button @click="toggleAddAttributeTag" class="inline-block h-8 w-8 text-center bg-primary-content rounded-full bg-tertiary text-white hover:bg-tertiary-focus ease-in duration-150 active:bg-secondary">
                            <span v-if="!addAttributeForm.showTag" class="flex justify-center items-center"><i class="fa fa-plus" /></span>
                            <span v-if="addAttributeForm.showTag" class="flex justify-center items-center"><i class="fa fa-times" /></span>
                        </button>
                    </div>
                </div>
                
            </div>
        </div>
    </ContentWrapper>
</template>