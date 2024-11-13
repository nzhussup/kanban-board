#!/bin/bash

export $(cat .env | grep -v '^#' | xargs)

echo "Starting the kanban-db container..."
docker run -d \
  --name kanban-db \
  -e POSTGRES_USER=$DB_USER \
  -e POSTGRES_PASSWORD=$DB_PASSWORD \
  -e POSTGRES_DB=$DB_NAME \
  -p $DB_PORT:5432 \
  $DOCKERHUB_USERNAME/kanban-db:latest

echo "Starting the redis container..."
docker run -d \
  --name redis \
  -p $REDIS_PORT:6379 \
  $DOCKERHUB_USERNAME/redis-db:latest


echo "Kanban DB is now accessible at localhost:$DB_PORT"
echo "Redis is now accessible at localhost:$REDIS_PORT"