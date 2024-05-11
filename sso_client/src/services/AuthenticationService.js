
import HTTP from '@/modules/Axios/Axios';

const BASE_URL = '/authenticate'

const AuthenticationService = {
    authenticate: function(username, password) {
        return HTTP.post(BASE_URL, {username: username, password: password});
    },
    maintain: function() {
        return HTTP.get(BASE_URL + '/maintain');
    }
};

export default AuthenticationService;