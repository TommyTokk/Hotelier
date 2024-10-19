package Server;


public class User {
    private String username;
    private String password;
    private int points;
    private Badge badge ;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.points = 0;
        this.setBadge(0);
    }

    public User(String username, String password, int points, Badge badge) {
        this.username = username;
        this.password = password;
        this.points = points;
        if(badge == null) {
            this.setBadge(points);
        } else {
            this.badge = badge;
        }
    }


    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public int getPoints() {
        return this.points;
    }

    public Badge getBadge() {
        return this.badge;
    }

    public void setBadge(int points) {
        if(points == 0)
            this.badge = new Badge("", "Non hai effettuato nessuna recensione. Recensisci un hotel per ottenere il tuo primo badge");
        else
        if (points > 0 && points < 100) {
            this.badge = new Badge("Recensore", "Complimenti!! Sei diventato un recensore");
        }else if(points >= 100 && points < 300){
            this.badge = new Badge("Recensore esperto", "Complimenti!! hai recensito più di cinque hotel");
        }else if(points >= 300 && points < 400){
            this.badge = new Badge("Contributore esperto", "Complimenti!! hai recensito più di quindici hotel");
        }else if(points >= 400){
            this.badge = new Badge("Contributore super", "Complimenti!! hai recensito più di venticinque hotel");
        }
    }

    public void addPoints(int points) {
        this.points += points > 1000 ? 1000 : points;
        System.out.printf("[SERVER] points successfully added\n");
        this.setBadge(this.points);
    }
}
