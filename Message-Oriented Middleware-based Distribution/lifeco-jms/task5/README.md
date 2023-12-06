# Introduction

To run the program, you will need to have Java 8 and Maven installed on your machine.

# Running the Program

This is a maven project, complete the following steps:

1.  Navigate to the root folder of the Project.
2.  Open three terminal windows.
3.  In the first terminal, run the following command: mvn clean install -DskipTests
4.  In the second terminal, run the following commands: "docker-compose build" and then "docker-compose up"
5.  In the third terminal, run the following command: mvn exec:java -pl client
6.  The program will now run and the quotations for each of our clients should be provided in the Client terminal.

# Addendum

Please note that in the docker terminal, messages from the auldfellas quotations will show and not from any of the other services. This is intentional and was only for debugging purposes. The quotations are still sent to the client from all clients as expected. This does not effect the output on the Client terminal in any way.
