FROM openjdk:8-jre-alpine
COPY target/broker-0.0.1-jar-with-dependencies.jar /broker-0.0.1-jar-with-dependencies.jar
CMD ["sh", "-c", "sleep 10 && /usr/bin/java -jar /broker-0.0.1-jar-with-dependencies.jar"]

# FROM openjdk:8-jre-alpine

# ENV SERVER_HOST localhost

# COPY target/broker-0.0.1-jar-with-dependencies.jar /broker-0.0.1-jar-with-dependencies.jar

# #introducing sleep functionality here to allow for server to start up before broker
# # CMD ["/usr/bin/java", "-cp", "/broker-0.0.1-jar-with-dependencies.jar", "brokerMain", "${SERVER_HOST}"]
# CMD ["/usr/bin/java", "-cp", "/broker-0.0.1-jar-with-dependencies.jar", "brokerMain", "${SERVER_HOST}"]
