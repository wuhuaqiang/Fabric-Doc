#!/bin/bash

docker-compose -f ../docker-compose-orderer.yaml up -d
docker-compose -f ../docker-compose-orderer1.yaml up -d
sleep 10
docker-compose -f ../docker-compose-peer.yaml up -d
docker-compose -f ../docker-compose-cli.yaml up -d
