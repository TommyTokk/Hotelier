package Server;

import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
public class Hotel implements Serializable{
    private int id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private ArrayList<String> services;
    private int rate;
    private Ratings ratings;
    private double ranking;
    // LinkedList<Ratings>: data structure to store the reviews
    // To be used to calculate the local ranking of the hotel
    // To be ordered by most recent date
    private LinkedList<Reviews> reviews = new LinkedList<Reviews>();

    public Hotel(int id, String name, String description, String city, String phone, ArrayList<String> services,
            int rate, Ratings ratings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.city = city;
        this.phone = phone;
        this.services = services;
        this.rate = rate;
        this.ratings = ratings;
    }

    public Hotel(int id, String name, String description, String city, String phone, ArrayList<String> services,
            int rate, Double ranking, Ratings ratings, LinkedList<Reviews> reviews) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.city = city;
        this.phone = phone;
        this.services = services;
        this.rate = rate;
        this.ranking = ranking;
        this.ratings = ratings;
        this.reviews = reviews;
    }

    // getter and setter
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public int getRate() {
        return rate;
    }

    public Ratings getRatings() {
        return ratings;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * Calcola la media delle recensioni e aggiorna i ratings dell'hotel
     */
    public void setRatings() {
        int rate = 0, cleaning = 0, position = 0, services = 0, quality = 0;

        for (Reviews rs : this.reviews) {
            rate += rs.getSynVote();
            cleaning += rs.getRatings().getCleaning();
            position += rs.getRatings().getPosition();
            services += rs.getRatings().getServices();
            quality += rs.getRatings().getQuality();
        }

        this.rate = rate / (this.reviews.size());
        Ratings r = new Ratings(cleaning / (this.reviews.size()), position / (this.reviews.size()),
                services / (this.reviews.size()), quality / (this.reviews.size()));
        this.ratings = r;
        this.updateRanking();
        ServerCache.getInstance().updateHotel(this);
        ServerCache.getInstance().updateBestHotelsMap(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String servicesToString() {
        String servicesString = "";
        for (String s : this.services) {
            servicesString += "  - " + s + "\n";
        }
        return servicesString;
    }

    public String servicesToJson() {
        String servicesString = "";
        for (String s : this.services) {
            servicesString += "\"" + s + "\",";
            if (s.equals(this.services.get(this.services.size() - 1))) {
                servicesString = servicesString.substring(0, servicesString.length() - 1);
            }
        }
        return servicesString;
    }

    public synchronized void addReview(Reviews review) {
        if (this.reviews == null)
            this.reviews = new LinkedList<Reviews>();
        this.reviews.add(review);
    }

    public ArrayList<Reviews> getReviews() {
        return new ArrayList<Reviews>(this.reviews);
    }

    /**
     * Aggiorna il ranking dell'hotel in base alle recensioni
     * La valutazione tiene conto di:
     * - Attualità delle recensioni
     * - Qualità delle recensioni
     * - Quantità delle recensioni
     */
    public void updateRanking() {
        // Calcolo del punteggio medio ponderato
        double finalRanking = 0.0;
        int numeroRecensioni = this.reviews.size();
        // clone ratings and sort by date
        // use min max normalize

        this.reviews.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        Date oldestDate = this.reviews.getLast().getDate();
        Date actualDate = new Date();

        int[] reviewsCount = ServerCache.getInstance().getMostLeastReviewedHotel();
        int mostReviewd = reviewsCount[0];
        int leastReviewd = reviewsCount[1];
        double normalizedReviews = 0.0;


        if(this.reviews.size() > 1){
            for (Reviews r : reviews) {
                double rateWeight = normalize(r.getDate().getTime(), actualDate.getTime(), oldestDate.getTime());
                finalRanking += r.getSynVote() * rateWeight;
            }
        }else{
            finalRanking = this.reviews.get(0).getSynVote();
        }

        // Normalizzazione del numero di recensioni
        if (mostReviewd != leastReviewd) {
            normalizedReviews = normalize(numeroRecensioni, mostReviewd, leastReviewd);
        } else {
            normalizedReviews = 0.0;
        }

        finalRanking += normalizedReviews;

        System.out.println("Punteggio medio: " + finalRanking);
        this.setRanking(finalRanking);
    }

    public String toString() {
        return "Name: " + this.name + "\n"
                + "Description: " + (description == null ? "" : this.description) + "\n"
                + "City: " + this.city + "\n"
                + "Phone: " + this.phone + "\n"
                + "Services:\n" + servicesToString()
                + "Rate: " + this.rateToString() + "\n"
                + "Ratings:\n" + this.ratings.toString();
    }

    public void setCleanings(int cleanings) {
        this.ratings.setCleaning(cleanings);
    }

    public void setPosition(int position) {
        this.ratings.setPosition(position);
    }

    public void setServices(int services) {
        this.ratings.setServices(services);
    }

    public String rateToString() {
        String rateString = "";
        for (int i = 0; i < this.rate; i++) {
            rateString += "★";
        }
        return rateString;
    }

    public void setQuality(int quality) {
        this.ratings.setQuality(quality);
    }

    /**
     * Normalize the value between 0 and 1
     * @param value : value to normalize
     * @param max : max value
     * @param min : min value
     * @return normalized value
     */
    private double normalize(double value, double max, double min) {
        //return (value - min) / (max - min);
        return (value - min) / (max - min);
    }

    public double getRanking() {
        return ranking;
    }

    public void setRanking(double ranking) {
        this.ranking = ranking;
    }

    public int compareTo(Hotel h) {
        if(this.ranking == h.getRanking())
            return (int) Math.ceil(this.reviews.size() - h.getReviews().size());
        else 
            return Double.compare(h.getRanking(), this.getRanking());
    }

}
