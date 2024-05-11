import axios from 'axios';
import settings from '@/config/settings.json';


export const httpClient = axios.create({
    baseURL: settings.SSO_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
});

export function getSensorClient(host) {
    return axios.create({
        baseURL: host,
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

export default httpClient;