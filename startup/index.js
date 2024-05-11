const { spawn } = require('child_process');
const sensor_config = require('./sensor_config.json');

main();

function start_sso_server() {
    console.log("Starting SSO server ...");
    const proc = spawn("sh", ["-c", "cd ../sso && ./run_with_database.sh"]);

    proc.on("error", (err) => {
        console.log("SSO server error: " + err);
    });

    proc.on("close", (code) => {
        console.log("SSO server stopped: " + code);
    });

    console.log("SSO server started");
}

function start_sso_client() {
    console.log("Starting SSO client ...");
    const proc = spawn("sh", ["-c", "cd ../sso_client && ./run.sh"]);

    proc.on("error", (err) => {
        console.log("SSO client error: " + err);
    });

    proc.on("close", (code) => {
        console.log("SSO client stopped: " + code);
    });

    console.log("SSO client started");
}

function start_smart_city_client() {
    console.log("Starting smart city client ...");
    const proc = spawn("sh", ["-c", "cd ../smart_city_client && ./run.sh"]);

    proc.on("error", (err) => {
        console.log("Smart city client error: " + err);
    });

    proc.on("close", (code) => {
        console.log("Smart city client stopped: " + code);
    });

    console.log("Smart city client started");
}

function start_sensor(port, object_attr, action_attr, index) {
    console.log("Starting sensor " + index + " ...");
    const proc = spawn("sh", ["-c", `cd ../sensors && node sensor.js -p ${port} -o '${object_attr}' -a '${action_attr}'`]);

    proc.on("error", (err) => {
        console.log("Sensor " + index + " error: " + err);
    });

    proc.on("close", (code) => {
        console.log("Sensor " + index + " stopped: " + code);
    });

    console.log("Sensor " + index + " started on port " + port);
}


function main() {
    start_sso_server();
    start_sso_client();
    start_smart_city_client();

    sensor_config.sensors.forEach((config, index) => {
        start_sensor(config.port, JSON.stringify(config.object_attributes), JSON.stringify(config.action_attributes), index);
    });    
}
