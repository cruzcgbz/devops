FROM openjdk:latest
COPY ./target/devops.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "devops.jar", "world:3306", "10000"]


# Use a stable, slim Java version for smaller image size
FROM openjdk:latest

# Set up a non-root user for security
RUN useradd -ms /bin/bash appuser
USER appuser

# Set the working directory
WORKDIR /tmp

# Copy the JAR file to the working directory
COPY ./target/devops.jar .

# Expose application port (optional, change as needed)
EXPOSE 8080

# Define the entry point with default arguments
ENTRYPOINT ["java", "-jar", "devops.jar", "world:3306", "10000"]
