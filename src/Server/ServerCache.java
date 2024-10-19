package Server;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


//TODO: Implementare la cache per gli hotel tenendo conto delle città completate 

/**
 * ServerCache
 * Classe che rappresenta la cache del server
 * Contiene le informazioni degli utenti e degli hotel
 */

public class ServerCache {
    // private LinkedBlockingQueue<User> users;
    // private LinkedBlockingQueue<Hotel> hotels;
    private ConcurrentHashMap<String, User> users;//HashMap degli utenti con chiave username
    private ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> hotels;//HashMap degli hotel con chiave città
    private static ServerCache instance;//Istanza della classe

    private ConcurrentHashMap<String, Hotel> BestHotelMap = new ConcurrentHashMap<>();//Contains the best hotel of each city
    private Hotel bestHotel = null;//Contiene l'hotel migliore tra tutti

    private ConcurrentHashMap<String, Boolean> completedCity = new ConcurrentHashMap<>();//Contiene le città completate all'interno della cache

    public ServerCache() {
        users = new ConcurrentHashMap<>();
        hotels = new ConcurrentHashMap<>();
    }

    /**
     * getInstance
     * Restituisce l'istanza della classe
     * @return istanza della classe ServerCache
     */
    public static synchronized ServerCache getInstance() {
        if(instance == null) {
            instance = new ServerCache();
        }
        return instance;
    }

    /**
     * existsUser
     * Controlla se esiste un utente con username e password uguali a quelli passati come parametri
     * 
     * @param username
     * @param password
     * @return User se esiste un utente con username e password uguali a quelli passati come parametri, null altrimenti
     */
    public User existsUser(String username, String password) {
        if(users.containsKey(username)){
            return users.get(username);
        }
        return null;
    }


    /**
     * existsHotel
     * Controlla se esiste un hotel con nome e città uguali a quelli passati come parametri
     * Il metodo sfrutta una concurrentHashMap per cercare l'hotel in modo più efficiente
     * @param city
     * @param hotelName
     * @return Hotel se esiste un hotel con nome e città uguali a quelli passati come parametri, null altrimenti
     */
    public Hotel existsHotel(String city, String hotelName){
        if(hotels.containsKey(city.toLowerCase())){
            LinkedBlockingQueue<Hotel> cityHotels = hotels.get(city.toLowerCase());
            for(Hotel h : cityHotels){
                if(h.getName().toLowerCase().equals(hotelName.toLowerCase()) && h.getCity().toLowerCase().equals(city.toLowerCase())) {
                    return h;
                }
            }
        }
        return null;
        // for(Hotel h : hotels) {
        //     if(h.getName().toLowerCase().equals(hotelName.toLowerCase()) && h.getCity().toLowerCase().equals(city.toLowerCase())) {
        //         return h;
        //     }
        // }
    }

    /**
     * addUser
     * Aggiunge un utente alla cache
     * @param user : utente da aggiungere alla cache
     * @return true se l'utente è stato aggiunto con successo, false altrimenti
     */
    public boolean addUser(User user) {
        if(!this.users.containsKey(user.getUsername())){
            User u = JsonHandler.getUser(user.getUsername(), user.getPassword(), ServerMain.getUsersPath());
            if(u == null){
                users.put(user.getUsername(), user);
                return true;
            }else return false;
        }else{
            return false;
        }
    }

    /**
     * updateUser
     * Aggiorna l'utente passato come parametro
     * @param user : utente da aggiornare
     */
    public synchronized void updateUser(User user){
        users.put(user.getUsername(), user);
    }

    /**
     * addHotel
     * Aggiunge un hotel alla cache
     * @param hotel : hotel da aggiungere alla cache
     * @return true se l'hotel è stato aggiunto con successo, false altrimenti
     */
    public boolean addHotel(Hotel hotel) {
        if(hotels.containsKey(hotel.getCity().toLowerCase())){
            LinkedBlockingQueue<Hotel> cityHotels = hotels.get(hotel.getCity().toLowerCase());
            if(!cityHotels.contains(hotel)){
                cityHotels.add(hotel);
                hotels.put(hotel.getCity().toLowerCase(), cityHotels);
                return true;
            }
        }
        else{
            LinkedBlockingQueue<Hotel> cityHotels = new LinkedBlockingQueue<>();
            cityHotels.add(hotel);
            hotels.put(hotel.getCity().toLowerCase(), cityHotels);
            return true;
        }
        return false;
        // if(!hotels.contains(hotel)) {
        //     hotels.add(hotel);
        //     return true;
        // }
        // return false;
    }

    /**
     * getUserByUsername
     * Restituisce l'utente con username uguale a quello passato come parametro
     * Se presente nella cache restituisce l'utente altrimenti l'utente viene cercato all'interno del file json
     * @param username
     * @return utente con username uguale a quello passato come parametro
     */

