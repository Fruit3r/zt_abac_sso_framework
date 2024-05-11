<script setup>
    import { onMounted } from 'vue';
    import { useRoute } from 'vue-router';
    // Services
    import AuthenticationService from '@/services/AuthenticationService.js';
    // Components
    import ContentWrapper from '@/components/wrapper/ContentWrapper.vue';

    let host;

    window.addEventListener('message', async (event) => {
        let httpStatus, msg;

        if (event.data === 'maintain') {
            // Maintain token
            let response;
            try {
                response = await AuthenticationService.maintain();
                // Return current token
                httpStatus = response.status;
                msg = getCookie('idtoken');                
            } catch (err) {
                httpStatus = err.response.status;
                msg = err.response.data.message;
            }
        } else {
            // Logout
            removeCookie('idtoken');
            httpStatus = '000';
            msg = 'logout';
        }

        // Respond
        window.parent.postMessage(httpStatus + '#' + msg,'*');
    });

    function getCookie(name) {
        let cookie = {};
        document.cookie.split(';').forEach(function(el) {
            let [key,value] = el.split('=');
            cookie[key.trim()] = value;
        })
        return cookie[name];
    }

    function removeCookie(name) {
        document.cookie = name + '=; Max-Age=-99999999;';  
    }
</script>

<template>
    <ContentWrapper>
        <span>AuthFrame</span>
    </ContentWrapper>
</template>