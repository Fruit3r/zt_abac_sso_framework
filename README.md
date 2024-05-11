# Zero Trust Single-Sign-On Framework using Attribute-Based Access Control (IoT Environment)

## Running the System
### Overview
The structure of the project folder is pretty straight forward. For every individual component in the system exists a folder:

+ ./sso: SSO-Backend (PostgreSQL docker container start included)
+ ./sso\_client: SSO-Client (Vue - Web)
+ ./smart\_city\_client: Application Client (Vue - Web)
+ ./sensor: Sensor software

Every component folder has a startup script of the format "run<suffix>.sh". The suffix is used to describe further functionalities of the script apart from starting the component. Another folder found in the top level of the project is called "startup". Since the system should be launchable in a simulation manner the startup folder contains a script that starts the system and sensors with the given sensor configuration file "sensor\_config.json".

### Startup
The major functionality of the sensor configuration is to define how many sensors should be started and which attributes they have. An example startup configuration for 2 sensors would look like:

```
{
    "sensors": [
        {
            "port": 7000,
            "object_attributes": {
                "test": "test"
            },
            "action_attributes": {
                "test2": 2
            }
        },
        {
            "port": 7001,
            "object_attributes": {
                "test": "test"
            },
            "action_attributes": {
                "test2": 3
            }
        }
    ]
}
```

Action attributes can be defined as well since sensors only have one endpoint and no further functionalities that would require changeable action attributes. The startup script runs the remaining components with their corresponding run-scripts. Therefore changes to other components have to be done in their associated configuration files.

To start the whole system with the sensor configuration run

```
./run.sh
```

in the startup folder.

### Further Configuration
#### SSO-Backend
The configuration file application.properties of the SSO-Backend is located at "./sso/src/main/resources" in the system folder and holds the associated explanations for its entries. Please note that it is important to configure the CORS part of the SSO-Backend with the host addresses of the web-clients to run the system in a new environment.

#### SSO-Client / Smart City Client
The only important configuration for the web-clients is their port defined in their run-scripts. Their ports can be changed their.