    public User getUserByUsername(String username, String password) {
        User u = this.existsUser(username, password);
        if(u != null){
            System.out.println("[SERVER] User found in cache");
            if(u.getPassword().equals(password))
                return u;
        }
        else {
            System.out.println("[SERVER] User not found in cache");
            u = JsonHandler.getUser(username, password, ServerMain.getUsersPath());
            if(u != null)
                if(u.getPassword().equals(password)){
                    this.addUser(u);
                    return u;
                }
        }
        return null;
    }

    /**
     * getHotelByName
     * Restituisce l'hotel con nome uguale a quello passato come parametro
     * Se presente nella cache restituisce l'hotel altrimenti l'hotel viene cercato all'interno del file json
     * @param hotelName nome dell'hotel da cercare
     * @return hotel con nome uguale a quello passato come parametro
     */

    public Hotel getHotelByName(String city, String hotelName) {
        Hotel h = this.existsHotel(city, hotelName);
        if(h != null)
            return h;
        else {
            h = JsonHandler.getHotel(hotelName, city,  ServerMain.getHotelsPath());
            if(h != null)
                this.addHotel(h);
            return h;
        }
    }

    /**
     * getHotelByNameAndCity
     * Restituisce l'hotel con nome e città uguali a quelli passati come parametri
     * Se presente nella cache restituisce l'hotel altrimenti l'hotel viene cercato all'interno del file json
     * @param city
     * @param hotelName
     * @return hotel con nome e città uguali a quelli passati come parametri
     */

    public Hotel getHotelByNameAndCity(String city, String hotelName) {
        Hotel h = this.existsHotel(city, hotelName);
        if(h != null)
            return h;
        else {
            h = JsonHandler.getHotel(hotelName, city, ServerMain.getHotelsPath());
            if(h != null)
                this.addHotel(h);
            return h;
        }
    }

    /**
     * getHotelsByCity
     * Restituisce la lista di hotel presenti nella città passata come parametro
     * @param city
     * @return lista di hotel presenti nella città "city"
     */

    public ArrayList<Hotel> getHotelsByCity(String city){
        ArrayList<Hotel> hotels = new ArrayList<Hotel>();
        if(completedCity.containsKey(city.toLowerCase())){
            hotels = new ArrayList<Hotel>(this.hotels.get(city.toLowerCase()));
        }else{
            ArrayList<Hotel> fileHotels = JsonHandler.getHotelsByCity(city, ServerMain.getHotelsPath());
            hotels = new ArrayList<>();
            if(fileHotels != null){
                for(Hotel h : fileHotels){
                    //if h is in cache substitute it
                    if(this.existsHotel(city, h.getName()) != null){
                        hotels.add(this.existsHotel(city, h.getName()));
                    }else{
                        hotels.add(h);
                    }
                    this.addHotel(h);
                }
                this.completedCity.put(city.toLowerCase(), true);
            }
        }
        hotels.sort((h1, h2) -> h1.compareTo(h2));
        for(Hotel h : hotels){
            System.out.println("[SERVER] Hotel: " + h.getName()+ "," + "Ranking: " + h.getRanking());
        }
        if(hotels.size() == 0) return null;
        return hotels;
    }

    /**
     * updateHotel
     * Aggiorna l'hotel passato come parametro
     * 
     * @param hotel : hotel da aggiornare
     */
    public synchronized void updateHotel(Hotel hotel){
        if(this.hotels.containsKey(hotel.getCity().toLowerCase())){
            LinkedBlockingQueue<Hotel> cityHotels = this.hotels.get(hotel.getCity().toLowerCase());
            for(Hotel h : cityHotels){
                if(h.getName().toLowerCase().equals(hotel.getName().toLowerCase())){
                    cityHotels.remove(h);
                    cityHotels.add(hotel);
                    hotels.put(hotel.getCity().toLowerCase(), cityHotels);
                    return;
                }
            }
        }
        // ArrayList<Hotel> ht = new ArrayList<>(this.hotels);
        // for(int i = 0; i < ht.size(); i++){
        //     if(ht.get(i).getName().toLowerCase().equals(hotel.getName().toLowerCase()) 
        //     && ht.get(i).getCity().toLowerCase().equals(hotel.getCity().toLowerCase())){
        //         ht.set(i, hotel);
        //         return;
        //     }
        // }
        // this.setHotels(new LinkedBlockingQueue<Hotel>(ht));
    }


    /**
     * getAllUsers
     * Restituisce tutti gli utenti presenti nella cache
     * @return
     */
    public LinkedBlockingQueue<User> getAllUsers() {
        LinkedBlockingQueue<User> us = new LinkedBlockingQueue<>();
        for(String username : users.keySet()){
            us.add(users.get(username));
        }
        return us;
    }
    
