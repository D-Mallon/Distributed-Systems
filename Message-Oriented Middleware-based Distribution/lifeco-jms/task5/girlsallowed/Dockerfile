FROM openjdk:8-jre-alpine
COPY target/girlsallowed-0.0.1-jar-with-dependencies.jar /girlsallowed-0.0.1-jar-with-dependencies.jar
CMD  ["sh", "-c", "sleep 10 && /usr/bin/java -jar /girlsallowed-0.0.1-jar-with-dependencies.jar"]


# FROM openjdk:8-jre-alpine

# ENV SERVER_HOST localhost

# COPY target/girlsallowed-0.0.1-jar-with-dependencies.jar /girlsallowed-0.0.1-jar-with-dependencies.jar

# CMD ["/bin/sh", "-c", "sleep 10 && /usr/bin/java -cp /girlsallowed-0.0.1-jar-with-dependencies.jar girlsallowedMain ${SERVER_HOST}"]

