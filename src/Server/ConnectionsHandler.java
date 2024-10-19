package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

enum State {
    HOME ("Home"),
    LOGIN ("Login"),
    LOGGED ("Logged"),
    REGISTER ("Register"),
    SEARCHHOTEL ("Search Hotel"),
    SHOWBADGES ("Show Badges"),
    INSERTREVIEW ("Insert Review"),
    LOGOUT ("Logout"),
    EXIT ("Exit");


    private String state; 
    private State(String s) {
        this.state = s;
    }

    public String getState() {
        return this.state;
    }

    public boolean stateEquals(String s) {
        return this.state.equals(s);
    }

    public void setState(String s) {
        this.state = s;
    }
};


public class ConnectionsHandler implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private State state;
    private State prevState;
    private User currentUser;

    public ConnectionsHandler(Socket s) {
        this.socket = s;
        this.state = State.HOME;
        this.prevState = null;
    }

    @Override
    public void run() {
        ServerCache cache = ServerCache.getInstance();
        System.out.printf("[Server] Connessione stabilita con %s\n", socket.getInetAddress());
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            CliServerHandler cliHandler = new CliServerHandler(in, out);

            while(!state.stateEquals("Exit")){
                System.out.printf("[Server] STATE: %s\n", state.getState());
                cliHandler.cleanScreen();
                if(currentUser != null){
                cliHandler.sendMessage("Ben tornato " + currentUser.getUsername() + "!");
                }else{
                    cliHandler.sendMessage("Benvenuto!");
                }

                switch (state) {
                    case HOME:
                        home(cliHandler);
                        break;
                    case LOGGED:
                        homeLogged(cliHandler);
                        break;
                    case LOGIN:
                        login(cache, cliHandler);
                        break;
                    case REGISTER:
                        register(cache, cliHandler);
                        break;
                    case SEARCHHOTEL:
                        searchHotel(cache, cliHandler);
                        break;
                    case INSERTREVIEW:
                        insertReview(cache, cliHandler);
                        break;
                    case SHOWBADGES:
                        showBadges(cache, cliHandler);
                        break;
                    case LOGOUT:
                        logout(cliHandler);
                        break;
                    case EXIT:
                        cliHandler.sendMessage("exit");
                        break;
                    default:
                        state = State.EXIT;
                        break;
                }
            }
            this.out.printf("%s\n", "exit");
        }catch(Exception e){
            System.err.printf("[Server] Error: %s\n", e.getMessage());
            System.exit(1);
        }
    }

    private void logout(CliServerHandler cliHandler) {
        if(this.currentUser == null || (this.state == State.LOGOUT && this.prevState == State.HOME)){
            this.state = State.EXIT;
            this.prevState = null;
            cliHandler.sendMessage("exit");
        }else{
            this.currentUser = null;
            this.prevState = this.state;
            this.state = State.HOME;
            out.printf("%s\n", "logout");
        }
    }

    public void searchHotel(ServerCache cache, CliServerHandler cliHandler){
        String[] options = {"Nome dell'hotel", "Città dell'hotel", "Esci"};
        int index = cliHandler.selectOption(options);
        if(index == -1){
            this.prevState = this.state;
            this.state = State.EXIT;
            return;
        }
        String selection = options[index];

        long startTime = 0;
        long endTime = 0;

        switch (selection) {
            case "Nome dell'hotel":
                cliHandler.cleanScreen();
                String hotelName = cliHandler.prompt("Inserisci il nome dell'hotel");
                if(hotelName == null){
                    this.prevState = this.state;
                    this.state = State.EXIT;
                    return;
                }else hotelName = hotelName.trim();

                String city = cliHandler.prompt("Inserisci la città");
                if(city == null){
                    this.prevState = this.state;
                    this.state = State.EXIT;
                    return;
                }else city = city.trim();
                
                startTime = System.currentTimeMillis();
                Hotel hotel = cache.getHotelByNameAndCity(city, hotelName);
                endTime = System.currentTimeMillis();
                System.out.printf("[Server - SearchByNameAndCity] Tempo di esecuzione: %d ms\n", endTime - startTime);
                if(hotel == null){
                    cliHandler.sendMessage("Non abbiamo trovato l'hotel che stai cercando!");
                }else{
                    cliHandler.cleanScreen();
                    cliHandler.showHotel(hotel);
                }
                cliHandler.cliWait();
                break;
            case "Città dell'hotel":
                cliHandler.cleanScreen();
                city = cliHandler.prompt("Inserisci la città");
                if(city == null){
                    cliHandler.sendMessage("Valore non valido");
                    this.prevState = this.state;
                    this.setState(State.SEARCHHOTEL);
                }else city.trim();
                System.out.println("[Server] City: " + city);
                startTime = System.currentTimeMillis();
                ArrayList<Hotel> hotels = cache.getHotelsByCity(city);
                endTime = System.currentTimeMillis();
                System.out.printf("[Server - SearchByCity] Tempo di esecuzione: %d ms\n", endTime - startTime);
                if(hotels == null){
                    cliHandler.sendMessage("Non abbiamo trovato l'hotel che stai cercando!");
                }else{
                    cliHandler.showHotels(hotels, city);
                }
                cliHandler.cliWait();
                break;
            case "Esci":
                this.prevState = this.state;
                this.state = State.LOGGED;
                break;
            default:
                break;
        }
        this.prevState = this.state;
        if(this.currentUser != null) this.state = State.LOGGED;
        else this.state = State.HOME;
    }

    private void register(ServerCache cache, CliServerHandler cliHandler) {
        String username = cliHandler.prompt("Inserisci il tuo username");
        if(username == null){
            this.prevState = this.state;
            this.state = State.EXIT;
            return;
        }else username = username.trim();

        System.out.printf("[Server] Username: %s\n", username);
        String password = cliHandler.promptPassword("Password");
        System.out.printf("[Server] Password: %s\n", password);

        boolean user = cache.addUser(new User(username, password));
        
        if(user){
            cliHandler.sendMessage("Registrazione avvenuta con successo!");
            cliHandler.cliWait();
            cliHandler.cleanScreen();
            String[] options = {"Accedi", "Torna alla home", "Esci"};
            int index = cliHandler.selectOption(options);
            if(index == -1){
                this.prevState = this.state;
                this.state = State.EXIT;
                return;
            }
            String selection = options[index];
            switch (selection.toLowerCase()) {
                case "accedi":
                    this.prevState = this.state;
                    this.state = State.LOGIN;
                    break;
                case "torna alla home":
                    this.prevState = this.state;
                    this.state = State.HOME;
                    break;
                case "esci":
                    this.prevState = this.state;
                    this.state = State.HOME;
                    break;
                default:
                    break;
            }
        }else{
            cliHandler.sendMessage("Username già in uso!");
            this.prevState = this.state;
            this.state = State.REGISTER;
        }
        cliHandler.cliWait();
    }

    private void login(ServerCache cache, CliServerHandler cliHandler) {        
        cliHandler.cleanScreen();
        cliHandler.sendMessage("---- LOGIN ----");
        String username = cliHandler.prompt("Username:");

        if(username == null){
            this.prevState = this.state;
            this.state = State.EXIT;
            return;
        }else username = username.trim();


        String password = cliHandler.promptPassword("Inserisci la tua password");
        if(password == null){
            this.prevState = this.state;
            this.state = State.EXIT;
            return;
        }

        User u = cache.getUserByUsername(username, password);

        if(u != null){
            cliHandler.sendMessage("Login avvenuto con successo!");
            out.printf("%s\n", "login");
            this.currentUser = u;
            this.prevState = this.state;
            this.state = State.LOGGED;

            cliHandler.cliWait();
        }else{
            cliHandler.sendMessage("Username o password errati!");
            String response = cliHandler.prompt("Hai già un account? (y/n)");
            if(response.equals("n")){
                response = cliHandler.prompt("Vuoi registrarti? (y/n)");
                if(response == null){
                    this.prevState = this.state;
                    this.state = State.EXIT;
                    return;
                }
                if(response.equals("y")){
                    this.prevState = this.state;
                    this.state = State.REGISTER;
                }else{
                    this.prevState = this.state;
                    this.state = State.HOME;
                }
            }
            cliHandler.cliWait();
        }
    }

    private void insertReview(ServerCache cache, CliServerHandler cliHandler) {
        cliHandler.cleanScreen();
        cliHandler.sendMessage("---- INSERISCI LA RECENSIONE ----");
        String hotelName = cliHandler.prompt("Inserisci il nome dell'hotel che vuoi recensire");
        if(hotelName == null){
            this.prevState = this.state;
            this.state = State.EXIT;
            return;
        }else hotelName = hotelName.trim();

        String hotelCity = cliHandler.prompt("Inserisci la città dell'hotel che vuoi recensire");
        if(hotelCity == null){
            this.prevState = this.state;
            this.state = State.EXIT;
            return;
        }else hotelCity = hotelCity.trim();

        //Clean the string from any special character except space
        hotelName = hotelName.replaceAll("[^a-zA-Z0-9 ]", "");
        hotelCity = hotelCity.replaceAll("[^a-zA-Z0-9 ]", "");
        
        Hotel hotel = cache.getHotelByNameAndCity(hotelCity, hotelName);
        if(hotel == null){
            cliHandler.sendMessage("Hotel non trovato!");
            this.prevState = this.state;
            this.state = State.LOGGED;
        }else{
            cliHandler.cleanScreen();
            int rate;
            while(true){
                try{
                    rate = (int) Double.parseDouble(cliHandler.prompt("Insersici il rate per l'hotel (0 - 5):"));
                    System.out.printf("[SERVER] %s\n", rate);
                    break;
                }catch(Exception e){
                    this.out.printf("Rate non valido, inserisci un valore compreso tra 0 e 5!\n");
                    cliHandler.cleanScreen();
                }
            }

            do{
                if(rate < 0 || rate > 5){
                    this.out.printf("Rate non valido, inserisci un valore compreso tra 0 e 5!\n");
                    cliHandler.cleanScreen();
                    rate = Integer.parseInt(cliHandler.prompt("Insersici il rate per l'hotel (0 - 5):"));
                }
            }while(rate < 0 || rate > 5);

            Ratings r = cliHandler.handleReview();
            Reviews review = new Reviews(this.currentUser.getUsername(), rate, r);
            //add the new review to the hotel list of reviews
            //calculate the new ratings value based on the new ratings
            hotel.addReview(review);
            hotel.setRatings();

            //update the hotel in the cache
            cache.updateHotel(hotel);
            System.out.println("[SERVER] Recensione aggiunta con successo!");
            this.currentUser.addPoints(20);
            cache.updateUser(this.currentUser);
            cliHandler.sendMessage("Recensione aggiunta con successo!");
            this.prevState = this.state;
            this.state = State.LOGGED;
        }
        cliHandler.cliWait();
    }

    private void showBadges(ServerCache cache, CliServerHandler cliHandler) {
        cliHandler.cleanScreen();
        if(this.currentUser == null){
            cliHandler.sendMessage("Devi aver effettuato il login per poter vedere i tuoi badge!");
            String login = cliHandler.prompt("Vuoi effettuare il login? (y/n)");
            this.prevState = this.state;
            this.state  = login.toLowerCase().equals("y") ? State.LOGIN : State.HOME;
        }else{
            Badge b = this.currentUser.getBadge();
            cliHandler.sendMessage("-------- BADGES DI " + this.currentUser.getUsername() + " --------");
            cliHandler.sendMessage("Badge: " + b.getTitle());
            cliHandler.sendMessage("Descrizione: " + b.getDescription());
        }
        this.prevState = this.state;
        this.state = State.LOGGED;
        cliHandler.cliWait();
    }

    private void homeLogged(CliServerHandler cliHandler) {
        String [] options = {"Cerca Hotel", "Inserisci Recensione", "Mostra Badges", "Logout"};
        
        int index = cliHandler.selectOption(options);
        if (index == -1) {
            this.state = State.EXIT;
            return;
        }
        String selection = options[index];
        String str = selection.replace(" ", "").toUpperCase();

        switch (str) {
            case "CERCAHOTEL":
                this.prevState = this.state;
                this.state = State.SEARCHHOTEL;
                break;
            case "INSERISCIRECENSIONE":
                this.prevState = this.state;
                this.state = State.INSERTREVIEW;
                break;
            case "MOSTRABADGES":
                this.prevState = this.state;
                this.state = State.SHOWBADGES;
                break;
            case "LOGOUT":
                this.prevState = this.state;
                this.state = State.LOGOUT;
                break;
            default:
                break;
        }

        cliHandler.cliWait();
    }

    public void home(CliServerHandler cliHandler) throws IOException{
        String[] options = {"Accedi", "Registrati", "Cerca Hotel", "Esci"};
        int index = cliHandler.selectOption(options);
        if (index == -1) {
            this.state = State.EXIT;
            return;
        }
        String selection = options[index];

        String str = selection.replace(" ", "").toUpperCase();
        
        switch (str) {
            case "ACCEDI":
                this.prevState = this.state;
                this.state = State.LOGIN;
                break;
            case "REGISTRATI":
                this.prevState = this.state;
                this.state = State.REGISTER;
                break;
            case "CERCAHOTEL":
                this.prevState = this.state;
                this.state = State.SEARCHHOTEL;
                break;
            case "ESCI":
                this.prevState = this.state;
                this.state = State.EXIT;
                break;
            default:
                break;
        }
        cliHandler.cliWait();
    }

    public void setState(State state){
        this.state = state;
    }

    public User getCurrentUser(){
        return this.currentUser;
    }  
}