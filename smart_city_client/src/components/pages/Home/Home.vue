<script setup>
    import { onMounted, ref } from 'vue';
    import { useRoute } from 'vue-router';
    import { getDefaultStore } from '@/stores/DefaultStore';
    import router from '@/router';
    // Packages
    import ApexCharts from 'apexcharts';
    import Emitter  from 'tiny-emitter/instance';
    // Modules
    import { getSensorClient } from '@/modules/Axios/Axios';
    // Components
    import ContentWrapper from '@/components/wrapper/ContentWrapper.vue';
    import TextInput from '@/components/input/TextInput.vue';
    import Button from '@/components/input/Button.vue';

    let defaultStore = getDefaultStore();
    let sensors = ref([]);


    const valCount = 40;

    let sensorData = new Array(valCount).fill(0);
    let timeData = new Array(valCount).fill('-');
    let loaded = false;

    var options = {
        chart: {
            type: 'area',
            height: '400px',
            fontFamily: 'Poppins',
            background: 'transparent',
            animations: {
                enabled: false
            }
        },
        colors: ['#AFC957'],
        series: [{
            name: 'sales',
            data: sensorData
        }],
        dataLabels: {
            enabled: false
        },
        stroke: {
            curve: 'straight',
            width: 2
        },
        fill: {
            type: 'solid',
            colors: ['#AFC957'],
            opacity: 0.2
        },
        xaxis: {
            categories: timeData
        },
        yaxis: {
            min:0,
            max:2,
            tickAmount: 10,
            labels: {
                formatter: function(val) {
                    return val.toFixed(2);
                }
            },
        },
    }

    onMounted(async () => {
        if (localStorage.getItem('sensors')) {
            let savedSensors = JSON.parse(localStorage.getItem('sensors'));
            savedSensors.forEach(() => {
                addSensor(false);
            });
        }

        //chart = new ApexCharts(document.querySelector("#chart"), options);
        setInterval(() => { fetchSensorData(); }, 2000);
        //chart.render();
    });

    
    function fetchSensorData() {
        if (!loaded) {
            let savedSensors = JSON.parse(localStorage.getItem('sensors'));
            savedSensors.forEach((sensor, index) => {
                addSensorHost(sensor.sensorHost, index);
            });
            loaded = true;
        }

        // Handle each sensor for data fetching
        sensors.value.forEach(sensor => {
            if (!defaultStore.isAuthenticated) {
                sensor.chartStatus = 'You are not logged in!';
                sensor.data = [];
                sensor.time = [];
                sensor.chart.updateSeries([{
                    name: 'Series',
                    data: sensor.data
                }]);
            } else {
                sensor.sensorClient.get('/value?idtoken=' + localStorage.getItem('idtoken'))
                .then((res) => {
                    let time = new Date(res.data.time);

                    if (sensor.data.length > valCount) {
                        sensor.data.shift();
                        sensor.time.shift();
                    }
                    sensor.data.push(Math.trunc(res.data.value * 100)/100);
                    sensor.time.push(String(time.getHours()).padStart(2, "0") + ':' + String(time.getMinutes()).padStart(2, "0") + ':' + String(time.getSeconds()).padStart(2, "0"));

                    sensor.chartStatus = 'ok';

                    sensor.chart.updateSeries([{
                        name: 'Series',
                        data: sensor.data
                    }]);
                }).catch((err) => {
                    console.log(err);

                    let status, message, errCode;
                    
                    if (err.response) {
                        status = err.response.data.status;
                        message = err.response.data.message;
                        errCode = parseInt(message.split('#')[0]);
                    }


                    if (err.code === 'ERR_NETWORK') {
                        sensor.chartStatus = 'Unable to reach sensor';
                    }
                    else if (status === 401 && errCode === -3 && defaultStore.isAuthenticated) {
                        Emitter.emit('maintain');
                    } else if (status === 401 && errCode === -1) {
                        sensor.chartStatus = message;
                    } else {
                        Emitter.emit('logout');
                    }
                });
            }
        });
    }

    function addSensor(save) {
        let length = sensors.value.length;
        console.log('Adding sensor at index: ' + length);
        sensors.value.push({
            sensorClient: null,
            chart: null,
            chartStatus: 'Sensor host not defined',
            data: new Array(valCount).fill(0),
            time: new Array(valCount).fill('-')
        });

        if (save) {
            saveSensor();
        }
    }

    function saveSensor() {
        // Add to local storage
        let savedSensors = localStorage.getItem('sensors') ? JSON.parse(localStorage.getItem('sensors')) : [];
        savedSensors.push({
            sensorHost: ''
        });
        console.log(savedSensors);
        localStorage.setItem('sensors', JSON.stringify(savedSensors));
    }

    function addSensorHost(host, sensorIndex) {
        //event.preventDefault();
        //let host = event.currentTarget.elements.sensorHost.value;
        console.log(host);

        let sensor = sensors.value[sensorIndex];
        sensor.sensorClient = getSensorClient(host);
        sensor.chart = new ApexCharts(document.querySelector('#chart-' + sensorIndex), options);
        sensor.chartStatus = 'Loading';

        sensor.chart.render();

        // Add to local storage sensor
        let savedSensors = JSON.parse(localStorage.getItem('sensors'));
        savedSensors[sensorIndex].sensorHost = host;
        localStorage.setItem('sensors', JSON.stringify(savedSensors));
    }

    function removeSensor(sensorIndex) {
        sensors.value = sensors.value.slice(0, sensorIndex).concat(sensors.value.slice(sensorIndex + 1));

        // Remove from local storage
        let savedSensors = JSON.parse(localStorage.getItem('sensors'));
        savedSensors = savedSensors.slice(0, sensorIndex).concat(savedSensors.slice(sensorIndex + 1));
        console.log(savedSensors);
        localStorage.setItem('sensors', JSON.stringify(savedSensors));
    }

