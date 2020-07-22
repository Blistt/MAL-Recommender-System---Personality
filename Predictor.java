import java.util.*;
import java.io.*;

public class Predictor {
	//FILE READER - creates an array of Strings by reading them from a textfile
	public static String[] reader(String filename) throws FileNotFoundException, IOException {
		File file = new File(filename);
		Scanner reader = new Scanner(file);
		int counter = 0;
		
		//Counts total number of lines in file
		while (reader.hasNextLine()) {
			reader.nextLine();
			counter++;
		}
		reader.close();
		
		//Initializes array of users and populates it with each line in the file
		String [] STR = new String[counter];
		reader = new Scanner(file);
		for (int i = 0; i < STR.length; i++) {
			STR[i] = reader.nextLine();			
		}
		
		reader.close();

		return STR;
	}
	
	
	//ANIME NAMES FILLER  - Populates array of users with all anime names from array of anime names
	public static user[] namer(String[] u, String[] a, String[]p){
		
		user[] users = new user[u.length];
	
		for (int i = 0; i < users.length; i++) {
			users[i] = new user(u[i]);
			users[i].anime = new anime[a.length];
			users[i].persona = new persona[p.length];
									
			for (int j = 0; j < users[i].anime.length; j++) {
				users[i].anime[j] = new anime();
				users[i].anime[j].name = a[j];
				users[i].anime[j].position = j;
			}
			
			for (int j = 0; j < users[i].persona.length; j++) {
				users[i].persona[j] = new persona();
				users[i].persona[j].name = a[j];
			}
		}
		return users;
	}

	
	//UTILITY MATRIX - populates ratings from user array by reading them from textfile
	public static user[] rater(user[] U, String str, String strr) throws IOException {
		File file = new File(str);
		Scanner reader = new Scanner(file);
		int i = 0;
		
		while (reader.hasNextLine()) {
			for (int j = 0; j < U[i].anime.length; j++) {
				U[i].anime[j].rating = Double.parseDouble(reader.nextLine());
			}
			System.out.println("populating ratings: " + (i + 1));
			i++;
		}	
		reader.close();
		
		file = new File(strr);
		reader = new Scanner(file);
		i = 0;
		
		while (reader.hasNextLine()) {
			for (int j = 0; j < U[i].persona.length; j++) {
				U[i].persona[j].rating = Double.parseDouble(reader.nextLine());
			}
			System.out.println("populating personality: " + (i + 1));
			i++;
		}	
		reader.close();
		
		
		return U;
	}
		
	
		//Simser - populates users' sims
		public static void simser(user[] U) throws FileNotFoundException {
			//Initializes reader variables
			File inFile = new File("PersoSims.txt");
			Scanner reader1 = new Scanner(inFile);
			Scanner reader2 = new Scanner(inFile);
			String str = "";
			String[]substr = new String[2];
			
			
			
			
			//Crates array of all sims nums
			double[][] simsNum = new double[U.length][];
			
			//Creates array of all sims names
			String[][] simsName = new String[U.length][];
			
			//Creates arrat of all sims positions
			int[][] simsPos = new int[U.length][];
			
			//Populates arrays with all the sims with their names
			for (int i = 0; i < simsNum.length; i++) {						
				
				simsNum[i] = new double[U.length - 1];
				simsName[i] = new String[U.length - 1];
				simsPos[i] = new int[U.length - 1];
				
//				System.out.println(counter);
				
				for (int k = 0; k < simsNum[i].length; k++) {
					
					str = reader2.nextLine();
										
					substr = str.split(" ");
					simsNum[i][k] = Double.parseDouble(substr[0]);
					simsName[i][k] = substr[1];
					simsPos[i][k] = Integer.parseInt(substr[2]);
				}
			}
			
			
			//Populates target user's neighbors
			for (int i = 0; i < U.length; i++) {
				U[i].sim = new similarity[100];
		
				//Loop stops once 
				for (int j = 0; j < U[i].sim.length; j++) {
					U[i].sim[j] = new similarity(simsNum[i][j]);
					U[i].sim[j].name = simsName[i][j];
					U[i].sim[j].pos = simsPos[i][j];
						
				}
		
				System.out.println("populating similarity: " + (i + 1));
			}
			
			reader1.close();
			reader2.close();
		}
		
