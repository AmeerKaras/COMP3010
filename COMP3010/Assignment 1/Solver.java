package assg1;
//Ameer Karas
//44948956
import java.util.*;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Solver {

	public static int W = 0;// The width of a given grid
	public static int H = 0;// The Height of a given grid
	public static int N = 0;// The number of unique letters in a given grid
	public static HashMap<String, Integer> areas = new HashMap<>();// Where
																	// letters
																	// and its
																	// corresponding
																	// value are
																	// stored
	public static String[][] arr;// The grid returned by readData
	public static List<Letter> letterSet = new ArrayList<>();// A list of
																// instances of
																// type 'letter'

	public static void main(String[] args) {
		Solver s = new Solver();
		s.solve(null);
	}

	/*
	 * A class and constructor for the cells in the grid. This was primarily
	 * used for the solveBaseCase method, an early solution that solved test
	 * case 2.
	 */

	class Cell {
		int x;
		int y;

		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/*
	 * The Letter class represents the letters in the grid and their values.
	 * These values are used to define the location of the letters, as well as
	 * the number of letters required to make a rectangle. Attached to each
	 * letter is an arraylist of the potential rectangles that it can form, as
	 * well as the bestRectangle, that is, the one used for the solution. By
	 * having a letter class, specific values can be called for each letter in
	 * methods. Additionally, attaching the arraylists for potential rectangles
	 * and best rectangles to a specific letter makes the coding easier.
	 */
	class Letter {
		private String l;
		private int posx;
		private int posy;
		private int num;
		ArrayList<Cell> loc = new ArrayList<>();// array list that stores the
												// location of a cell that
												// contains a letter
		private ArrayList<String[][]> potRectangles;// an array list that holds
													// all potential rectangles
													// for a given letter
		private ArrayList<String[][]> bestRectangle;// an array list that holds
													// the best potential
													// rectangle

		/*
		 * This letter class and constructor identify the letter and number of
		 * cells its rectangle must be. 'l' represents the letter as a string
		 * value, and the 'y' and 'x' values are its location in the grid The
		 * 'n' value is the amount of cells of the rectangle
		 */
		public Letter(String l, int y, int x, int n) {
			this.l = l;
			this.posy = y;
			this.posx = x;
			this.num = n;
		}
	}

	/**
	 * The solve method accepts a String containing the path to the input file
	 * of a space partition problem as described in the assignment specification
	 * and returns a two-dimensional String array containing the solution.
	 * 
	 * @param infile
	 *            the input file containing the problem
	 * @return solution to the space partition problem
	 */
	public String[][] solve(String infile) {

		/*
		 * Here is a simple try-catch block for readData. If you don't know what
		 * try/catch and exceptions are, you don't have to worry about it for
		 * this unit, but it would be good if you can learn a bit of it.
		 */
		try {
			// We begin by removing all previous values of letterSet.
			letterSet.removeAll(letterSet);
			// From the input file, we use define a grid using the arr value.
			arr = readData(infile);
			Iterator<Letter> T = letterSet.iterator();
			List<String[][]> rectangleList = new ArrayList<String[][]>();
			while (T.hasNext()) {
				// Define a letter l as an instance of the letter class.
				// Using this instance, we call the possibleRectangle and
				// bestRect methods with respect to l
				// to generate the array list of potential rectangles that can
				// be formed by l and, from the list of
				// potential rectangles, choose the best.
				Letter l = T.next();
				possibleRectangle(l);
				bestRect(l);
				// We also define the dimensions of the grid using the arr
				// value.
				String[][] grid = new String[arr.length][arr[0].length];
				// The solution for the base case (also my first solution) is
				// called for grids that contain one
				// unique letter value, i.e. one rectangle
				if (N == 1) {
					solveBaseCase(l, l.posy, l.posx);
				}
				for (int i = 0; i < l.bestRectangle.size(); i++) {
					// Next, we make the grid equal the best rectangle for the
					// given letter, as defined by the bestRectangle method, and
					// add it to the rectangleList
					grid = l.bestRectangle.get(i);
					rectangleList.add(grid);
				}
			}
			for (int i = 0; i < rectangleList.size(); i++) {
				String[][] rect = rectangleList.get(i);
				// Create a new grid (with dimensions defined by H and W values)
				// and fill it with 0's.
				String[][] tmp = new String[H][W];
				tmp = zeroFill(tmp);
				for (int j = 0; j < rectangleList.size(); j++) {
					String[][] rect1 = rectangleList.get(j);
					if (!rect.equals(rect1)) {
						if (checkGrid(rect1, tmp) == true) {
							tmp = addGrid(rect1, tmp);
						}
					}
				}
				// If the tmp grid is completely filled and contains
				// no more 0's, make arr equal tmp.
				if (complete(tmp) == true) {
					arr = tmp;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		printGrid(arr);
		return arr;
	}

	/*
	 * The following method was the solve method for the first 2 test cases
	 */
	public boolean solveBaseCase(Letter l, int y, int x) {
		// Using the cell class, we make the 'tmp' variable the cell in the grid
		// that holds the location of the
		// letter we are currently looking at.
		Cell tmp = new Cell(y, x);
		// The following if statement ensures that the cell is within the
		// boundaries of the
		// defined height (H) and width (W) values.
		if (y >= H || y < 0 || x >= W || x < 0) {
			y = l.posy;
			x = l.posx;
			return false;
		} // The following else if statement calls the checkLocation method to
			// ensure that the
			// letter we are currently working on is in the position of the tmp
			// cell in the grid.
		else if (checkLocation(tmp) == true) {
			return false;
		} else {// If the cell we are looking at is within the boundaries and
				// does not contain a letter, we:
			l.loc.add(tmp);// add the current cell to the location array list
			arr[y][x] = l.l;// add the current letter to the current cell
			l.num--;// decrement the amount of cells that this letter's
					// rectangle contains
		}
		// Now we try to solve the rectangle by shifting around the letter's
		// location in the grid
		return solveBaseCase(l, y + 1, x) || solveBaseCase(l, y - 1, x) || solveBaseCase(l, y, x - 1)
				|| solveBaseCase(l, y, x + 1);
	}

	/*
	 * The printGrid method, as the name implies, prints the grid. This method
	 * was very helpful and used to visualise the grid to understand the
	 * effectiveness of the solution.
	 */
	public static void printGrid(String[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println("");
			for (int j = 0; j < arr[0].length; j++) {
				System.out.print(arr[i][j] + " ");
			}
		}
		System.out.println("");
	}

	/*
	 * The complete method checks whether the grid has been completed. This is
	 * accomplished by checking whether every 'empty' space (denoted by a "0")
	 * has been replaced by a letter.
	 */
	public static boolean complete(String[][] grid) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				if (grid[i][j] == "0") {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * The checkLocation method makes sure that the letter we are examining is
	 * in the location we expect it to be.
	 */
	public boolean checkLocation(Cell tmp) {
		for (Letter x : letterSet) {
			for (int i = 0; i < x.loc.size(); i++) {
				Cell j = x.loc.get(i);
				if (tmp.x == j.x && tmp.y == j.y) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Adds a grid, gridArr, defined as a 2d String array
	 */
	public static String[][] addGrid(String[][] grid, String[][] gridArr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				String l = grid[i][j];
				// if the grid we are looking at:
				// has no 0's,
				// is not currently stored in the areas HashMap, and
				// is not equal to String l
				if (!grid[i][j].equals("0") && !areas.containsKey(gridArr[i][j]) && !gridArr[i][j].equals(l)) {
					gridArr[i][j] = grid[i][j];
				}
			}
		} // then we return it.
			// In the main solver function, this method is used to add
			// rectangles to the tmp 2d array
		return gridArr;
	}

	/*
	 * the checkGrid method is used to determine whether our 'areas' HashMap
	 * contains the current grid and, if so, that it is not regarded as an empty
	 * grid (full of 0's)
	 */
	public static boolean checkGrid(String[][] grid, String[][] gridArr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				if (areas.containsKey(grid[i][j])) {
					if (!gridArr[i][j].equals("0")) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/*
	 * The zeroFill method is used to make a given grid into a grid of 0's, i.e.
	 * empty cells
	 */
	public static String[][] zeroFill(String[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = "0";
			}
		}
		return grid;
	}

	/*
	 * The insert method is used to populate a given grid with instances of the
	 * given letter.
	 */
	public static String[][] insert(Letter l, String[][] Grid, int y, int x, int h, int w) {
		Grid[y][x] = l.l;
		for (int i = y; i < y + h; i++) {
			for (int j = x; j < x + w; j++) {
				Grid[i][j] = l.l;
			}
		}
		return Grid;
	}

	/*
	 * The subjectLetter method checks to ensure that the sub-grid we are
	 * operating on contains the letter that is the subject of the sub-grid
	 */
	public static boolean subjectLetter(Letter l, String[][] grid) {
		boolean base = true;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				// This if statement checks:
				// That the letter does not occur outside of the grid it is
				// supposed to
				// That the area HashMap contains the given grid
				// That the grid's cells contain the subject letter
				if (!arr[i][j].equals(l.l) && areas.containsKey(arr[i][j]) && grid[i][j].equals(l.l)) {
					base = false;
				}
			}
		}
		return base;
	}

	public static void bestRect(Letter l) {
		ArrayList<String[][]> potentList = l.potRectangles;
		ArrayList<String[][]> rectGrid = new ArrayList<String[][]>();
		// The following for loop iterates through the array list
		// of potential rectangles
		// for the given letter.
		for (int k = 0; k < potentList.size(); k++) {
			String[][] rect = potentList.get(k);
			// We now define the width and height of the potential rectangles
			int w = rect[0].length - 1;
			int h = rect.length - 1;
			for (int i = 0; i < rect.length; i++) {
				for (int j = 0; j < rect[0].length; j++) {
					// The x and y coordinates are defined as the coordinates of
					// the given letter, shifted vertically by the
					// i value and shifted horizontally by the j value
					int posx = l.posx - j;
					int posy = l.posy - i;
					// This if statement ensures that the rectangle can exist
					// within the boundaries of the grid.
					if (posx >= 0 && posx + w < W && posy >= 0 && posy + h < H) {
						String[][] grid = new String[H][W];
						// For the 2d array grid, we fill all of its cells with
						// 0's
						grid = zeroFill(grid);
						// We then insert the letter's trial rectangle into the
						// tmpGrid
						String[][] tmpGrid = insert(l, grid, posy, posx, rect.length, rect[0].length);
						// And call the subjectLetter method to ensure that:
						// The letter does not occur outside of the sub-grid;
						// tmpGrid is the sub-grid in this case
						// The grid is contained in the areas HashMap
						// That the grid's cells contain the subject letter
						boolean checkBase = subjectLetter(l, tmpGrid);
						if (checkBase == true) {
							rectGrid.add(tmpGrid);
						}
					}
				}
			}
		}
		// Finally, we can decide that the tmpGrid that we have examined and
		// added to rectGrid is the best option for the letter's rectangle
		l.bestRectangle = rectGrid;
	}

	public static void possibleRectangle(Letter l) {
		ArrayList<String[][]> possibleRect = new ArrayList<String[][]>();
		// The larger amount of potential rectangles will be for letters that
		// have a
		// larger number of cells to occupy than 1, so we use this
		// if statement to filter them
		if (l.num != 1) {
			for (int i = 1; i <= l.num; i++) {
				// To determine all possible formations that a rectangle that
				// occupies N cells can make,
				// we divide the number of cells by every number from 1 until N
				int N = l.num / i;
				int x = i;
				if (N * x == l.num) {
					String[][] tmp = new String[N][x];
					for (int m = 0; m < N; m++) {
						for (int o = 0; o < x; o++) {
							// We make arrays of all possible size for the given
							// N value
							tmp[m][o] = String.valueOf(l.num);
						}
					}
					int h = tmp.length - 1;
					int w = tmp[0].length - 1;
					// These 2d arrays represent the potential rectangles,
					// defined by the vertical value 'h', and the horizontal
					// value 'w'.
					// The following if statement ensures that the potential
					// rectangle we have just made adheres to the boundary of
					// the grid.
					if (l.posy + h < H && l.posx + w <= W || l.posy - h >= 0 && l.posx - w >= 0) {
						possibleRect.add(tmp);
					}
				}
			}
		} else {// We now examine the case where we have a 1 x 1 rectangle
			String[][] tmp = new String[1][1];
			tmp[0][0] = l.l;
			possibleRect.add(tmp);
		} // the possible rectangles are added to the potential rectangles array
			// list
		l.potRectangles = possibleRect;
	}

	/**
	 * The readData method accepts a String containing the path to the input
	 * file containing the details of the problem (size of grid, number of
	 * sections, etc). Please see the assignment specification for more
	 * information on the input format.
	 * 
	 * You should use this method to populate this class with the information
	 * that you need to solve the problem.
	 * 
	 * I also recommend the use of Scanner class (I have written a little bit
	 * for you to start with), but you may choose to use something else.
	 * 
	 * @param infile
	 *            the input file containing the problem
	 * @throws Exception
	 *             if file is not found or there is input reading error
	 */
	public String[][] readData(String infile) throws Exception {
		String[][] grid = new String[W][H];
		try {
			File txt = new File(infile);
			Scanner input = new Scanner(txt);
			W = input.nextInt();
			H = input.nextInt();
			N = input.nextInt();
			input.nextLine();

			String[][] tmp = new String[H][W];
			int letterNumber = 0;
			// The code in the following while loop reads each letter and its
			// corresponding value into a hashMap of String, Integer pairs
			// while the number of letters is greater than the letterNumber
			// value
			while (letterNumber < N) {
				String d = input.nextLine();
				String e[] = d.split(" ", 2);
				areas.put(e[0], Integer.parseInt(e[1].replaceAll("\\s+", "")));
				letterNumber++;
			}
			// The following nested while loops read the height and width of the
			// given grids
			int i = 0;
			while (i < H) {
				String data = input.nextLine();
				String st = data.replaceAll("\\s+", "");
				int j = 0;
				while (j < W) {
					String val = String.valueOf(st.charAt(j));
					if (areas.containsKey(val)) {
						// Instances of letter are made and added to a List of
						// letter values.
						// This List is called 'letterSet'
						Letter x = new Letter(val, i, j, areas.get(val));
						letterSet.add(x);
					}
					tmp[i][j] = val;
					j++;
				}
				i++;
			}
			input.close();
			grid = tmp;
		} catch (FileNotFoundException e) {
			System.out.println("mission failed");
			e.printStackTrace();
		}
		return grid;
	}

}
