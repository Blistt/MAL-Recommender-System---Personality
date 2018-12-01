import java.io.*;
import java.util.*;

public class Evaluator {
	
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
				users[i].seen = new HashSet<String>();
										
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
		public static user[] rater(user[] U, String rList, String predList, String pList) throws IOException {
			File file = new File(predList);
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
			
			file = new File(pList);
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
			
			file = new File(rList);
			reader = new Scanner(file);
			i = 0;
				
			while (reader.hasNextLine()) {
				for (int j = 0; j < U[i].anime.length; j++) {
					if(Double.parseDouble(reader.nextLine()) != 0.0) {
						U[i].seen.add(U[i].anime[j].name);
					}
				}
				System.out.println("populating seen shows: " + (i + 1));
				i++;
			}	
			reader.close();
			
			
			return U;
		}
		
		
		//MEANER - calculates all users's mean rating
		public static void meaner(user[] U) {
			for (int i = 0; i < U.length; i++) {
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
		
		
		//RATINGS SIMILARITIES - Calculates rating similarities between target user and neighbors
		public static void similer(user[] U, int pos, double[] mae) throws FileNotFoundException{
			
			//Iterates through all of target user's animes
			for (int i = 0; i < U[pos].anime.length; i++) {
				
	
				double tempR = 0.0;
				
				//Checks for rated animes - test animes
				if(U[pos].seen.contains(U[pos].anime[i].name)) {
					
					//Sets test animes' scores to 0
					tempR = U[pos].anime[i].rating;
					U[pos].anime[i].rating = 0.0;
			
					U[pos].sim = new similarity[U.length - 1];
				
									
					int simIndex = 0;
					
					//Iterates through neighbors
					for (int j = 0; j < U.length; j++) {
						//Initializes variables for the pearson's formula
						double mean1 = 0.0;
						double mean2 = 0.0;
						double pearson1 = 0.0;
						double pearson2_1 = 0.0;
						double pearson2_2 = 0.0;
						double pearson2 = 0.0;	
						int simCounter = 0;
						
						//Keeps track of number of target and neighbor's watched shows 
						double nWatchedCounter = 0.0;
						
						//Calculates sum of rating scores provided by target and neighbor
						for (int k = 0; k < U[j].persona.length-1; k++) {
							if (U[pos].persona[k].rating > 0.0 && U[j].persona[k].rating > 0.0) {
								mean1 += (U[pos].persona[k].rating);
								mean2 += (U[j].persona[k].rating);
								nWatchedCounter += 1.0;
							}	
						}	
						
						//Calculates mean of watched shows
						mean1 = mean1 / nWatchedCounter;
						mean2 = mean2 / nWatchedCounter;
					
						//Checks that user is not being compared to itself
						if (j != pos) {
							// Pearson's r correlation formula 
							for (int l = 0; l < U[pos].persona.length-1; l++){
								if (U[pos].persona[l].rating > 0.0 && U[j].persona[l].rating > 0.0) {
									pearson1 += ((U[pos].persona[l].rating - mean1) * (U[j].persona[l].rating - mean2));
									pearson2_1 += Math.pow((U[pos].persona[l].rating - mean1), 2.0);
									pearson2_2 += Math.pow((U[j].persona[l].rating - mean2), 2.0);
									simCounter++;
								}
							}
							pearson2 = Math.sqrt(pearson2_1 * pearson2_2);
						
							U[pos].sim[simIndex] = new similarity(U[j].name);
							U[pos].sim[simIndex].pos = j;
							
							// Pearson's r correlation formula - result
							if (!Double.isNaN(pearson1 / pearson2)) {
								U[pos].sim[simIndex].num = pearson1 / pearson2;
							}
						
							//Penalizes sets of ratings in common that contain less than 5 shows
							U[pos].sim[simIndex].num *= (Math.min(simCounter, 5) / 5);
							
					
							simIndex++;
						}
					}
					
					//Sorts similarities for user
					similarity tempS = new similarity();
								
					for (int j = 0; j < U[pos].sim.length - 1; j++) {
						int maxIndex = j; // Index of smallest remaining value.
					
						for (int k = j + 1; k < U[pos].sim.length; k++) {
							if (U[pos].sim[maxIndex].num < U[pos].sim[k].num) {
								maxIndex = k;  // Remember index of new minimum
							}
						}
						
						if (maxIndex != j) { 
							//Exchange current element with smallest remaining.
							tempS = U[pos].sim[j];
							U[pos].sim[j] = U[pos].sim[maxIndex];
							U[pos].sim[maxIndex] = tempS;
						}
					}
							
					//Calculates predictions for target anime
					predict(U, pos, i, tempR, mae);
					
					//Restores target anime's original rating
					U[pos].anime[i].rating = tempR;
				}
			}
		}
		
		
		//PREDICT - predicts target user's rating of target anime
		public static void predict(user[] U, int Upos, int Apos, double rating, double[] mae) throws FileNotFoundException {
		
			
			double prediction = 0.0;
			
			//Stores position of neighbor's name
			int namePos = 0;
			
			//Initializes variables for aggregation
			double normalizer = 0.0;
			double weighter = 0.0;
								
			//Checks only unseen animes
			if(U[Upos].anime[Apos].rating == 0) {
				
				//Iterates through k nearest neighbors
				for (int k = 0; k < 100; k++) {
										
					namePos = U[Upos].sim[k].pos;
							
					//Adds to normalizer variable
					normalizer += Math.abs(U[Upos].sim[k].num);
					
																
					//Checks if neighbor has rated target user's unrated anime
					if (U[namePos].anime[Apos].rating > 0) {
						weighter += (U[Upos].sim[k].num * (U[namePos].anime[Apos].rating - U[namePos].mean));
					}
					
				}
				
				if (normalizer != 0) {
					normalizer = 1 / normalizer;
				}
			
				
				double size = U[Upos].seen.size();

					prediction = (weighter * normalizer) + 
								(((U[Upos].mean * size) - rating) / (size - 1.0));

				

					mae[0] += Math.abs(prediction - rating);
					mae[1] += 1.0;
			}
			
		}
				

		//EVALUATOR - evaluates a rating prediction for a set of users
		public static void evaluator(user[] users) throws FileNotFoundException {
			File outFile = new File("personalityMAE.txt");
			PrintWriter writer = new PrintWriter(outFile);
			
			double[] temp = new double[2];
			
			double[] mae = new double[2];
			
			for (int i = 0; i < users.length; i++) {
				temp[0] = mae[0];
				temp[1] = mae[1];
				similer(users, i, mae);
				System.out.println("Evaluating " +  (i + 1) +  ":  " + (mae[0] / mae[1]));
				writer.println(((mae[0] - temp[0]) / (mae[1] - temp[1])));
			}
			
			double MAE = mae[0] / mae[1];
			
			writer.println();
			writer.println("MAE: " +  MAE);
			writer.close();
			
			
		}
	
		
		
		public static void main (String[] args) throws FileNotFoundException, IOException {
			File file = new File("personalityData.txt");
			PrintWriter writer = new PrintWriter(file);
			
			//Creates an array of anime names by reading them from a textfile	
			String[] aList = reader("animes.txt");
			
			//Creates an array of personality scores by reading them from a textfile	
			String[] pList = reader("PersonalityMeasures.txt");
			
			//Creates an array of usernames names by reading them from a textfile	
			String[] uList = reader("FilteredUsers.txt");
				
			//Populates array of users with all anime names from array of anime names
			user[] users = namer(uList, aList, pList);
			
			//Extracts users' ratings from text file and populates utility matrix
			users = rater(users, "unpredidctedMatrix.txt", "predictedMatrix.txt", "PersonalityScores.txt");
				
			//Calculates means of all users
			meaner(users);
			
			//Evaluates each user
			evaluator(users);	
			
				
			writer.close();
		}
	
}
