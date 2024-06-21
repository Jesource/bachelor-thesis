#!/bin/bash

docker run --name "mongo_docker" -e MONGO_INITDB_DATABASE="my_awesome_db" -d -p 49677:27017 mongo:latest
