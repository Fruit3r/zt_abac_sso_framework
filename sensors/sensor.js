// Imports
const express = require('express');
const cors = require('cors');
const request = require('request');

// Config
var port = 7000;
const maxVal = 2;
const minVal = 0;
const volatility = 0.1;

// Sensor default attributes
var objectAttributes = {
    type: 'sensor',
    location: 'downtown',
    secLevel: 5
}
var actionAttributes = {
    type: 'read'
}

let sensorVal = 0;
let sensorTime = 0;

// Parse process parameters
for(let i = 0; i < process.argv.length; ++i) {
    if (process.argv[i] === '-p') {
        port = parseInt(process.argv[i+1]);
    }
    if (process.argv[i] === '-o') {
        objectAttributes = JSON.parse(process.argv[i+1]); 
    }
    if (process.argv[i] === '-a') {
        actionAttributes = JSON.parse(process.argv[i+1]); 
    }
}

setInterval(updateValue, 1000);


function updateValue() {
    let sensorValNew = sensorVal + (volatility) * ((Math.random() * 4) - 2);

    if (sensorValNew <= maxVal && sensorValNew >= minVal) {
        sensorVal = sensorValNew;
    }
    sensorTime = Date.now();
}


async function authorize(idtoken, objectAttributes, actionAttributes) {
    let response;

    authReqBody = {
        subject: idtoken,
        object: JSON.stringify(objectAttributes),
        action: JSON.stringify(actionAttributes)
    }

    response = await fetch('http://10.0.1.103:9000/authorize', {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(authReqBody)
    });

    let result;

    if (response.headers.get('content-type')?.includes('application/json')) {
        try {
            result = await response.json();
        } catch (err) {
            console.log(err);
        }
    } else {
        result = {
            status: response.status,
            message: 'ok'
        };
    }

    
    //console.log(timeStamp.getFullYear() + '-' + timeStamp.getMonth() + '-' + timeStamp.getDay() + '  ---  Authorization: status=' + result.status + '; message=' + result.message);
    log('Authorization: status=' + result.status + '; message=' + result.message);

    if (response.status === 202) {
        return {
            authorized: true,
        };
    } else {
        return {
            authorized: false,
            status: result.status,
            message: result.message,
        };
    }
}

function log(msg) {
    const timeStamp = new Date();
    console.log(timeStamp.getFullYear() + '-' + timeStamp.getMonth() + '-' + timeStamp.getDay() + '  ---  ' + msg);
}

// Create REST application
const sensorApp = express();
sensorApp.use(cors());

// Endpoints
sensorApp.get('/value', async function (req, res) {
    log('HTTP-Request: /value # FROM: ' + req.get('origin'));

    // Get idtoken from request
    let idtoken = req.query.idtoken;

    let result =  await authorize(idtoken, objectAttributes, actionAttributes);

    if (result.authorized) {
        let newIDToken = result.idtoken;
        // Return value
        return res.send({ 
            value: sensorVal, 
            time: sensorTime,
        });
    } else {
        // Return error
        res.status(401).send({
            status: result.status,
            message: result.message
        });
    }
    
});


// Start application
sensorApp.listen(port, '0.0.0.0', () =>{
    console.log('Sensor started on port ' + port);
});
