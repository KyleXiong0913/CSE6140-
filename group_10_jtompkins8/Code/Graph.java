//Data structure to hold the graph

import java.util.HashSet;
import java.util.HashMap;
import java.io.*;
import java.util.*;

public class Graph {
    Vertex[] vertices;
    int numberOfEdges;
    ArrayList<Integer>[] adj; //adjacency list
    int edges = -1;

    //deprecated
    public Graph(int vertexCount) {
        vertices = new Vertex[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            vertices[i] = new Vertex(i);
        }
    }

    public int getEdgeCount()
    {
        return numberOfEdges;
    }

    public Graph(String filepath)
    {
        try
        {
            //open file
            FileReader f = new FileReader(filepath);
            BufferedReader br = new BufferedReader(f);
            //read first line
            String[] firstLine = br.readLine().split(" ");
            edges = Integer.parseInt(firstLine[1]);
            int vertexCount = Integer.parseInt(firstLine[0]);
            vertices = new Vertex[vertexCount];
            adj = (ArrayList<Integer>[]) new ArrayList[vertexCount+1];
            for(int i=0;i<vertexCount;i++)
            {
                vertices[i] = new Vertex(i+1);
                adj[i+1] = new ArrayList<Integer>();
            }
            //read data
            numberOfEdges = 0;
            String line;
            int label = 0;
            while((line = br.readLine())!=null)
            {
                if(line.length() != 0){
                  String[] splitline = line.split(" ");
                  for(String s : splitline) //add each neighbor to current vertex's neighbor list
                  {
                      //System.out.println("    "+(label+1)+" - "+Integer.parseInt(s)); //debug
                      vertices[label].addNeighbor(vertices[Integer.parseInt(s)-1]);
                      vertices[Integer.parseInt(s)-1].addNeighbor(vertices[label]); //TODO: if the input takes care of this undirectedness, this line is unnecessary
                      adj[label+1].add(Integer.parseInt(s));
                      numberOfEdges++;
                  }
                }
                label++;
            }
            numberOfEdges = numberOfEdges/2; //assuming the input provides all undirected edges i.e. each edge is mentioned twice.
            br.close();
            f.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //debug
    public void printAdjacencyList()
    {
        for(int i=0;i<vertices.length;i++)
        {
            for(Vertex v : vertices[i].neighbors)
                System.out.print(v.label+" ");
            System.out.print("\n");
        }

    }

    public class Vertex implements Comparable<Vertex> {
        HashSet<Vertex> neighbors;
        int label = -1;
        private boolean chosen = false;
        private boolean covered = false;

        public Vertex(int label) {
            this.label = label;
            neighbors = new HashSet<>();
        }

        public int compareTo(Vertex that)
        {
            Integer thisSize = this.neighbors.size();
            Integer thatSize = ((Vertex)that).neighbors.size();
            return thisSize.compareTo(thatSize);
        }

        public void addNeighbor(Vertex v)
        {
            neighbors.add(v);
        }

        public boolean isChosen() {
            return chosen;
        }

        public boolean isCovered() {
            return covered;
        }

        public void choose() {
            chosen = true;
            for (Vertex v : neighbors) {
                v.updateCovered();
            }
        }

        public void unChoose() {
            chosen = false;
            for (Vertex v : neighbors) {
                v.updateCovered();
            }
        }

        public void updateCovered() {
            if (chosen) {
                covered = true;
                return;
            }
            for (Vertex v : neighbors) {
                if (v.isChosen()) {
                    covered = true;
                    return;
                }
            }
        }
    }

    //for testing. remove this.
    public static void main(String[] args)
    {
        Graph g = new Graph("./data/karate.graph");
        //g.printAdjacencyList();
        PriorityQueue<Graph.Vertex> pqs = new PriorityQueue<Graph.Vertex>();

        PriorityQueue<Graph.Vertex> pqr = new PriorityQueue<Graph.Vertex>(g.vertices.length,Collections.reverseOrder());
        for(Graph.Vertex v : g.vertices)
        {
            pqs.add(v);
            pqr.add(v);
        }
        System.out.print("pqs: ");
        while(!pqs.isEmpty())
        {
            System.out.print(pqs.poll().neighbors.size()+" ");
        }
        System.out.print("\n\npqr: ");
        while(!pqr.isEmpty())
        {
            System.out.print(pqr.poll().neighbors.size()+" ");
        }
    }
}
