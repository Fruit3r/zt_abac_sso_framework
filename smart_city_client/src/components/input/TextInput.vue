<script setup>

    import { onMounted } from 'vue';

    const props = defineProps({
        value: '',
        password: false,
        name: '',
        label: '',
        info: '',
        placeholder:'',
        class: ''
    });

    const emit = defineEmits(['update:value']);

    onMounted(() => {});

    
</script>

<template>
    <div class="">
        <label v-if="props.label" class="text-neutral-light mr-2 mb-1">{{ label }}</label>
        <input @input="emit('update:value', $event.target.value);" :type="props.password ? 'password' : 'text'" :name="name" :value="value" :placeholder="placeholder"  :class="'h-7 px-1 rounded bg-gray-100 text-black outline-none ' + props.class" />
        <label v-if="props.info" class="text-neutral-light mb-1 text-sm ml-2">{{ info }}</label>
    </div>
</template>


