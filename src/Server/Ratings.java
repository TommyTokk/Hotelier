package Server;

public class Ratings implements java.io.Serializable{
    private int cleaning;
    private int position;
    private int services;
    private int quality;

    public Ratings(int cleaning, int position, int services, int quality) {
        this.cleaning = cleaning;
        this.position = position;
        this.services = services;
        this.quality = quality;
    }

    public int getCleaning() {
        return this.cleaning;
    }

    public int getPosition() {
        return this.position;
    }

    public int getServices() {
        return this.services;
    }

    public int getQuality() {
        return this.quality;
    }


    public void setCleaning(int cleaning) {
        this.cleaning = cleaning;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setServices(int services) {
        this.services = services;
    }

    public void setQuality(int quality) {
        this.quality = services;
    }

    private String rateConverter(int rate) {
        String result = "";
        for (int i = 0; i < rate; i++) {
            result += "★";
        }
        return result;
    }

    public String toString() {
        //format output string like dotted list
        return String.format("  - Cleaning: %s\n  - Position: %s\n  - Services: %s\n  - Quality: %s\n", this.rateConverter(cleaning), this.rateConverter(position), this.rateConverter(services), this.rateConverter(quality));
    }

    public String toJson() {
        return String.format("{\"cleaning\": %d, \"position\": %d, \"services\": %d, \"quality\": %d}", cleaning, position, services, quality);
    }
}
