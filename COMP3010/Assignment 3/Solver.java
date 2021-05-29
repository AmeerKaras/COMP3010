package assg3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Solver {
	public static int v = 0;// Initialise the number of vertices for a given
							// input as 0
	public static int e = 0;// Initialise the number of connections/edges for a
							// given input as 0
	public static int[][] capMatrix;// Matrix to store capacity
	public static int[][] costMatrix;// Matrix to store cost
	
	public int[] solve_1(String infile) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

		return solve_2(infile);
	}	

	public int[] solve_2(String infile) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

		return solve_3(infile);
	}

	boolean visited[];// Array to store if an edge has been visited
	int n;// Stores the number of nodes
	int cap[][];// Stores the capacity of each edge		
	int flow[][];// Stores the flow between vertices 
	int cost[][];// Stores the cost of flow
	int nN[];// Stores negative nodes to be subtracted from flow
	int dist[];// Stores the distance from specified node
	int cE[]; // Stores the chosen edges

	static final int bigBoi = Integer.MAX_VALUE / 2 - 1; // bigBoi as the infinite value for unvisited vertices 
	
	// Function to obtain the maximum Flow
	int[] bestCostMaxCap(int cap[][], int cost[][], int source, int dest) {
		this.cap = cap;
		this.cost = cost;
		n = cap.length;
		visited = new boolean[n];
		flow = new int[n][n];
		dist = new int[n + 1];
		nN = new int[n];
		cE = new int[n];
		int maxCap = 0;
		int minCost = 0;

		// The code in the following loop is run until there is a path
		// from the source to the sink
		while (findPath(source, dest)) {

			// Set the default amount to infinity 
			int amount = bigBoi;// Infinity
			int amount2 = 0;// Variable to hold potential amount value

			for (int x = dest; x != source; x = nN[x]){	
				if((flow[x][nN[x]] != 0)){// Check for positive flow
					amount = flow[x][nN[x]];
				}else{
					amount2 = cap[nN[x]][x] - flow[nN[x]][x];
				}
				amount = Math.min(amount, amount2);// Set amount to be the lower amount value
			}				
			
			for (int x = dest; x != source; x = nN[x]) {
				if (flow[x][nN[x]] != 0) {// If positive flow+
					flow[x][nN[x]] = flow[x][nN[x]] - amount;
					minCost = minCost - amount * cost[x][nN[x]];
				} else {// If negative flow
					flow[nN[x]][x] = flow[nN[x]][x] + amount;
					minCost = minCost + amount * cost[nN[x]][x];
				}
			}
			maxCap = maxCap + amount;
		}
		// Return pair total cost and sink
		return new int[] { maxCap, minCost };
	}

	
	//This function checks if there is a possible path 
	//from the source to the destination
	boolean findPath(int source, int dest) {

		// Set all visited values to false
		for(int i = 0; i < visited.length; ++i){
			visited[i] = false;
		}

		// Initialise the distance from any vertex to another as infinite (bigBoi)
		for(int i = 0; i < dist.length; ++i){
			dist[i] = bigBoi;
		}
		// Distance from the source node is 0 as we're starting there
		dist[source] = 0;
		// Loop while source vertex hasn't found final vertex
		while (source != n) {
			int b = n;// For holding the best current path
			visited[source] = true;

			for (int k = 0; k < n; k++) {
				// If already found
				if (visited[k])
					continue;
				// Evaluate while there is some flow remaining
				if (flow[k][source] != 0) {
					// Calculate the total value
					int val = dist[source] + cE[source] - cE[k] - cost[k][source];
					// If dist[k] is > minimum value
					if (dist[k] > val) {
						// Update values
						dist[k] = val;
						nN[k] = source;
					}
				}
				if (flow[source][k] < cap[source][k]) {// If statement to ensure that flow does not exceed capacity
					int val = dist[source] + cE[source] - cE[k] + cost[source][k];
					// If dist[k] is > minimum value
					if (dist[k] > val) {
						// Update
						dist[k] = val;
						nN[k] = source;
					}
				}
				// Sets k to the best current path if it's less than previous best
				if (dist[k] < dist[b])
					b = k;
			}
			source = b;
		}
		for (int k = 0; k < n; k++)
			cE[k] = Math.min(cE[k] + dist[k], bigBoi);
		return visited[dest];
	}


	public int[] solve_3(String infile) {
		try {
			readData(infile);
			int ans[] = bestCostMaxCap(capMatrix, costMatrix, 0, v - 1);
			return ans;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


	public void readData(String infile) throws Exception {
		try {
			FileReader f = new FileReader(infile);
			Scanner s = new Scanner(f);
			v = s.nextInt();
			e = s.nextInt();
			// Have a cost and capacity matrix
			int[][] capAdjMatrix = new int[v][v];
			int[][] costAdjMatrix = new int[v][v];

			for (int i = 0; i < e; i++) {
				s.nextLine();
				int start = s.nextInt();
				int end = s.nextInt();
				int capacity = s.nextInt();
				int cost = s.nextInt();
				// Store values is cost and capacity matrices
				capAdjMatrix[start][end] = capacity;
				costAdjMatrix[start][end] = cost;
			}

			capMatrix = capAdjMatrix;
			costMatrix = costAdjMatrix;

			s.close();

		} catch (FileNotFoundException e) {
			System.out.println("mission failed(A3).");
			e.printStackTrace();
		}

	}
}
