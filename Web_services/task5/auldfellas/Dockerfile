# FROM openjdk:8-jre-alpine
# COPY target/auldfellas-0.0.1-jar-with-dependencies.jar /auldfellas-0.0.1-jar-with-dependencies.jar
# CMD /usr/bin/java -jar /auldfellas-0.0.1-jar-with-dependencies.jar $SERVER_HOST

FROM openjdk:8-jre-alpine
ENV SERVER_HOST localhost
COPY target/auldfellas-0.0.1-jar-with-dependencies.jar /auldfellas-0.0.1-jar-with-dependencies.jar
CMD /usr/bin/java -cp /auldfellas-0.0.1-jar-with-dependencies.jar AuldfellasMain ${SERVER_HOST}
