#!/bin/bash
docker run --name postgres_datafruit_sso --rm  -p 5432:5432 -e POSTGRES_USER=sso  -e POSTGRES_PASSWORD=xmas1010 -e POSTGRES_DB=sso -d postgres
mvn spring-boot:run
