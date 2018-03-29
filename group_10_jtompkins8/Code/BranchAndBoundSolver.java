//Algorithm 1 of 4: Branch-and-Bounds. Solves the VC problem using a branch-and-bounds procedure
import java.util.ArrayList;
import java.util.Stack;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Arrays;

public class BranchAndBoundSolver extends Solver {

	public BranchAndBoundSolver(Graph g, int seed, long wallTimeSeconds, ArrayList<String> updates) {
		super(g, seed, wallTimeSeconds, updates);
	}

	public Graph.Vertex[] findMVC() {
		Arrays.sort(g.vertices, Collections.reverseOrder());
		int lowerVertexSolution = 9999999;
		Solution incumbantSolution = new Solution();
		incumbantSolution.solver = this;
		PriorityQueue<Solution> frontier = new PriorityQueue<>();
		frontier.add(incumbantSolution);

		while(!outOfTime()) {
			if (frontier.size() > 0) {
				this.printStatusLine(incumbantSolution.verticesUsed());
				Solution current = frontier.poll();
                if (current.cachedIsCover()) {
					if (lowerVertexSolution > current.verticesUsed()) {
						updateBest(current.verticesUsed());
						incumbantSolution = current;
						lowerVertexSolution = current.verticesUsed();
					}
				} else {
					if (current.verticesUsed() >= lowerVertexSolution) {
						continue;
					}
					ArrayList<Graph.Vertex> undecided = current.getUnDecided();
					if (undecided.size() == 0) {
						continue;
					}
					HashSet<Graph.Vertex> decided = current.getDecided();
					HashSet<Graph.Vertex> decidedAgainst = new HashSet<>();
					for (Graph.Vertex v : g.vertices) {
						decidedAgainst.add(v);
					}
					for (Graph.Vertex v : undecided) {
						decidedAgainst.remove(v);
					}
					for (Graph.Vertex v : decided) {
						decidedAgainst.remove(v);
					}
					int edgesCovered = g.edges - current.cachedEdgesMissing();
					Integer[] degrees = new Integer[undecided.size()];
					int i = 0;
					for (Graph.Vertex v : undecided) {
						int degree = v.neighbors.size();
						for (Graph.Vertex n : v.neighbors) {
							if (decided.contains(n)) {
								degree--;
							}
						}
						degrees[i++] = degree;
					}
					Graph.Vertex bestUndecided = null;
					int highestDegree = -1;
					for (int index = 0; index < degrees.length; index++) {
						if (degrees[index] > highestDegree) {
							highestDegree = degrees[index];
							bestUndecided = undecided.get(index);
						}
					}
					Arrays.sort(degrees, Collections.reverseOrder());
					int boost = 0;
					while (boost < degrees.length && edgesCovered < g.edges) {
						edgesCovered += degrees[boost];
						boost++;
					}
					if (current.verticesUsed() + boost >= lowerVertexSolution) {
						continue;
					}
					boolean redundant = true;
					boolean canRemove = true;
					for (Graph.Vertex neighbor : bestUndecided.neighbors) {
						if (!decided.contains(neighbor)) {
							redundant = false;
						}
						if (decidedAgainst.contains(neighbor)) {
							canRemove = false;
						}
					}
					if (!redundant) {
						Solution decidedFor = current.softClone();
						decidedFor.decide = bestUndecided;
                        //compute edgesMissing. Look at neighbors of decidedFor!
                        int edgesAdded = 0;
                        for(Graph.Vertex neighbor : bestUndecided.neighbors) {
                            if (!decided.contains(neighbor)) {
                                edgesAdded++;
                            }
                        }
                        decidedFor.edgesMissing = current.cachedEdgesMissing() - edgesAdded;
						frontier.add(decidedFor);
					}
					if (canRemove) {
						Solution decidedNotFor = current.softClone();
						decidedNotFor.edgesMissing = current.cachedEdgesMissing();
						decidedNotFor.toss = bestUndecided;
						frontier.add(decidedNotFor);
					}
				}
			} else {
				break;
			}
		}
		this.clearStatusLine();
		return incumbantSolution.getDecided().toArray(new Graph.Vertex[incumbantSolution.verticesUsed()]);
}

private static class Solution implements Comparable<Solution> {
		static Solver solver;
		int edgesMissing = -1;
		Graph.Vertex decide = null;
		Graph.Vertex toss = null;
		Solution parent = null;
		int verticesUsed = -1;

		public ArrayList<Graph.Vertex> getUnDecided() {
			if (null == parent) {
				ArrayList<Graph.Vertex> unDecided = new ArrayList<>();
				for (Graph.Vertex v : solver.g.vertices) {
					unDecided.add(v);
				}
				return unDecided;
			}
			ArrayList<Graph.Vertex> myUnDecided = parent.getUnDecided();
			if (null != decide) {
				myUnDecided.remove(decide);
			} else {
				myUnDecided.remove(toss);
			}
			return myUnDecided;
		}

		public HashSet<Graph.Vertex> getDecided() {
			if (null == parent) {
				return new HashSet<>();
			}
			HashSet<Graph.Vertex> myDecided = parent.getDecided();
			if (null != decide) {
				myDecided.add(decide);
			}
			return myDecided;
		}

		public int verticesUsed() {
			if (null == parent) {
				return 0;
			}
			if (-1 != verticesUsed) {
				return verticesUsed;
			}
			int verticesUsed = parent.verticesUsed();
			if (decide != null) {
				verticesUsed++;
			}
			return verticesUsed;
		}

		public int compareTo(Solution that) {
			Integer thisEdge = this.cachedEdgesMissing();
			Integer thatEdge = that.cachedEdgesMissing();
			return thisEdge.compareTo(thatEdge);
		}

		public int cachedEdgesMissing() {
			if (edgesMissing != -1) {
				return edgesMissing;
			}
			HashSet<Graph.Vertex> decided = getDecided();
            edgesMissing = solver.edgesMissing(decided, solver.g);
			return edgesMissing;
		}

		public boolean cachedIsCover() {
			return 0 == cachedEdgesMissing();
		}

		public Solution softClone() {
			Solution clone = new Solution();
			clone.parent = this;
			return clone;
		}
	}
}