    /**
     * getAllHotels
     * Restituisce tutti gli hotel presenti nella cache
     * @return
     */
    public LinkedBlockingQueue<Hotel> getAllHotels() {
        LinkedBlockingQueue<Hotel> hs = new LinkedBlockingQueue<>();
        for(String city : hotels.keySet()){
            for(Hotel h : hotels.get(city)){
                hs.add(h);
            }
        }
        return hs;
    }
    // private void setHotels(LinkedBlockingQueue<Hotel> hotels) {
    //     this.hotels = new ConcurrentHashMap<>();
    //     for(Hotel h : hotels) {
    //         this.addHotel(h);
    //     }
    // }
    
    /**
     * updateBestHotelsMap
     * Aggiorna la mappa degli hotel migliori
     * @param hotel
     */
    public void updateBestHotelsMap(Hotel hotel){
        //Merging the best hotels from file and cache
        ConcurrentHashMap<String, Hotel> FileBestHotelMap = JsonHandler.getFileBestHotel(ServerMain.getHotelsPath());
        for(String city: FileBestHotelMap.keySet()){
            if(!this.BestHotelMap.containsKey(city)){
                this.BestHotelMap.put(city, FileBestHotelMap.get(city));
            }
        }
        //update the best hotel map
        if(this.BestHotelMap.containsKey(hotel.getCity().toLowerCase())){
            if(this.BestHotelMap.get(hotel.getCity().toLowerCase()).getRanking() < hotel.getRanking()){
                this.BestHotelMap.put(hotel.getCity().toLowerCase(), hotel);
            }
        }else{
            this.BestHotelMap.put(hotel.getCity().toLowerCase(), hotel);
        }
        this.updateBestHotel();
        // if(this.BestHotelMap.containsKey(hotel.getCity().toLowerCase())){
        //     if(this.BestHotelMap.get(hotel.getCity().toLowerCase()).getRanking() < hotel.getRanking()){
        //         this.BestHotelMap.put(hotel.getCity().toLowerCase(), hotel);
        //         //Send the news message via UDP to all the clients in the groups
        //         //ServerMain.sendBestHotelMessage(hotel);
        //     }
        // }else{
        //     this.BestHotelMap.put(hotel.getCity().toLowerCase(), hotel);
        // }
        // for (ConcurrentHashMap.Entry<String, Hotel> entry : this.BestHotelMap.entrySet()) {
        //     System.out.println("[SERVER] Best hotel of " + entry.getKey() + ": " + entry.getValue().getName());
        // }
        // this.updateBestHotel(hotel);
    }

    /**
     * updateBestHotel
     * Aggiorna l'hotel migliore ed invia un messaggio via UDP a tutti i client
     * con il nuovo hotel migliore
     */
    public void updateBestHotel() {
        boolean changed = false;
        for (ConcurrentHashMap.Entry<String, Hotel> entry : this.BestHotelMap.entrySet()) {
            if(this.bestHotel == null || this.bestHotel.getRanking() < entry.getValue().getRanking()){
                this.bestHotel = entry.getValue();
                changed = true;
            }
        }

        if(changed){
            //Send the news message via UDP to all the clients in the groups
            // Prepare message with current date and time
            Date date = new Date();
            String message = String.format("[%s] %s (%s) è diventato il miglior hotel", date.toString(), this.bestHotel.getName(), this.bestHotel.getCity());
    
            // Send UDP message to clients
            ServerMain.sendUDPMessage(message);
    
            System.out.println("[SERVER] Best hotel: " + this.bestHotel.getName());
        }
    }

    int[] getMostLeastReviewedHotel(){
        int[] mostLeast = new int[2]; 
        ArrayList<Hotel> fileHotels = JsonHandler.getAllHotels(ServerMain.getHotelsPath());
        ArrayList<Hotel> hotel = new ArrayList<>();
        for(Hotel h : fileHotels){
            if(this.existsHotel(h.getCity(), h.getName()) != null){
                hotel.add(this.existsHotel(h.getCity(), h.getName()));
            }else{
                hotel.add(h);
            }
        }

        hotel.sort((h1, h2) -> Integer.compare(h2.getReviews().size(), h1.getReviews().size()));
        mostLeast[0] = hotel.get(0).getReviews().size();
        mostLeast[1] = hotel.get(hotel.size()-1).getReviews().size();
        return mostLeast;
    }

    int getLeastReviewedHotel(){
        int leastReviewed = Integer.MAX_VALUE;
        for(String city : hotels.keySet()){
            for(Hotel h : hotels.get(city)){
                if(h.getReviews().size() < leastReviewed){
                    leastReviewed = h.getReviews().size();
                }
            }
        }
        return leastReviewed;
    }

    public void flushCache(){
        this.users = new ConcurrentHashMap<>();
        this.hotels = new ConcurrentHashMap<>();
        this.completedCity = new ConcurrentHashMap<>();
        this.BestHotelMap = new ConcurrentHashMap<>();
        this.bestHotel = null;
    }
}
