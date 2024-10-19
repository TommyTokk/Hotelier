package Server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

/**
 * JsonHandler
 * Classe usata per gestire la conversione e manipolazione di file JSON
 */

public class JsonHandler {
    private static Gson gson;

    public JsonHandler() {
    }

    public static ArrayList<Hotel> getAllHotels(String hotelsPath) {
        ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> hotels = JsonHandler.readHotelFromFile(hotelsPath);
        ArrayList<Hotel> allHotels = new ArrayList<Hotel>();
        for (String city : hotels.keySet()) {
            allHotels.addAll(hotels.get(city));
        }
        return allHotels;
    }

    /**
     * Metodo per l'aggiornamento di un utente dalla cache al file JSON
     * Il metodo legge gli utenti dal file JSON, aggiorna l'utente passato come
     * parametro
     * tramite il metodo updateUser e scrive gli utenti aggiornati sul file JSON
     * 
     * @param usersPath
     * @param user
     */
    public static void updateUser(String usersPath, User user) {
        ConcurrentHashMap<String, User> users = JsonHandler.readUsersFromFile(usersPath);
        if (users.containsKey(user.getUsername())) {
            users.put(user.getUsername(), user);
        } else {
            users.put(user.getUsername(), user);
        }
        JsonHandler.writeUserstoFile(usersPath, users);
        // boolean isSet = false;
        // String data = JsonHandler.readFromFile(usersPath);
        // gson = new Gson();
        // Type userListType = new TypeToken<ArrayList<User>>() {
        // }.getType();
        // ArrayList<User> users = gson.fromJson(data, userListType);

        // for (int i = 0; i < users.size(); i++) {
        // User u = users.get(i);
        // if (u.getUsername().equals(user.getUsername())) {
        // users.set(i, user);
        // isSet = true;
        // break;
        // }
        // }
        // if (!isSet)
        // users.add(user);
        // JsonHandler.writeToFile(usersPath, gson.toJson(users));
    }

    /**
     * Legge il contenuto di un file JSON e lo restituisce come stringa.
     *
     * Il metodo utilizza un `FileInputStream` e un `BufferedInputStream` per
     * leggere il file in modo efficiente.
     *
     * @param path Percorso del file JSON da leggere.
     * @return Stringa contenente il contenuto del file JSON.
     * @deprecated Questo metodo è stato deprecato e sostituito da
     *             `readUsersFromFile` e `readHotelFromFile`.
     *             Si consiglia di utilizzare questi metodi al posto di questo.
     */
    @Deprecated
    public static String readFromFile(String path) {
        try {
            System.out.println("[SERVER - readFromFile] Reading from file: " + path);
            InputStream in = new FileInputStream(path);
            BufferedInputStream bis = new BufferedInputStream(in);
            StringBuilder sb = new StringBuilder();
            while (bis.available() > 0) {
                sb.append((char) bis.read());
            }
            bis.close();
            return sb.toString();
        } catch (Exception e) {
            System.err.printf("[SERVER - readFromFile] Error: %s\n", e.getMessage());
            return null;
        }
    }

    /**
     * Metodo per estrarre un utente dal file JSON
     * Il metodo legge gli utenti dal file JSON e restituisce l'utente con username
     * e password passati come parametri
     * @param username
     * @param password
     * @param usersPath
     * @return
     */
    public static User getUser(String username, String password, String usersPath) {
        ConcurrentHashMap<String, User> users = JsonHandler.readUsersFromFile(usersPath);
        if (users.containsKey(username)) {
            return users.get(username);
        }
        return null;
        // String data = JsonHandler.readFromFile(usersPath);
        // gson = new Gson();
        // Type userListType = new TypeToken<ArrayList<User>>() {
        // }.getType();
        // ArrayList<User> users = gson.fromJson(data, userListType);

        // for (User u : users) {
        // if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
        // return u;
        // }
        // }
        // return null;
    }

    public static Hotel getHotel(String name, String city, String hotelsPath) {
        ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> hotels = JsonHandler.readHotelFromFile(hotelsPath);
        if (hotels.containsKey(city.toLowerCase())) {
            for (Hotel h : hotels.get(city.toLowerCase())) {
                if (h.getName().toLowerCase().equals(name.toLowerCase())) {
                    return h;
                }
            }
        }
        return null;
        // String data = JsonHandler.readFromFile(hotelsPath);
        // gson = new Gson();

        // Type hotelListType = new TypeToken<ArrayList<Hotel>>() {
        // }.getType();

        // ArrayList<Hotel> hotels = gson.fromJson(data, hotelListType);
        // for(Hotel h : hotels){
        // if(h.getName().toLowerCase().equals(name.toLowerCase()) &&
        // h.getCity().toLowerCase().equals(city.toLowerCase())){
        // return h;
        // }
        // }
        // return null;
    }

