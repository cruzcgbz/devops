# Specify a stable MySQL version
FROM mysql/mysql-server:latest

# Copy initialization script
COPY world.sql /docker-entrypoint-initdb.d

# Set up environment variables
ENV MYSQL_ROOT_PASSWORD=example
ENV MYSQL_ROOT_HOST=%
ENV MYSQL_DATABASE=world

