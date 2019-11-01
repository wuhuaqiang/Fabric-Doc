#!/bin/bash

docker-compose -f ../docker-compose-orderer.yaml up -d
sleep 10
docker-compose -f ../docker-compose-peer.yaml up -d
docker-compose -f ../docker-compose-couchdb0.yaml up -d
docker-compose -f ../docker-compose-cli.yaml up -d
docker-compose -f ../docker-compose-ca.yaml up -d
