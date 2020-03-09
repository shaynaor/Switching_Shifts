package backend;

import com.google.firebase.Timestamp;


public class Shift {
    /* private data members */
    private Timestamp date;
    private String type;
    private String role;
    private boolean delete;
    /*constructors */
    public Shift() {}

    public Shift(Shift s) {
        this.date = s.date;
        this.type = s.type;
        this.role = s.role;
        this.delete = s.delete;
    }
    public Shift(Timestamp date, String type, String role, boolean delete) {
        this.date = date;
        this.type = type;
        this.role = role;
        this.delete = delete;
    }

    /* getters and setters*/
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() { return this.role; }

    public void setRole(String role) { this.role = role; }

    public Boolean getDelete(){ return this.delete; }

    public void setDelete(boolean delete) { this.delete = delete; }


}
