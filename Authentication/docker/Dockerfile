# Use the official PostgreSQL image as the base image
FROM postgres:latest

# Set environment variables for PostgreSQL
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_HOST_AUTH_METHOD=trust

# Install Bash and Nano, Vim editors
RUN apt-get update && apt-get install -y bash
RUN apt-get update && apt-get install -y nano
RUN apt-get update && apt-get install -y vim
