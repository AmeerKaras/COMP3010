package assg2;
//Ameer Karas, 44948956
import java.io.*;
import java.util.*;

public class Solver {
	public static int lots = 0;//Initialise the number of lots for a given input as 0
	public static int bids = 0;//Initialise the number of bids for a given input as 0
	public static List<bid> bidList = new ArrayList<>();//A list to store instances of type 'bid'
	
	public class bid{
		int i, start, end, price;
		
		bid(int i, int start, int end, int price){
			this.i = i;
			this.start = start;
			this.end = end;
			this.price = price;
		}
	}
	
	/**
	 * You can use this to test your program without running the jUnit test,
	 * and you can use your own input file. You can of course also make your
	 * own tests and add it to the jUnit tests.
	 */
	public static void main(String[] args) {
		Solver m = new Solver();
		int answer = m.solve(null);
		System.out.println(answer);
	}

	//This function uses linear search to find the index of the last bid
	//that doesn't conflict with the current bid; 
	//In this case, int i of the functino call
	public static int lastBid(List<bid> jobs, int i){
		//This method takes a list of type bid for a given input and finds
		//the index of the last bid such that there is no overlap for the given job		
		for (int j = i - 1; j >= 0; j--) {
			//The following if statement checks that the end of job j is
			//before the start of job i; ensures no overlapping bids
			if (jobs.get(j).end < jobs.get(i).start) {
				return j;
			}
		}
		return -1;
	}
	
		// Method to find the maximum price of non-overlapping bids 
		public static int maxPrice(List<bid> bid){
			//Sorts bids in increasing order with respect to end time
			Collections.sort(bid, Comparator.comparingInt(x -> x.end));
			//Let n be the number of bids
			int n = bid.size();
			//Dynamic programming part: create a table to store the 
			//maximum price for the first i bids
			int[] maxPrice = new int[n];
			//We need the first bid (the one we are currently looking at to reach the maximum price possible.
			//So we add the first bid, and then proceed from the second 
			maxPrice[0] = bid.get(0).price;
			//Then fill maxPrice[] table in bottom-up manner from the second index
			for (int i = 1; i < n; i++)
			{
				//Find the last bid that doesn't overlap with the current bid
				int last = lastBid(bid, i);
				//Add the current bid to the list of its compatible bids
				int curr = bid.get(i).price;
				if (last != -1) {
					curr += maxPrice[last];
				}
				//Add the maximum price by including or excluding current job
				maxPrice[i] = Math.max(curr, maxPrice[i - 1]);
			}
			return maxPrice[n - 1];
		}

	/** The solve method accepts a String containing the 
	 * path to the input file for the problem (as described
	 * in the assignment specification) and returns an integer
	 * denoting the maximum income 
	 * 
	 * @param infile the file containing the input
	 * @return maximum income for this set of input
	 */
	
	public int solve(String infile) {
		int turtleNotSus = 0;//if fail = 0
		try {
			readData(infile);
			turtleNotSus = maxPrice(bidList);
			
			//System.out.println("testing");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return turtleNotSus;
	}

	/**
	 * The readData method accepts a String containing the 
	 * path to the input file for the problem.
	 * Please see the assignment specification for more information
	 * on the input format.
	 * 
	 * You should use this method to populate this class with 
	 * the information that you need to solve the problem.
	 * 
	 * @param infile the input file containing the problem
	 * @throws Exception if file is not found or if there is an input reading error
	 */
   	public void readData(String infile) throws Exception {
   		try{
   			FileReader f = new FileReader(infile);
   			Scanner s = new Scanner(f);
   			lots = s.nextInt();
   			bids = s.nextInt();
   			bidList.clear();
   			int j = 0;
   			//The following while loop reads each value
   			//for:[i start end price] and uses the values read
   			//to populate the list of bids, 'bidList'
   			while(j<bids){
   				s.nextLine();
   				int i = s.nextInt();
   				int start = s.nextInt();
   				int end = s.nextInt();
   				int price = s.nextInt();
   				bid tmp = new bid(i, start, end, price);//create an instance of the bid class
   				bidList.add(tmp);//add the values from the instance to the list of bids
   				j++;
   			}
   		}catch (FileNotFoundException e){
   			System.out.println("mission failed(A2).");
   			e.printStackTrace();
   		}

	}


}
