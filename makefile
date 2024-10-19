#===== VARIABLES =====
SRC = src
LIB = lib/Gson.jar
BIN = bin

JFLAGS = -cp
JARFLAGS = cvfm
JC = javac

SMANIFEST = SERVER.MF
CMANIFEST = CLIENT.MF
#===========


#===== COMMANDS =====
#Create the bin directory
all: server_jar client_jar

#Run server and client
run-all: server_run client_run

#Comile the server
server:
	$(JC) $(JFLAGS) $(LIB) $(SRC)/Server/*.java -d $(BIN)

#Compile the client
client:
	$(JC) $(JFLAGS) $(LIB) $(SRC)/Client/*.java -d $(BIN)

#Run the server
server_run:
	java $(JFLAGS) $(BIN):$(LIB) Server.ServerMain

#Run the client
client_run:
	java $(JFLAGS) $(BIN):$(LIB) Client.ClientMain

#Create the jar file for the server
server_jar:
	jar $(JARFLAGS) Server.jar $(SMANIFEST) -C $(BIN)/ Server

#Create the jar file for the client
client_jar:
	jar $(JARFLAGS) Client.jar $(CMANIFEST) -C $(BIN)/ Client

#Run the jar file for the server
server_run_jar:
	java -jar Server.jar

#Execute the jar file for the client
client_run_jar:
	java -jar Client.jar

#Clean the bin directory
clean:
	rm -rf $(BIN)/*
#===========
