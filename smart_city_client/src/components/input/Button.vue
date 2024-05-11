<script setup>

    defineEmits(['click'])

</script>

<template>
    <button @click="$emit('click')" class="inline-block h-8 px-2 rounded cstm-shadow">
        <slot></slot>
    </button>
</template>