</script>

<template>
    <ContentWrapper>
        <div class="w-full h-90 bg-white rounded p-4"><span class="mr-4">Authentication State: </span>
            <span v-if="defaultStore.isAuthenticated" class="text-xl font-bold">Authenticated</span>
            <span v-if="!defaultStore.isAuthenticated" class="text-xl font-bold">Unauthenticated</span>
        </div>

        <div v-for="(sensor, index) in sensors" class="bg-white rounded mt-6 p-4">
            <div class="w-full h-[400px] font-poppins" :id="'chart-' + index"></div>
            <div class="w-full flex flex-row justify-between">
                <div><span class="font-bold">Status:</span> {{ sensor.chartStatus }}</div>
                
                <div><Button @click="removeSensor(index)" class="bg-primary text-white"><i class="fa fa-trash" /></Button></div>
            </div>
            <div v-if="sensor.sensorClient === null" class="mt-5">
                <form @submit.prevent="addSensorHost($event.currentTarget.elements.sensorHost.value, index)" class="flex justify-start gap-6">
                    <TextInput name="sensorHost" label="Sensorhost: "/>
                    <Button class="bg-primary text-white">Save</Button>
                </form>
            </div>
        </div>

        <div class="w-full text-center mt-8">
            <button class="h-8 px-4 bg-tertiary text-white rounded-full shadow-lg" @click="addSensor(true)"><i class="fa fa-plus" /> Add Sensor</button>
        </div>
        
        <!--<div class="w-full  p-4 rounded mt-8 bg-white">
            <form @submit="addSensor($event)" class="flex justify-between">
                <div>
                    <TextInput name="sensorHost" label="Sensor Host:" />
                </div>
                <button class="h-8 px-4 bg-tertiary text-white rounded-full shadow-lg"><i class="fa fa-plus" /> Add Sensor</button>
            </form>
        </div>-->
    </ContentWrapper>
</template>