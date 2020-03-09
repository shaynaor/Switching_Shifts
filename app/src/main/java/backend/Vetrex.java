package backend;

import java.util.LinkedList;

public class Vetrex{

    /* private data members*/
    private String id;
    private boolean is_user;
    private LinkedList<Vetrex> neg;


    /*constructors */
    public Vetrex(boolean is_user, String id){
        neg= new LinkedList<>();
        this.is_user=is_user;
        this.id=id;
    }

    public Vetrex(Vetrex v){
        neg= new LinkedList<>(v.neg);
        this.is_user=v.is_user;
        this.id=id;
    }

    /* Getters & Setters */
    public String getId() {
        return id;
    }

    public boolean isIs_user() {
        return is_user;
    }

    public  LinkedList<Vetrex> getNeg(){
        return neg;
    }

    /*add vertex v to this neighborhoods list (create edge)*/
    public void addEdge(Vetrex v){
        if (!contains_edge(v))
            this.neg.add(v);
    }
    /*check if there edge from this vertex to v*/
    public boolean contains_edge(Vetrex v){
        for (int i=0; i<this.neg.size(); i++)
            if (neg.get(i).getId().equals(v.getId()))
                return true;
        return false;
    }
    /*remove vertex v to this neighborhoods list (remove edge)*/
    public void remove_edge(Vetrex v){
        for (int i=0; i<this.neg.size(); i++)
            if (neg.get(i).getId().equals(v.getId()))
                neg.remove(i);
    }
    /*check if this vetrex is equal to vetrex v*/
    public boolean equals(Vetrex v){
        return  this.id.equals(v.id);
    }
}
