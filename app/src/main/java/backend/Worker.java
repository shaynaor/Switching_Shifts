package backend;


import com.google.firebase.Timestamp;

import java.util.Date;

public class Worker {
    /* Private data members */
    private String first_name;
    private String last_name;
    private String role;
    private String email;
    private Timestamp birthday;
    private boolean first_login;


    /* default constructor */
    public Worker(){}


    public Worker(String first_name, String last_name, String role, String email){
        this.first_name = first_name;
        this.last_name = last_name;
        this.role = role;
        this.email = email;
        birthday = new Timestamp(new Date());
        this.first_login = true;
    }

    /* Getters & Setters */
    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public boolean isFirst_login() { return first_login; }

    public void setFirst_login(boolean first_login) { this.first_login = first_login; }
}
