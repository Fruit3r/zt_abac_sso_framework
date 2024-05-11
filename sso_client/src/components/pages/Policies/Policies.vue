<script setup>
    import { onMounted, ref } from 'vue';
    import router from '@/router';
    // Modules
    import { showSuccessToast, showErrorToast } from '@/modules/Utils/ToastFunctions';
    // Services
    import AdministrationService from '@/services/AdministrationService';
    // Components
    import ContentWrapper from '@/components/wrapper/ContentWrapper.vue';
    import TextInput from '@/components/input/TextInput.vue';
    import AddRuleTag from '@/components/pages/Policies/components/AddRuleTag.vue';

    const policies = ref([]);
    const addPolicyForm = ref({
        newPolicy: ''
    });
    const addRuleForm = ref({
        showTag: false,
        newRule: '',
    });

    onMounted(() => {
        AdministrationService.getAllPolicies()
        .then((res) => {
            policies.value = res.data;
        }).catch((err) => { console.log(err); });
    });

    async function createPolicy(event) {
        event.preventDefault();

        let newPolicy = {
            name: addPolicyForm.value.newPolicy,
            rules: []
        };

        try {
            let res = await AdministrationService.createPolicy(newPolicy);
            policies.value.push(res.data);
            // Clear form
            addPolicyForm.value = {
                newPolicy: ''
            }
            // Show msg
            showSuccessToast('created new policy "' + res.data.name + '"');
        } catch (err) {
            console.log(err);
            showErrorToast('creating new policy failed');
        }
    }

    async function deletePolicy(policy) {
        try {
            let res = await AdministrationService.deletePolicyByUUID(policy.uuid);
            policies.value = policies.value.filter(item => item.uuid !== res.data.uuid);
            showSuccessToast('deleted policy "' + res.data.name + '"');
        } catch (err) {
            console.log(err);
            showErrorToast('deleting policy failed');
        }
    }

    async function deleteRule(policy, delRule) {
        const newRuleSet = policy.rules.filter(rule => rule !== delRule);

        let updatePolicy = {
            uuid: policy.uuid,
            rules: newRuleSet
        }

        try {
            let res = await AdministrationService.updatePolicy(updatePolicy);
            policy.rules = res.data.rules;
            showSuccessToast('deleted policy rule');
        } catch (err) {
            console.log(err);
            showErrorToast('deleting policy rule failed');
        }
    }

</script>

<template>
    <ContentWrapper>
        <div class="w-full"><span class="text-gray-500 mr-2">Location:</span><span class="h-8 px-2 text-secondary bg-primary-content rounded">SSO > Policies</span></div>
        <br>
        <br>
        
        <div class="w-full p-2 text-xl text-tertiary font-bold bg-secondary-content rounded-lg">Policies</div>
        
        <!-- Policy entries -->
        <div v-for="policy in policies" class="w-full bg-white p-4 mt-3 rounded-lg">
            <div class="w-full flex flex-row justify-between">
                <span>
                    <span class="mr-2 text-gray-500">Name:</span>
                    <span class="text-xl font-source-code-pro font-bold text-neutral-dark">{{ policy.name }}</span>
                </span>
                <span>
                    <button @click="deletePolicy(policy)" class="inline-block h-8 w-8 text-center text-white bg-secondary rounded-full hover:bg-secondary-focus hover:text-white active:bg-secondary">
                        <span><i class="fa fa-trash" /></span>
                    </button>
                </span>
            </div>
            <br>
            <hr class="border-t-2 border-dashed border-tertiary-content mb-4">
            <!-- Rule entries -->
            <div class="w-full text-left mb-2">
                <span class="mr-2 text-gray-500">Rules:</span>
            </div>
            <div class="flex flex-wrap">
                <div v-for="rule in policy.rules" class="inline-block h-8 px-3 bg-primary-content mr-3 mb-3 rounded-full">
                    <div class="inline-block flex items-center h-full">
                        <span class="text-tertiary font-source-code-pro mr-1">{{ rule }} </span>
                        <button @click="deleteRule(policy, rule)" class="text-primary hover:text-gray-600"><i class="fa fa-times"/></button>
                    </div>
                </div>
                <!-- Add rule tag-->
                <AddRuleTag :policy="policy"/>
            </div>
        </div>

        <!-- Add policy entry -->
        <div class="p-4 mt-3 rounded-lg bg-white">
            <form @submit="createPolicy($event)" class="flex flex-row justify-between items-center">
                <TextInput v-model:value="addPolicyForm.newPolicy" label="Name:" class="inline-block w-72 font-source-code-pro"/>
                <button class="inline-block h-8 px-3 text-center bg-tertiary rounded text-white hover:bg-tertiary-focus active:bg-tertiary">
                    <i class="fa fa-plus" /> Add Policy
                </button>
            </form>
        </div>
    </ContentWrapper>
</template>