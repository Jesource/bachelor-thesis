#!/bin/bash

## Define variables

# Docker variables
CONTAINER_NAME="oasys-auth-db"
IMAGE_NAME="oasys-postgres-db"

# PostgreSQL variables
POSTGRES_USER="postgres"  # Use the default PostgreSQL superuser
POSTGRES_PASSWORD="postgres"  # Use the default PostgreSQL superuser password
DATABASE_NAME="oasys_dev"

# Application variables, used for DB connection
OASYS_USER="andrej"
OASYS_USER_PASSWORD="dontTellAnyone"


# Build a docker image, can be commented out after first successful launch
./build_db_image.sh $IMAGE_NAME


# Run the Docker container with port mapping
winpty docker run --name $CONTAINER_NAME -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD -d -p 5432:5432 $IMAGE_NAME


# Wait for the PostgreSQL container to start
echo "Waiting 20 seconds for PostgreSQL container to start..."
sleep 20

# Create the "oasys_dev" database
winpty docker exec -it $CONTAINER_NAME psql -U $POSTGRES_USER -c "CREATE DATABASE $DATABASE_NAME;"

# Create the "andrej" user with full access to the "oasys_dev" database
winpty docker exec -it $CONTAINER_NAME psql -U $POSTGRES_USER -d $DATABASE_NAME -c "CREATE USER $OASYS_USER WITH PASSWORD '$OASYS_USER_PASSWORD';"
winpty docker exec -it $CONTAINER_NAME psql -U $POSTGRES_USER -d $DATABASE_NAME -c "GRANT ALL PRIVILEGES ON DATABASE $DATABASE_NAME TO $OASYS_USER;"
# Let the application to create and manage tables
winpty docker exec -it $CONTAINER_NAME psql -U $POSTGRES_USER -d $DATABASE_NAME -c "GRANT ALL PRIVILEGES ON SCHEMA public TO $OASYS_USER; COMMIT;"


echo "PostgreSQL container '$CONTAINER_NAME' is running."
echo "User: $OASYS_USER"
echo "Password: $OASYS_USER_PASSWORD"
echo "Database: $DATABASE_NAME"
