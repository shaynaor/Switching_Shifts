package backend;


import java.util.Arrays;

import java.util.Stack;

public class DFS {
    /* Private data members */
    private final int WHITE = 1, GRAY = 2, BLACK = 3, NIL = -1;// colors
    private int[] first;
    private int[] last;
    private int[] pred;
    private int[] color;
    private int time, size;
    private Graph graph;
    private int startCycle, endCycle;
    private boolean hasCycle;
    /*constructor */
    public DFS(Graph g) {
        size = g.getGraph().size();
        pred = new int[size];
        color = new int[size];
        first = new int[size];
        last = new int[size];
        this.graph = new Graph(g);
    }
    /*DFS method*/
    public void dfs(int s) {
        for (int i = 0; i < size; i++) {
            color[i] = WHITE;
            pred[i] = NIL;
            first[i] = 0;
            last[i] = 0;
        }
        hasCycle = false;
        startCycle = NIL;
        endCycle = NIL;
        time = 0;
        visit(s);
    }
    /*visit method*/
    private void visit(int u) {
        color[u] = GRAY;
        first[u] = ++time;
        Vetrex vet = graph.getGraph().get(u);
        for (Vetrex v : graph.getGraph().get(u).getNeg()) {
            int v_index = graph.get_vetrex_index(v);
            if (!hasCycle && color[v_index] == GRAY && pred[u] != v_index) {
                hasCycle = true;
                startCycle = u;
                endCycle = v_index;
            }
            if (color[v_index] == WHITE) {
                color[v_index] = GRAY;
                pred[v_index] = u;
                visit(v_index);
            }
            color[u] = BLACK;
            last[u] = ++time;

        }
    }
    /* get path method*/

    public Stack<Vetrex> dfsPath(int v, int u) {
        Stack<Vetrex> ans = new Stack<Vetrex>();
        dfs(u);
        if (color[v] != WHITE) {
            ans.push(graph.getGraph().get(v));

            while (v != u) {
                v = pred[v];

                ans.push(graph.getGraph().get(v));

            }
        }
        return ans;
    }

    /*return cycle method*/

    public Stack<Vetrex> dfsCycle() {
        Stack<Vetrex> ans = new Stack<Vetrex>();
        boolean firstCycle = false;
        for (int i = 0; !firstCycle && i < size; i++) {
            dfs(i);
            if (hasCycle) {
                firstCycle = true;
                dfs(startCycle);
                ans = dfsPath(startCycle, endCycle);

            }
        }
        return ans;
    }

    public static void main(String[] args) {
        Graph G= new Graph();
        Vetrex SM= new Vetrex(false,"9.1.2020EveningChef");
        Vetrex eti= new Vetrex(true,"uW6bRWyYQOQSu6Pyx4H6LKOn");
        Vetrex MM= new Vetrex(false, "10.1.2020MorningChef");
        Vetrex yaara= new Vetrex(true, "kikkb1EnI9WD7kFItkrugcIX");
        Vetrex SM2=new Vetrex(false,"9.1.2020EveningChef");
        Vetrex MM2= new Vetrex(false, "10.1.2020MorningChef");

        G.add_edge(SM, eti, MM);
        G.add_edge(MM2,yaara,SM2);

        System.out.println("graph size:"+G.graph_size());
        G.printGraph();


   }


}