    /**
     * Metodo per ottenere, dal file JSON, la lista degli hotel presenti nella città
     * passata come parametro; null altrimenti.
     * Viene costruita una ConcurrentHashMap contenente gli hotel divisi per città
     * così da velocizzare la ricerca degli hotel per città.
     * @param city
     * @param hotelsPath
     * @return ArrayList con gli hotel presenti nella città "city", null altrimenti
     */

    public static ArrayList<Hotel> getHotelsByCity(String city, String hotelsPath) {
        ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> fileHotels = JsonHandler.readHotelFromFile(hotelsPath);
        if (fileHotels.containsKey(city.toLowerCase())) {
            return new ArrayList<Hotel>(fileHotels.get(city.toLowerCase()));
        }
        return null;

        // String data = JsonHandler.readFromFile(hotelsPath);
        // gson = new Gson();

        // Type hotelListType = new TypeToken<ArrayList<Hotel>>() {
        // }.getType();

        // ArrayList<Hotel> hotels = gson.fromJson(data, hotelListType);
        // ArrayList<Hotel> hotelsByCity = new ArrayList<Hotel>();
        // for(Hotel h : hotels){
        // if(h.getCity().toLowerCase().equals(city.toLowerCase())){
        // hotelsByCity.add(h);
        // }
        // }
        // return hotelsByCity;
    }

    public static ConcurrentHashMap<String, Hotel> getFileBestHotel(String filePath){
        ConcurrentHashMap<String, Hotel> bestHotels = new ConcurrentHashMap<String, Hotel>();
        ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> Filehotels = JsonHandler.readHotelFromFile(filePath);
        for(String city: Filehotels.keySet()){
            LinkedBlockingQueue<Hotel> hotels = Filehotels.get(city);
            bestHotels.put(city, hotels.peek());
        }
        return bestHotels;
    }

    /**
     * Metodo per l'aggiornamento di un hotel dalla cache al file JSON
     * Il metodo legge gli hotel dal file JSON, aggiorna l'hotel passato come
     * parametro tramite il metodo updateHotel e scrive gli hotel aggiornati sul file JSON
     * tramite il metodo writeHotelsToFile
     * @param hotelsPath
     * @param hotel
     */
    public static void updateHotel(String hotelsPath, Hotel hotel) {
        ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> hotels = JsonHandler.readHotelFromFile(hotelsPath);

        if (hotels.containsKey(hotel.getCity().toLowerCase())) {
            LinkedBlockingQueue<Hotel> list = hotels.get(hotel.getCity().toLowerCase());
            for (Hotel h : list) {
                if (h.getName().toLowerCase().equals(hotel.getName().toLowerCase())) {
                    list.remove(h);
                    list.add(hotel);
                    break;
                }
            }
        } else {
            LinkedBlockingQueue<Hotel> list = new LinkedBlockingQueue<Hotel>();
            list.add(hotel);
            hotels.put(hotel.getCity().toLowerCase(), list);
        }
        JsonHandler.writeHotelsToFile(hotelsPath, hotels);
    }

    /**
     * Metodo per la scrittura di una stringa su un file
     * Il metodo scrive la stringa passata come parametro sul file specificato
     * 
     * @param path
     * @param data
     * @deprecated Questo metodo è stato deprecato e sostituito da
     *            `writeUserstoFile` e `writeHotelsToFile`.
     *           Si consiglia di utilizzare questi metodi al posto di questo.
     */
    @Deprecated
    public static void writeToFile(String path, String data) {
        try {
            System.out.println("[SERVER - writeToFile] Writing to file: " + path);
            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(data.getBytes());
            bos.close();
        } catch (Exception e) {
            System.err.printf("[SERVER - writeToFile] Error: %s\n", e.getMessage());
        }
    }

    /**
     * Metodo per la lettura di un file JSON contenente informazioni sugli hotel
     * Il metodo legge gli hotel dal file JSON tramite l'uso di un JsonReader.
     * Effettua il parsing del file e restituisce una ConcurrentHashMap contenente
     * gli hotel divisi per città.
     * 
     * @param filePath
     * @return ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>>, null se il file non esiste
     */

