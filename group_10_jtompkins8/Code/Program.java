//Main executable to run any of the 4 algorithms to solve the given input graph
//to use: java Program -inst <filename> -alg [BnB|Approx|LS1|LS2] -time <cutoff in seconds> -seed <random seed>

import java.io.*;
import java.util.*;

class Program
{
	public enum Algorithm {BnB, Approx, LS1, LS2};

	private static class Edge
	{
		int u;
		int v;
		public Edge(int _u, int _v)
		{
			if(_u>_v)
			{
				int tmp = _u;
				_u = _v;
				_v = tmp;
			}
			u = _u;
			v = _v;
		}

		public boolean equals(Object obj)
		{
			Edge that = (Edge) obj;
			return this.u == that.u && this.v == that.v;
		}
	}

	public static void main(String[] args)
	{
		//parse parameters
		String filePath = "./data/toy.graph";
		Algorithm algorithm = Algorithm.Approx;

		int wallTimeSeconds = 60;
		int seed = 0;
		for(int i=0;i<args.length;i+=2)
		{
			switch(args[i])
			{
				case "-inst":
					filePath = args[i+1];
					break;
				case "-alg":
					switch(args[i+1])
					{
						case "BnB":
							algorithm = Algorithm.BnB;
							break;
						case "Approx":
							algorithm = Algorithm.Approx;
							break;
						case "LS1":
							algorithm = Algorithm.LS1;
							break;
						case "LS2":
							algorithm = Algorithm.LS2;
							break;
						default:
							System.out.println("Invalid algorithm name. Please choose from [BnB|Approx|LS1|LS2].");
							System.exit(1);
					}
					break;
				case "-time":
					wallTimeSeconds = Integer.parseInt(args[i+1]);
					break;
				case "-seed":
					seed = Integer.parseInt(args[i+1]);
					break;
				default:
					System.out.println("Invalid parameter. Expected parameters: -inst <filename> -alg [BnB|Approx|LS1|LS2] -time <cutoff in seconds> -seed <random seed>");
			}
		}

		//generate graph instance
		Graph graph = new Graph(filePath);
		//solve
		Solver solver = null;
		ArrayList<String> updates = new ArrayList<>();
		switch(algorithm)
		{
			case BnB:
				solver = new BranchAndBoundSolver(graph, seed, wallTimeSeconds, updates);
				break;
			case Approx:
				solver = new ApproxSolver(graph, seed, wallTimeSeconds, updates);
				break;
			case LS1:
				solver = new LSSimple2(graph, seed, wallTimeSeconds, updates);
				break;
			case LS2:
				solver = new LSHillClimbing(graph, seed, wallTimeSeconds, updates);//replace with ls2
				break;
		}


		Graph.Vertex[] solution = solver.findMVC();
		String fnPrefix = "results/" + filePath.split("/")[1].split("[.]")[0] + "_" + algorithm + "_" + wallTimeSeconds;
		if (algorithm == Algorithm.LS1 || algorithm == Algorithm.LS2) {
			fnPrefix += "_" + seed;
		}
		String prefix = "";
		String solnString = "";
		for (Graph.Vertex v : solution) {
			solnString += prefix + v.label;
			prefix = ",";
		}
		String traceString = "";
		for (String update : updates) {
			traceString += update + "\n";
		}

		writeToFile(fnPrefix + ".sol", "" + solution.length + "\n" + solnString + "\n");
		writeToFile(fnPrefix + ".trace", traceString);

		int n = graph.vertices.length;
		HashSet<Integer> edges = new HashSet<Integer>();
		for (Graph.Vertex u : graph.vertices)
			for (Graph.Vertex v : u.neighbors)
			{
				int x = u.label>v.label ? v.label*n+u.label : u.label*n+v.label;
				edges.add(x);
			}
		HashSet<Integer> coveredEdges = new HashSet<Integer>();
		for (Graph.Vertex u : solution)
			for (Graph.Vertex v : u.neighbors)
			{
				int x = u.label>v.label ? v.label*n+u.label : u.label*n+v.label;
				coveredEdges.add(x);
			}
		System.out.println("    " + coveredEdges.size() + " of "+edges.size() + " edges have been covered, with " + solution.length + " vertices");

	}

	public static void writeToFile(String fn, String contents) {
		try {
			FileOutputStream fos = new FileOutputStream(fn);
			fos.write(contents.getBytes());
			fos.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
