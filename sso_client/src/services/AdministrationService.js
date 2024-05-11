
import HTTP from '@/modules/Axios/Axios';

const BASE_URL = '/administrate';

const AdministrationService = {
    getAuthenticatedUser: function() {
        return HTTP.get(BASE_URL + '/users/authenticated');
    },
    getAllUsers: function() {
        return HTTP.get(BASE_URL + '/users');
    },
    getUserByUUID: function(uuid) {
        return HTTP.get(BASE_URL + '/users/' + uuid);
    },
    createUser: function(newUser) {
        return HTTP.post(BASE_URL + '/users', newUser);
    },
    updateUser: function(updateUser) {
        return HTTP.put(BASE_URL + '/users', updateUser);
    },
    deleteUserByUUID: function(uuid) {
        return HTTP.delete(BASE_URL + '/users/' + uuid);
    },
    getAllPolicies: function() {
        return HTTP.get(BASE_URL + '/policies');
    },
    createPolicy: function(policy) {
        return HTTP.post(BASE_URL + '/policies', policy);
    },
    updatePolicy: function(updatePolicy) {
        return HTTP.put(BASE_URL + '/policies', updatePolicy);
    },
    deletePolicyByUUID: function(uuid) {
        return HTTP.delete(BASE_URL + '/policies/' + uuid);
    }
}

export default AdministrationService;