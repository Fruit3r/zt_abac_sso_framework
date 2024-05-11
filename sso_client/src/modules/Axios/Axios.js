import axios from 'axios';
import settings from '@/config/settings.json';


const httpClient = axios.create({
    baseURL: settings.SSO_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
});

export default httpClient;