#!/bin/bash

docker-compose -f ../docker-compose-orderer.yaml down --volume --remove-orphans
docker-compose -f ../docker-compose-peer.yaml down --volume --remove-orphans
docker-compose -f ../docker-compose-cli.yaml down --volume --remove-orphans
docker rm -f $(docker ps -aq)
docker volume prune
docker network prune