    public static ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> readHotelFromFile(String filePath) {
        JsonReader reader;
        int id = 0;
        String name = "";
        String description = "";
        String city = "";
        String phone = "";
        ArrayList<String> services = null;
        int rate = 0;
        Double ranking = 0.0;


        int cleaning = 0, position = 0, serv = 0, quality = 0;

        // Review attribute
        String username = "";
        int synVote = 0;
        Ratings ratings = null;
        Date date = new Date();
        Reviews review = null;
        LinkedList<Reviews> reviews = new LinkedList<Reviews>();

        ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> hotels = new ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>>();
        try {
            reader = new JsonReader(new java.io.FileReader(filePath));
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    if (key.equals("id")) {
                        id = reader.nextInt();
                    } else if (key.equals("name")) {
                        name = reader.nextString();
                    } else if (key.equals("description")) {
                        description = reader.nextString();
                    } else if (key.equals("city")) {
                        city = reader.nextString();
                    } else if (key.equals("phone")) {
                        phone = reader.nextString();
                    } else if (key.equals("services")) {
                        reader.beginArray();
                        services = new ArrayList<String>();
                        while (reader.hasNext()) {
                            String service = reader.nextString();
                            services.add(service);
                        }
                        // System.out.println(services);
                        reader.endArray();
                    } else if (key.equals("rate")) {
                        rate = reader.nextInt();
                    } else if (key.equals("ranking")) {
                        ranking = reader.nextDouble();
                    } else if (key.equals("ratings")) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String key2 = reader.nextName();
                            if (key2.equals("cleaning")) {
                                cleaning = reader.nextInt();
                            } else if (key2.equals("position")) {
                                position = reader.nextInt();
                            } else if (key2.equals("services")) {
                                serv = reader.nextInt();
                            } else if (key2.equals("quality")) {
                                quality = reader.nextInt();
                            }
                        }
                        reader.endObject();
                    } else if (key.equals("reviews")) {
                        reviews = new LinkedList<Reviews>();
                        reader.beginArray();
                        while (reader.hasNext()) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String key2 = reader.nextName();
                                if (key2.equals("username")) {
                                    username = reader.nextString();
                                } else if (key2.equals("SynVote")) {
                                    synVote = reader.nextInt();
                                } else if (key2.equals("ratings")) {
                                    reader.beginObject();
                                    while (reader.hasNext()) {
                                        String key3 = reader.nextName();
                                        if (key3.equals("cleaning")) {
                                            cleaning = reader.nextInt();
                                        } else if (key3.equals("position")) {
                                            position = reader.nextInt();
                                        } else if (key3.equals("services")) {
                                            serv = reader.nextInt();
                                        } else if (key3.equals("quality")) {
                                            quality = reader.nextInt();
                                        }
                                    }
                                    reader.endObject();
                                } else if (key2.equals("date")) {
                                    String dateStr = reader.nextString();
                                    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                                            Locale.ENGLISH);
                                    date = formatter.parse(dateStr);
                                }
                            }
                            reader.endObject();
                            ratings = new Ratings(cleaning, position, serv, quality);
                            review = new Reviews(username, synVote, ratings);
                            if (date != null) {
                                review.setDate(date);
                            }
                            reviews.add(review);
                        }
                        reader.endArray();
                    }
                }
                reader.endObject();
                // create hotel
                Hotel hotel = new Hotel(id, name, description, city, phone, services, rate, ranking,
                        new Ratings(cleaning, position, serv, quality), reviews);
                // add hotel to hotels
                if (hotels.containsKey(city.toLowerCase())) {
                    hotels.get(city.toLowerCase()).add(hotel);
                } else {
                    LinkedBlockingQueue<Hotel> list = new LinkedBlockingQueue<Hotel>();
                    list.add(hotel);
                    hotels.put(city.toLowerCase(), list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hotels;
    }

    /**
     * Metodo per la lettura di un file JSON contenente informazioni sugli utenti
     * Il metodo legge gli utenti dal file JSON tramite l'uso di un JsonReader.
     * Effettua il parsing del file e restituisce una ConcurrentHashMap contenente
     * gli utenti
     * 
     * @param userFilePath
     * @return ConcurrentHashMap<String, User>, null se il file non esiste
     */
    public static ConcurrentHashMap<String, User> readUsersFromFile(String userFilePath) {
        String username = "";
        String password = "";
        int points = 0;

        String badgeName = "";
        String badgeDescription = "";
        Badge badge = null;
        ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();
        JsonReader reader;

        try {
            reader = new JsonReader(new java.io.FileReader(userFilePath));
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    if (key.equals("username")) {
                        username = reader.nextString();
                    } else if (key.equals("password")) {
                        password = reader.nextString();
                    } else if (key.equals("points")) {
                        points = reader.nextInt();
                    } else if (key.equals("badge")) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String key2 = reader.nextName();
                            if (key2.equals("title")) {
                                badgeName = reader.nextString();
                            } else if (key2.equals("description")) {
                                badgeDescription = reader.nextString();
                            }
                        }
                        reader.endObject();
                        badge = new Badge(badgeName, badgeDescription);
                    }
                }
                reader.endObject();
                User user = new User(username, password, points, badge);
                users.put(username, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Metodo per la scrittura degli hotel su un file JSON
     * Il metodo scrive gli hotel su un file JSON tramite un FileWriter
     * 
     * @param filePath
     * @param hotels
     */
    public static void writeHotelsToFile(String filePath,
        ConcurrentHashMap<String, LinkedBlockingQueue<Hotel>> hotels) {
        FileWriter writer = null;
        gson = new Gson();
        try {
            writer = new FileWriter(filePath);
            writer.write("[");
            boolean isFirst = true; // Flag to track the first element
            for (String key : hotels.keySet()) {

                LinkedBlockingQueue<Hotel> list = hotels.get(key);
                //sort hotels by ranking
                ArrayList<Hotel> hotelsByCity = new ArrayList<Hotel>(list);
                hotelsByCity.sort((h1, h2) -> Double.compare(h2.getRanking(), h1.getRanking()));
                list = new LinkedBlockingQueue<Hotel>(hotelsByCity);

                for (Hotel hotel : list) {
                    if (!isFirst) {
                        writer.write(","); // Add comma only after the first element
                    }
                    isFirst = false;
                    JsonObject hotelObj = new JsonObject();
                    hotelObj.addProperty("id", hotel.getId());
                    hotelObj.addProperty("name", hotel.getName());
                    hotelObj.addProperty("description", hotel.getDescription());
                    hotelObj.addProperty("city", hotel.getCity());
                    hotelObj.addProperty("phone", hotel.getPhone());
                    JsonArray services = new JsonArray();
                    for (String service : hotel.getServices()) {
                        services.add(service);
                    }
                    hotelObj.add("services", services);
                    hotelObj.addProperty("rate", hotel.getRate());
                    hotelObj.addProperty("ranking", hotel.getRanking());
                    JsonObject ratings = new JsonObject();
                    ratings.addProperty("cleaning", hotel.getRatings().getCleaning());
                    ratings.addProperty("position", hotel.getRatings().getPosition());
                    ratings.addProperty("services", hotel.getRatings().getServices());
                    ratings.addProperty("quality", hotel.getRatings().getQuality());
                    hotelObj.add("ratings", ratings);
                    JsonArray reviews = new JsonArray();
                    for (Reviews review : hotel.getReviews()) {
                        JsonObject reviewObj = new JsonObject();
                        reviewObj.addProperty("username", review.getUsername());
                        reviewObj.addProperty("SynVote", review.getSynVote());
                        JsonObject ratingsObj = new JsonObject();
                        ratingsObj.addProperty("cleaning", review.getRatings().getCleaning());
                        ratingsObj.addProperty("position", review.getRatings().getPosition());
                        ratingsObj.addProperty("services", review.getRatings().getServices());
                        ratingsObj.addProperty("quality", review.getRatings().getQuality());
                        reviewObj.add("ratings", ratingsObj);
                        reviewObj.addProperty("date", review.getDate().toString());
                        reviews.add(reviewObj);
                    }
                    hotelObj.add("reviews", reviews);
                    writer.write(gson.toJson(hotelObj));
                }
            }
            writer.write("]");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Metodo per la scrittura degli utenti su un file JSON
     * Il metodo scrive gli utenti su un file JSON tramite un FileWriter
     * 
     * @param usersFilePath
     * @param users
     */

    public static void writeUserstoFile(String usersFilePath, ConcurrentHashMap<String, User> users) {
        FileWriter writer = null;
        gson = new Gson();

        try {
            writer = new FileWriter(usersFilePath);

            writer.write("[");

            boolean isFirst = true; // Flag to track the first element
            for (String key : users.keySet()) {
                if (!isFirst) {
                    writer.write(","); // Add comma only after the first element
                }
                isFirst = false;

                User user = users.get(key);
                JsonObject userObj = new JsonObject();
                userObj.addProperty("username", user.getUsername());
                userObj.addProperty("password", user.getPassword());
                userObj.addProperty("points", user.getPoints());
                JsonObject badgeObj = new JsonObject();
                badgeObj.addProperty("title", user.getBadge().getTitle());
                badgeObj.addProperty("description", user.getBadge().getDescription());
                userObj.add("badge", badgeObj);

                writer.write(gson.toJson(userObj)); // Write the user object
            }

            writer.write("]");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure the writer is closed even in case of exceptions
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
