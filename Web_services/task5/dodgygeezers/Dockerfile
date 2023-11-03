# FROM openjdk:8-jre-alpine
# COPY target/dodgygeezers-0.0.1-jar-with-dependencies.jar /dodgygeezers-0.0.1-jar-with-dependencies.jar
# CMD /usr/bin/java -jar /dodgygeezers-0.0.1-jar-with-dependencies.jar ${SERVER_HOST}

FROM openjdk:8-jre-alpine
ENV SERVER_HOST localhost
COPY target/dodgygeezers-0.0.1-jar-with-dependencies.jar /dodgygeezers-0.0.1-jar-with-dependencies.jar
CMD /usr/bin/java -cp /dodgygeezers-0.0.1-jar-with-dependencies.jar DodgygeezersMain ${SERVER_HOST}