//Algorithm 2 of 4: Approximation algorithm. Solves the VC problem using a Greedy method

import java.util.*;

public class ApproxSolver extends Solver {

	public ApproxSolver(Graph g, int seed, long wallTimeSeconds, ArrayList<String> updates) {
		super(g, seed, wallTimeSeconds, updates);
	}

	public Graph.Vertex[] findMVC() {
		PriorityQueue<Graph.Vertex> pq = new PriorityQueue<Graph.Vertex>(g.vertices.length,Collections.reverseOrder());
		for(Graph.Vertex v : g.vertices)
		pq.add(v);
		HashSet<Integer> vc = new HashSet<Integer>();
		while(!pq.isEmpty()) {
			Graph.Vertex me = pq.poll();
			Graph.Vertex friend = null;
			for (Graph.Vertex v : me.neighbors) {
				boolean iAmGood = true;
				boolean friendIsGood = true;
				if (vc.contains(v.label)) {
					iAmGood = false;
				}
				friend = v;
				friendIsGood = !vc.contains(friend.label);
				if (iAmGood && friendIsGood) {
					vc.add(me.label);
					vc.add(friend.label);
					break;
				}
			}
		}
		ArrayList<Graph.Vertex> solution = new ArrayList<Graph.Vertex>();
		for(Graph.Vertex v : g.vertices) {
			if(vc.contains(v.label)) {
				solution.add(v);
			}
		}
		updateBest(solution.size());
		return solution.toArray(new Graph.Vertex[solution.size()]);
	}
}
