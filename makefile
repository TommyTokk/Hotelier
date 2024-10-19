#===== VARIABILI =====
SRC = src
LIB = lib/Gson.jar
BIN = bin

JFLAGS = -cp
JARFLAGS = cvfm
JC = javac

SMANIFEST = SERVER.MF
CMANIFEST = CLIENT.MF
#===========


#===== COMANDI =====
#Crea il file jar per il server e per il client
all: server_jar client_jar
#Esegue sia il server che il client
run-all: server_run client_run
#Compila il server
server:
	$(JC) $(JFLAGS) $(LIB) $(SRC)/Server/*.java -d $(BIN)
#Compila il client
client:
	$(JC) $(JFLAGS) $(LIB) $(SRC)/Client/*.java -d $(BIN)
#Esegue il server
server_run:
	java $(JFLAGS) $(BIN):$(LIB) Server.ServerMain
#Esegue il client
client_run:
	java $(JFLAGS) $(BIN):$(LIB) Client.ClientMain
#Crea il file jar per il server
server_jar:
	jar $(JARFLAGS) Server.jar $(SMANIFEST) -C $(BIN)/ Server
#Crea il file jar per il client
client_jar:
	jar $(JARFLAGS) Client.jar $(CMANIFEST) -C $(BIN)/ Client
#Esegue il file jar per il server
server_run_jar:
	java -jar Server.jar
#Esegue il file jar per il client
client_run_jar:
	java -jar Client.jar
#Pulisce la directory bin
clean:
	rm -rf $(BIN)/*
#===========
