package Rating;

public class Rate {
    // Instances
    private String id;
    private String username;
    private float rating;
    private String comment;

    //Constructor
    public Rate() {}

    public Rate(String username, float rating, String comment) {
        this.username = username;
        this.rating = rating;
        this.comment = comment;
    }

    //Getters
    public String getUsername() {
        return username;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getId() {
        return id;
    }

    //Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setId(String id) {
        this.id = id;
    }
}
