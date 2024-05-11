## TODO

- Performance of policy evaluation:
    - The target of evaluation is to find a policy that could evaluate to true -> only evaluate policies 
      that could evaluate to true (Attributes used in policy should be a subset of context attributes)
- Add Database policy validations so invalid data from the backend size is not loaded
- Key rotation
- Add boolean attributes
- Token refreshing:
  - New token is retrieved by responding with a new token on an expired token request
  - Timespan for refresh after token expiration is configured
  - New token is issued the by the resource response using 

- Token controlling:
  - Save last time token has been issued for user in user db entry
  - As user is always loaded timestamp of token can be compared to last time token issue for user
  - Would not require an additional db access
  - Tokens can be invalidated by e.g. deleting last issued time entry which requires user to authenicate     again (No token refresh allowed)

## NOTES

- Curl request:
  - Get all policies: ```curl http://localhost:9000/administrate/policies```
  - Add policy: ```curl -X POST -H "Content-Type: application/json" -d '{"name":"testpolicy", "rulesString":"#subject_permlevel > 2"}' http://localhost:9000/administrate/policies```
  - Delete policy: ```curl -X DELETE http://localhost:9000/administrate/policies/testpolicy```
  - Get admin token: ```curl -v -X POST -H "Content-Type: application/json" -d '{"username": "Admin", "password":"Password"}' http://localhost:9000/authenticate```
  - Authorization request: ``` curl -v -X POST -H  "Content-Type: application/json" -d '{"subject":"<token>", "object":"{\"type\":\"sensor\", \"area\":\"downtown\"}", "action": "{\"type\":\"read\"}"}' http://localhost:9000/authorize```