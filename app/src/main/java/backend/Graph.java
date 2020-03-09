package backend;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    /* private data member*/
    private List<Vetrex> graph;

    /*constructors */
    public Graph() {
        graph = new ArrayList<Vetrex>();
    }
    public Graph(Graph g) {
        graph = new ArrayList<>(g.getGraph());
    }
    /* get graph*/
    public List<Vetrex> getGraph(){
        return graph;
    }

    public void add_vetrex(Vetrex v) {
        if (!this.contains_vetrex(v))
            graph.add(v);
    }
    /* check if the graph contain this vetrex*/
    public boolean contains_vetrex(Vetrex v) {
        for (int i = 0; i < graph.size(); i++)
        {
            if (graph.get(i).getId().equals(v.getId()))
                return true;
        }
        return false;
    }

    /* return the vertex index at the graph, -1 if the vertex is not exists*/
    public int get_vetrex_index(Vetrex v){
        for (int i = 0; i < graph.size(); i++) {
            if (graph.get(i).getId().equals(v.getId()))
                return i;
        }
        return -1;
    }
    /* get the shift that the worker is regeister to, the worker, and the shift that the worker wants,
    and create 2 vetrex:
    shift_reg---> worker
    worker---> shift_wanted
     */
    public void add_edge(Vetrex shift_reg, Vetrex worker, Vetrex shift_wanted){
        add_vetrex(shift_reg);
        add_vetrex(worker);
        add_vetrex(shift_wanted);


        int worker_index=get_vetrex_index(worker);
        int shift_reg_index=get_vetrex_index(shift_reg);

        graph.get(shift_reg_index).addEdge(worker);
        graph.get(worker_index).addEdge(shift_wanted);
    }
    public void add_edge(Vetrex shift_reg, Vetrex worker){
        add_vetrex(shift_reg);
        add_vetrex(worker);
        int shift_reg_index=get_vetrex_index(shift_reg);
        graph.get(shift_reg_index).addEdge(worker);

    }

    /* gets two vertex and remove the edge between them (if exists) */
    public void remove_edge(Vetrex v, Vetrex u){
        int v_index=get_vetrex_index(v);
        graph.get(v_index).remove_edge(u);
    }
    /*print the graph to the console*/
    public void printGraph(){
        for (Vetrex v: graph){
            System.out.print(v.getId()+" : ");
            for (Vetrex u: v.getNeg())
                System.out.println(u.getId()+" , ");
            System.out.println();
        }
    }

    public int graph_size(){
        return graph.size();
    }
}
