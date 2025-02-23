# Hotelier

## What's inside the repository?
The repository contains the project for the CS course of Laboratorio di Reti (Networks Laboratory) of the University of Pisa. The project is a simple hotel management system that allows the user to get information about the hotel.

## How to run the project?
Inside the repository there is a Makefile that allows you to compile the project. To compile the project, you can run the following command:
- To compile and run the server:
```bash
cd path/to/project
make server 
make server_run
```
- To compile and run the client:
```bash
cd path/to/project
make client
make client_run
```
There's also the possibility to compile and run the JAR files running the following commands:
- To compile the client:
```bash
cd path/to/projectDir
make client_jar
make client_run_jar
```

- To compile the server:
```bash
cd path/to/projectDir
make server_jar
make server_run_jar
```

```warning
For a correct execution of the project, the server must be started before the client. Also for a better user experience, server and client should be run in two different terminals.
```
