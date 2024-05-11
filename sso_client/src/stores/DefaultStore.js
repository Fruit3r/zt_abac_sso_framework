import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const getDefaultStore = defineStore({
    id: 'defaultStore',
    state: () => ({
        user: {},
        menu: 'Home'
    }),
    getters: {
        userIsSet(state) {
            return state.user.uuid ? true :false;  
        },
    },
})