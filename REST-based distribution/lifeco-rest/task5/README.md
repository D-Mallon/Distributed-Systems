# Introduction

To run the program, you will need to have Java 8 and Maven installed on your machine.

# Running the Program

This is a maven project, complete the following steps:

# Task 4

1.  Navigate to the root folder of the Project.
2.  Open 5 terminal windows.
3.  In the first window terminal window, run the following commands consecutively: 'mvn clean install' and then 'mvn compile spring-boot:run -pl auldfellas'
4.  In the second and third terminal windows, run the following commands respectively: "mvn compile spring-boot:run -pl dodgygeezers" and "mvn compile spring-boot:run -pl girlsallowed"
5.  In the fourth terminal, run the following command: mvn compile spring-boot:run -pl broker
6.  In the fifth terminal window, run the following command: mvn compile spring-boot:run -pl client
7.  The program will now run and the quotations for each of our clients should be provided in the Client terminal.

# Task 5

1.  Navigate to the root folder of the Project.
2.  Open 2 terminal windows.
3.  In the first terminal window, run the following commands in this order: mvn clean install
    docker compose build
    docker compose up
4.  In the second terminal, run the following command: "mvn exec:java -pl client"
5.  The program will now run and the quotations for each of our clients should be provided in the Client terminal.
