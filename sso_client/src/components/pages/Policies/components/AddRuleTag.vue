<script setup>
    import { ref } from 'vue';
    // Modules
    import { showSuccessToast, showErrorToast } from '@/modules/Utils/ToastFunctions';
    // Services
    import AdministrationService from '@/services/AdministrationService';
    // Components
    import TextInput from '@/components/input/TextInput.vue';

    const props = defineProps({
        policy: ''
    });

    const addRuleForm = ref({
        showTag: false,
        newRule: '',
    });

    function createRule(event) {
        event.preventDefault();
        let policy = props.policy;
        let newRuleSet = policy.rules.concat([addRuleForm.value.newRule]);


        let updatePolicy = {
            uuid: policy.uuid,
            rules: newRuleSet
        };

        AdministrationService.updatePolicy(updatePolicy)
        .then((res) => {
            props.policy.rules = newRuleSet;
            // Clear add rule form
            addRuleForm.value = {
                showTag: false,
                newRule: ''
            }
            showSuccessToast('created new policy rule');
        }).catch((err) => {
            console.log(err);
            showErrorToast('creation of policy rule failed');
        });
    }

    function toggleAddRuleTag() {
        addRuleForm.value.showTag = !addRuleForm.value.showTag;
    }
</script>

<template>
    <div class="inline-block h-8 bg-primary-content mr-3 rounded-full">
        <div class="inline-block h-full flex items-center">
            <span v-if="addRuleForm.showTag" class="pl-3">
                <form @submit="createRule($event)" class="flex items-center">
                    <TextInput v-model:value="addRuleForm.newRule" class=" w-72 h-5 bg-white rounded-none font-source-code-pro " />
                    <button type="submit" class="text-primary hover:text-tertiary mx-3"><i class="fa fa-check"/></button>
                </form>
            </span>
            <button @click="toggleAddRuleTag" class="inline-block h-8 w-8 text-white bg-tertiary rounded-full text-secondary hover:bg-tertiary-focus active:bg-secondary">
                <span v-if="!addRuleForm.showTag" class="flex justify-center items-center"><i class="fa fa-plus" /></span>
                <span v-if="addRuleForm.showTag" class="flex justify-center items-center"><i class="fa fa-times" /></span>
            </button>
        </div>
    </div>
</template>
