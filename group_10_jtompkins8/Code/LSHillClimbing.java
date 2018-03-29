//Algorithm 4 of 4: Hill Climbing. Solves the VC problem using a hill climbing local search procedure

import java.util.*;

public class LSHillClimbing extends Solver {
  ArrayList<Graph.Vertex> incumbantSolution = new ArrayList<Graph.Vertex>(); //holds the current best feasible solution
  ArrayList<Graph.Vertex> includedVerticesList = new ArrayList<Graph.Vertex>(); //list for helping to randomly choose a vertex to remove
  ArrayList<Graph.Vertex> excludedVerticesList = new ArrayList<Graph.Vertex>(); //list for helping to randomly choose a vertex to add
  HashSet<Integer> excludedVerticesSet = new HashSet<Integer>(); //hashmap for quickly getting the number of edges affected by a vertex addition/removal



  public LSHillClimbing(Graph g, int seed, long wallTimeSeconds, ArrayList<String> updates) {
    super(g, seed, wallTimeSeconds, updates);
  }

  private int getToggledEdges(int victim) {
    int loss = 0;
    for(Integer v : g.adj[victim])
      if(excludedVerticesSet.contains(v))
        loss++;
    return loss;
  }

  public Graph.Vertex[] findMVC() {
    includedVerticesList = new ArrayList<Graph.Vertex>(Arrays.asList(g.vertices));
    excludedVerticesList.clear();
    excludedVerticesSet.clear();
    int totalEdges = g.getEdgeCount();
    int edgesCovered = totalEdges;
    incumbantSolution.addAll(includedVerticesList);
    while(!outOfTime()) { //restarts
      while (!outOfTime()) { //find one solution
        this.printStatusLine(incumbantSolution.size());
        if (edgesCovered == totalEdges) {
          if (incumbantSolution.size() == 0 || includedVerticesList.size() < incumbantSolution.size()) {
            incumbantSolution = (ArrayList<Graph.Vertex>) includedVerticesList.clone();
            updateBest(incumbantSolution.size());
          }
          Graph.Vertex victim = includedVerticesList.remove((int) random.nextDouble() * includedVerticesList.size());
          edgesCovered -= getToggledEdges(victim.label);
          excludedVerticesList.add(victim);
          excludedVerticesSet.add(victim.label);
        }

        boolean found = false;
        LOOP: for (Graph.Vertex victim : includedVerticesList.toArray(new Graph.Vertex[includedVerticesList.size()])) {
          for (Graph.Vertex winner : excludedVerticesList.toArray(new Graph.Vertex[excludedVerticesList.size()])) {
            int oldCovered = edgesCovered;
            excludedVerticesList.add(victim);
            excludedVerticesList.remove(winner);
            includedVerticesList.add(winner);
            includedVerticesList.remove(victim);
            edgesCovered -= getToggledEdges(victim.label);
            excludedVerticesSet.add(victim.label);
            excludedVerticesSet.remove(winner.label);
            edgesCovered += getToggledEdges(winner.label);
            if (oldCovered > edgesCovered || random.nextDouble() > 0.9) { //rolling back a solution as it caused harm
              edgesCovered = oldCovered;
              excludedVerticesList.remove(victim);
              excludedVerticesList.add(winner);
              includedVerticesList.remove(winner);
              includedVerticesList.add(victim);
              excludedVerticesSet.remove(victim.label);
              excludedVerticesSet.add(winner.label);
            } else {
              found = true;
              break LOOP;
            }
          }
        }
        if (!found) {
          break;
        }
      }
    }
    this.clearStatusLine();
    return incumbantSolution.toArray(new Graph.Vertex[incumbantSolution.size()]);
  }
}
