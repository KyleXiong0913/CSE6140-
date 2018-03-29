//parent class for all the algorithms. Defines basic functions that all algorithms use. The function findMVC() must be implemented by the child.

import java.util.ArrayList;
import java.util.*;

public abstract class Solver {
	int percent = -1;
	long wallTimeSeconds;
	long startTime;
	Random random;
	Graph g;
	ArrayList<String> updates;

	public abstract Graph.Vertex[] findMVC();

	public Solver(Graph g, int seed, long wallTimeSeconds, ArrayList<String> updates) {
		random = new Random(seed);
		this.g = g;
		this.wallTimeSeconds = wallTimeSeconds;
		this.updates = updates;
		this.startTime = System.currentTimeMillis();
	}

	public void printStatusLine(int soln) {
		int elapsed = (int)(System.currentTimeMillis()-startTime)/1000;
		int curPercent = (elapsed*100)/((int)wallTimeSeconds);
		if (curPercent > percent) {
			String completed = "";
			String notCompleted = "";
			for (int i = 0; i < curPercent; i++) {
				completed += "#";
			}
			for (int i = 0; i < (100-curPercent); i++) {
				notCompleted += "-";
			}
			System.out.print("\r    [" + completed + notCompleted + "] (" + curPercent + "%) (" + soln + " used)");
			percent = curPercent;
		}
	}

	public void clearStatusLine() {
		System.out.print("\r                                                                                                                                      \r");
	}

	public int edgesMissing(Collection<Graph.Vertex> c, Graph g) {
		int edgesMissing = 0;
		for (Graph.Vertex v : g.vertices) {
			for (Graph.Vertex neighbor : v.neighbors) {
				if (!c.contains(v) && !c.contains(neighbor)) {
					edgesMissing++;
				}
			}
		}
		edgesMissing = edgesMissing / 2;
		return edgesMissing;
	}

	public boolean isCover(Collection<Graph.Vertex> c, Graph g) {
		return 0 == edgesMissing(c, g);
	}

	public boolean outOfTime() {
		return !(System.currentTimeMillis()-startTime < wallTimeSeconds*1000);
	}

	public void updateBest(int best) {
		updates.add("" + (System.currentTimeMillis()-startTime)/1000 + " " + best);
	}
}
