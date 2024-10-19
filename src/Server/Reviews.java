package Server;

import java.util.Date;

/**
 * Reviews class
 * This class is used to store the reviews of the users for the hotels
 * It contains:
 * - username: username of the user who wrote the review
 * - synVote: synthetic vote for the hotel == rate in the Hotel class
 * - ratings: ratings for the hotel
 * - date: date of the review
 */


public class Reviews {
    private String username;//username of the user who wrote the review
    private int synVote;//synthetic vote for the hotel == rate in the Hotel class
    private Ratings ratings;//ratings for the hotel
    private Date date;//date of the review

    public Reviews(String username, int synVote, Ratings ratings) {
        this.username = username;
        this.synVote = synVote;
        this.ratings = ratings;
        this.date = new Date();
    }

    public String getUsername() {
        return username;
    }

    public int getSynVote() {
        return synVote;
    }

    public Date getDate() {
        return date;
    }

    public Ratings getRatings() {
        return ratings;
    }

    public String toString() {
        return String.format("  - Username: %s\n  - Synthetic Vote: %d\n  - Ratings: \n%s  - Date: %s\n", this.username, this.synVote, this.date.toString());
    }

    public void setSynVote(int synVote) {
        this.synVote = synVote;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRatings(Ratings ratings) {
        this.ratings = ratings;
    }
}
