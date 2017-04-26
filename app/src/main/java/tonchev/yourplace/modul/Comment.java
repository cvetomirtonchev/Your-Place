package tonchev.yourplace.modul;

import java.io.Serializable;

/**
 * Created by Цветомир on 3.4.2017 г..
 */

public class Comment implements Serializable{
    private String user;
    private String text;
    private String rating;

    public Comment(String user, String text, String rating) {
        this.user = user;
        this.text = text;
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
