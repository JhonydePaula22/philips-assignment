#!/bin/bash

echo "Executing Program"

sh -c "mvn clean package"

cd docker

sh -c "docker-compose up -d --force-recreate"

sh -c "./docker-files/create-queues.sh"