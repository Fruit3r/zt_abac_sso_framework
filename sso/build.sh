#!/bin/bash

# Clean old build
echo "########################################################"
echo "# Clean old build directory                            #"
echo "########################################################"

(set -x; rm ./build -R) 

# Package project to jar
echo
echo "########################################################"
echo "# Package spring boot project (execute tests)          #"
echo "########################################################"
mvn clean package

echo
echo "########################################################"
echo "# Setup deployment ready application folder            #"
echo "########################################################"
# Create build folder
(set -x; mkdir ./build)
# Create application folder. Will contain
# the application with its deployment ready
# folder structure
APP_FOLDER=./build/application
(set -x; mkdir $APP_FOLDER)

# Setup application folder
(set -x; mkdir $APP_FOLDER/config)
(set -x; mkdir $APP_FOLDER/resources)
(set -x; mkdir $APP_FOLDER/log)
(set -x; cp ./target/application.jar $APP_FOLDER)
(set -x; cp ./src/main/resources/application.properties $APP_FOLDER/config)

echo
echo "########################################################"
echo "# ZIP application folder                               #"
echo "########################################################"
# Add application folder as .zip
(set -x; zip -r ./build/application.zip $APP_FOLDER)

