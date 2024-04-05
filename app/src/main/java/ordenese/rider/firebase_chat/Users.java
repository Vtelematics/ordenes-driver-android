package ordenese.rider.firebase_chat;

public class Users {
    String user_name,image,uid,email,token;

    public Users(String user_name, String uid, String email,String time,String type) {
        this.user_name = user_name;
        this.uid = uid;
        this.email = email;
    }

    public Users(){

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getuid() {
        return uid;
    }

    public void setuid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
