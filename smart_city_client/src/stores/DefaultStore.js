import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const getDefaultStore = defineStore({
    id: 'defaultStore',
    state: () => ({
        isLoggedIn: false,
        authState: '',
        menu: 'Home',
    })
})