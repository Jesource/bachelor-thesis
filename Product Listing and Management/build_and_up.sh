#!/bin/bash

# Use docker-compose to build and start the containers
docker-compose -f compose.yaml up -d

# Build JAR file
./maven_build.sh

## Run the service
# Change directory and find executable
cd ./target/ || exit
jarExecutable=$(find . -type f -name "*.jar")

# Launch executable
echo "Launching $jarExecutable"
java -jar $jarExecutable