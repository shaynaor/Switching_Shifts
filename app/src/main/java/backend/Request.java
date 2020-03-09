package backend;


public class Request {

    /* private data members */

    private String shift_reg_id;
    private String shift_wanted_id;
    private String worker_id;


    /*constructors */
    public Request() {
    }

    public Request(Request s) {

        this.shift_reg_id = s.shift_reg_id;
        this.shift_wanted_id = s.shift_wanted_id;
        this.worker_id=s.worker_id;
    }

    public Request(String shift_reg_id, String shift_wanted_id, String worker_id) {

        this.shift_reg_id = shift_reg_id;
        this.shift_wanted_id = shift_wanted_id;
        this.worker_id=worker_id;

    }

    /* getters and setters*/



    public String getShift_reg_id() {
        return shift_reg_id;
    }
    public String getWorker_id() {
        return worker_id;
    }
    public void setShift_reg_id(String shift_reg_id) {
        this.shift_reg_id = shift_reg_id;
    }

    public String getShift_wanted_id() {
        return shift_wanted_id;
    }

    public void setShift_wanted_id(String shift_wanted_id) {
        this.shift_wanted_id = shift_wanted_id;
    }
}