		//MEANER - calculates all users's mean rating
		public static void meaner(user[] U) {
			
			for (int i = 0; i < U.length; i++){
				//Stores target user's mean rating
				double mean1 = 0.0;
		
				//Keeps track of target user's number of watched shows
				double tWatchedCounter = 0.0;
				
				
				//Calculates sum of rating scores provided by target user
				for (int j = 0; j < U[i].anime.length; j ++) {
					if (U[i].anime[j].rating > 0) {
						mean1 += (U[i].anime[j].rating);
						tWatchedCounter += 1.0;
					}
				}
				mean1 = mean1 / tWatchedCounter;
				
				//Stores mean in user
				U[i].mean = mean1;
			}
		}
		
		//PREDICT - predicts ratings of all users
		public static void predict(user[] U) throws FileNotFoundException {
			//Initializes file writer
			File outFile = new File("PredictedMatrix.txt");
			PrintWriter writer = new PrintWriter(outFile);
			
			
			//Stores position of neighbor's name
			int namePos = 0;
			
			//Initializes variables for aggregation
			double normalizer = 0.0;
			double weighter = 0.0;
			
		
			//Iterates through all users
			for(int i = 0; i < U.length; i++) {
				
				//Iterates through all animes
				for (int j = 0; j < U[i].anime.length; j++) {
					//Resets aggregator variables
					normalizer = 0.0;
					weighter = 0.0;
					
					//Checks only unseen animes
					if(U[i].anime[j].rating == 0) {
						//Iterates through all neighbors
						for (int k = 0; k < U[i].sim.length; k++) {
							namePos = U[i].sim[k].pos;
							
							//Adds to normalizer variable
							normalizer += Math.abs(U[i].sim[k].num);
							
														
							//Checks if neighbor has rated target user's unrated anime	 				
							if (U[namePos].anime[j].rating > 0) {
								weighter += U[i].sim[k].num * (U[namePos].anime[j].rating - U[namePos].mean);
							}
						}
						
						normalizer = 1 / normalizer;
						
						//Checks that at least one neighbor rated the item
						if (weighter != 0) {
							U[i].anime[j].rating = (weighter * normalizer) + U[i].mean;
						}
					}
					writer.println(U[i].anime[j].rating);
				}
				
				
				System.out.println("Prediction: " + (i + 1));
			}
			
			writer.close();
		}
		
		//TESTER - Tests similarities and predictions
		public static void tester(user[] U) throws FileNotFoundException {
			//Initializes file writer
			File outFile = new File("BLISK_test.txt");
			PrintWriter writer = new PrintWriter(outFile);
			
			//Initializes temporary variables
			anime temp = new anime();
			
			
			//Sorts all animes for BLISK
			for (int i = 0; i < U.length; i++) {
				if(U[i].name.equals("Blisk")) {
					System.out.println();
					System.out.println("BLISK:");
					for (int j = 0; j < U[i].sim.length; j++) {
						writer.println(U[i].sim[j].name + "----" + U[i].sim[j].num);
					}
									
					for (int j = 0; j < U[i].anime.length - 1; j++) {
						
						int maxIndex = j; // Index of smallest remaining value.
						
						for (int k = j + 1; k < U[i].anime.length; k++) {
								if (U[i].anime[maxIndex].rating < U[i].anime[k].rating) {
									maxIndex = k;  // Remember index of new minimum
								}
						}
						
						if (maxIndex != j) { 
								//Exchange current element with smallest remaining.
								temp = U[i].anime[j];
								U[i].anime[j] = U[i].anime[maxIndex];
								U[i].anime[maxIndex] = temp;
						}
					}
					
					writer.println();
					writer.println("Animes:");
					for(int j = 0; j < U[i].anime.length; j++) {
						if (U[i].anime[j].rating != 0.0) {
							writer.println(U[i].anime[j].name + " -- " + U[i].anime[j].rating);
						}
					}
				}
			}
			writer.close();
		}
	
	
	public static void main (String[] args) throws FileNotFoundException, IOException {
		//Creates an array of animes names by reading them from a textfile	
		String[] aList = reader("animes.txt");
		
		//Creates an array of personality scores by reading them from a textfile	
		String[] pList = reader("PersonalityMeasures.txt");
		
		//Creates an array of usernames names by reading them from a textfile	
		String[] uList = reader("FilteredUsers.txt");
			
		//Populates array of users with all anime names from array of anime names
		user[] users = namer(uList, aList, pList);
		
		//Extracts users' ratings from text file and populates utility matrix
		users = rater(users, "FilteredMatrix.txt", "PersonalityScores.txt");
			
		//Populates users' sims
		simser(users);
			
		//Calculates mean rating of all users
		meaner(users);
		
		//Predicts ratings from neighbors' animes
		predict(users);
					
		//Tests
		tester(users);
	}
}
