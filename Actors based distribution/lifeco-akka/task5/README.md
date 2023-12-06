# Introduction

To run the program, you will need to have Java 8 and Maven installed on your machine.

# Running the Program

This is a maven project, complete the following steps:

# Task 4

1.  Navigate to the root folder of the Project.
2.  Open 5 terminal windows.
3.  In the first window terminal window, run the following commands consecutively: 'mvn clean install' and then 'mvn exec:java -pl broker'
4.  In the second, third and fourth terminal windows, run the following commands respectively: "mvn exec:java -pl dodgygeezers", "mvn exec:java -pl girlsallowed" and "mvn exec:java -pl auldfellas".
5.  In the fourth terminal, run the following command: mvn exec:java -pl client
6.  The program will now run and the quotations for each of our clients should be provided in the Client terminal.

# Task 5

1.  Navigate to the root folder of the Project.
2.  Open 1 terminal window.
3.  In the terminal window, run the following commands in this order: mvn clean install
    docker compose build
    docker compose up
4.  The program will now run and the quotations for each of our clients should be provided in the terminal window.
