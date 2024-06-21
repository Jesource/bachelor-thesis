#!/bin/bash

cd ./docker/ || exit

# Prepare Docker container with DB, otherwise tests will be unable to run thus making maven build fail
./launch_database_docker_container.sh

# Build JAR file
./maven_build.sh

## Run the service
# Change directory and find executable
cd ../target/ || exit
jarExecutable=$(find . -type f -name "*.jar")

# Launch executable
echo "Launching $jarExecutable"
java -jar $jarExecutable