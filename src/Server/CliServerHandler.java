package Server;

import java.io.BufferedReader;
import java.io.PrintWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/*
 * CliServerHandler
 * Classe usata per gestire la comunicazione con il client tramite la CLI
 * La classe prevede un set di metodi per l'invio di messaggi nel formato 
 * corretto rispetto al protocollo di comunicazione tra client e server
 */

public class CliServerHandler {
    private BufferedReader in;
    private PrintWriter out;

    public CliServerHandler(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    /*
     * cleanScreen
     * Metodo usato per pulire lo schermo del client
     */

    public void cleanScreen() {
        out.printf("\033[H\033[2J");
        out.flush();
    }

    /*
     * sendMessage
     * Metodo usato per inviare un messaggio al client
     * Il messaggio viene inviato nel formato corretto rispetto al protocollo
     * di comunicazione tra client e server
     * @param message: messaggio da inviare al client
     * @return 0 se l'invio del messaggio è andato a buon fine
     */

    public void sendMessage(String message) {
        out.printf("%s\n", message);
        out.flush();
    }

    /*
     * senlectOption
     * Metodo che gestisce la scelta dell'opzione dal menu da parte del client
     * @param options: array di stringhe contenente le opzioni del menu
     * @return l'indice dell'opzione scelta
     */
    public int selectOption(String[] options){
        try {
            int option = 0;
            boolean isValid = false;

            while(!isValid){
                this.out.printf("---- SCEGLI UN'OPZIONE ----\n");
                for(int i = 0; i < options.length; i++){
                    this.out.printf("%d) %s\n", i+1, options[i]);
                }
                out.printf("%s\n", "select");
                
                String userInput;
                while((userInput = in.readLine())== null){
                    out.printf("Errore di comunicazione con il client\n");
                    return -1;
                }
                System.out.printf("[SERVER] %s\n", userInput);
                try {
                    option = Integer.parseInt(userInput);
                } catch (Exception e) {
                    out.printf("Scelta non valida\n");
                    continue;
                }

                if(!userInput.equals("") && option > 0 && option <= options.length){
                    isValid = true;
                }else{
                    out.printf("Scelta non valida, prova di nuovo\n");
                }
            }
            return option-1;
        } catch (Exception e) {
            System.err.printf("[Server] Error: %s\n", e.getMessage());
            return -1;
        }
    }

    public String prompt(String message){
        try{
            String userInput = "";

            do{
                this.out.printf("%s\n", message);
                out.printf("%s\n", "input");
                while((userInput = in.readLine()) == null){
                    out.printf("Errore di comunicazione con il client\n");
                    return null;
                }

                if(userInput.equals("")){
                    this.out.printf("Input non valido\n");
                }
            }while(userInput.equals(""));

            return userInput;

        }catch(Exception e){
            System.err.printf("[Server] Error: %s\n", e.getMessage());
            return null;
        }
    }

    public String promptPassword(String message){

        String userInput = "";
        try {
            do{
                this.out.printf("%s\n", message);
                out.printf("%s\n", "input-pwd");

                while((userInput = in.readLine()) == null){
                    out.printf("Errore di comunicazione con il client\n");
                    return null;
                }
                if(userInput.equals("")){
                    this.out.printf("Input non valido\n");
                }
            }while(userInput.equals(""));

            return this.hashPassword(userInput);
            
        } catch (Exception e) {
            System.err.printf("[Server] Error: %s\n", e.getMessage());
            return null;
        }
    }

    public void showHotels(ArrayList<Hotel> hotels, String city){
        this.cleanScreen();
        this.out.printf("--------------------------\n");
        this.out.printf("       HOTELS\n         IN\n       %s\n", city);
        this.out.printf("--------------------------\n");
        for(Hotel h : hotels){
            this.showHotel(h);
            this.out.printf("----------------------\n");
        }
    }

    public void showHotel(Hotel hotel){
        this.cleanScreen();
        this.out.printf("---- HOTEL %s ----\n", hotel.getName());
        this.out.printf("%s", hotel.toString());
    }

    public Ratings handleReview() {
        while (true) {
            int cleaning, position, services, quality;
    
            try {
                cleaning = getValidRating(this.in, this.out, "Inserisci il voto per la pulizia:");
                position = getValidRating(this.in, this.out, "Inserisci il voto per la posizione:");
                services = getValidRating(this.in, this.out, "Inserisci il voto per i servizi:");
                quality = getValidRating(this.in, this.out, "Inserisci il voto per la qualità:");
                return new Ratings(cleaning, position, services, quality);
            } catch (NumberFormatException e) {
                out.printf("Voto non valido, inserisci un valore compreso tra 0 e 5!\n");
                //Send clear screen message
                out.printf("\033[H\033[2J");
                out.flush();
            }
        }
    }

    public int getValidRating(BufferedReader in, PrintWriter out, String message){
        int rating;
        while (true) {
            try {
                String input = this.prompt(message);
                rating = Integer.parseInt(input);
                if (rating >= 0 && rating <= 5) {
                    return rating;
                } else {
                    out.printf("Voto non valido, inserisci un valore tra 0 e 5!\n");
                    out.printf("\033[H\033[2J");
                    out.flush();
                }
            } catch (Exception e) {
                out.printf("Voto non valido, insersici un valore tra 0 e 5!\n");
                out.printf("\033[H\033[2J");
                out.flush();
            }
        }
    }
    

    public void cliWait(){
        this.out.printf("Premi invio per continuare...\n");
        this.out.println("wait");
        try {
            this.in.readLine();
        } catch (Exception e) {
            System.err.printf("[Server] Error: %s\n", e.getMessage());
            System.exit(1);
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for(byte b : digest){
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
