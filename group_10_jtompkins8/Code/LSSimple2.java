//Algorithm 3 of 4: Random search with tabu memory. Solves the VC problem using a simple local search procedure with tabu memory to prevent looping

import java.util.*;

public class LSSimple2 extends Solver {
  ArrayList<Graph.Vertex> incumbantSolution = new ArrayList<Graph.Vertex>(); //holds the current best feasible solution
  ArrayList<Graph.Vertex> includedVerticesList = new ArrayList<Graph.Vertex>(); //list for helping to randomly choose a vertex to remove
  ArrayList<Graph.Vertex> excludedVerticesList = new ArrayList<Graph.Vertex>(); //list for helping to randomly choose a vertex to add
  HashSet<Integer> excludedVerticesSet = new HashSet<Integer>(); //hashmap for quickly getting the number of edges affected by a vertex addition/removal
  Queue<Integer> tabuMemory = new LinkedList<Integer>(); //tabu search memory
  final int TABU_MEMORY_SIZE = 6;

  public LSSimple2(Graph g, int seed, long wallTimeSeconds, ArrayList<String> updates) {
    super(g, seed, wallTimeSeconds, updates);
  }

  private int getLostEdges(int victim)
  {
    int loss = 0;
    for(Integer v : g.adj[victim])
      if(excludedVerticesSet.contains(v))
        loss++;
    return loss;
  }

  private int getGainedEdges(int winner)
  {
    int gained = 0;
    for(Integer v : g.adj[winner])
      if(excludedVerticesSet.contains(v))
        gained++;
    return gained;
  }

  private void addToTabuMemory(int label)
  {
  	tabuMemory.add(label);
  	if(tabuMemory.size()>6) tabuMemory.remove();
  }

  public Graph.Vertex[] findMVC() {
    //start with a vertex cover of all vertices
    includedVerticesList = new ArrayList<Graph.Vertex>(Arrays.asList(g.vertices));
    //includedVerticesList.addAll(g.vertices);
    int totalEdges = g.getEdgeCount();
    int edgesCovered = totalEdges; //initially all edges are covered
    incumbantSolution.addAll(includedVerticesList);


    //for (Graph.Vertex v : g.vertices) //start with a vertex cover of all vertices
    //{
    //  includedVertices.add(v);
    //}

    //now do while we have time left...
    while (!outOfTime()) {
      this.printStatusLine(incumbantSolution.size());
      if (edgesCovered == totalEdges) //a solution with the current k has been reached. save it.
      {
        incumbantSolution = (ArrayList<Graph.Vertex>) includedVerticesList.clone();
        //incumbantSolution.clear();
        //incumbantSolution.addAll(includedVertices);
        updateBest(incumbantSolution.size());
        //now try getting a solution for k-1, starting by randomly removing a vertex from the cover
        Graph.Vertex victim = includedVerticesList.remove((int) random.nextDouble() * includedVerticesList.size());
        edgesCovered -= getLostEdges(victim.label);
        excludedVerticesList.add(victim);
        excludedVerticesSet.add(victim.label);
        tabuMemory.clear();
      }

      //randomly choose vertex to leave/enter vertex cover
      Graph.Vertex victim = includedVerticesList.remove((int) random.nextDouble() * includedVerticesList.size());
      int winnerIndex;
      do{
      	winnerIndex = (int) random.nextDouble() * excludedVerticesList.size();
      }while(excludedVerticesList.size()>TABU_MEMORY_SIZE && tabuMemory.contains(excludedVerticesList.get(winnerIndex).label));
      Graph.Vertex winner = excludedVerticesList.remove(winnerIndex);
      excludedVerticesList.add(victim);
      addToTabuMemory(victim.label);
      excludedVerticesList.remove(winner);
      includedVerticesList.add(winner);
      includedVerticesList.remove(victim);
      edgesCovered -= getLostEdges(victim.label);
      excludedVerticesSet.add(victim.label);
      excludedVerticesSet.remove(winner.label);
      edgesCovered += getGainedEdges(winner.label);
    }
    this.clearStatusLine();
    return incumbantSolution.toArray(new Graph.Vertex[incumbantSolution.size()]);
  }
}
