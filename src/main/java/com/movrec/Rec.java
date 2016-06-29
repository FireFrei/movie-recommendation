package com.movrec;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;


public class Rec {

	public static HashMap<Integer,List<String>> movies;
	public static HashMap<Integer,List<String>> links;
	public static JavaRDD<Rating> ratings;
	public static JavaSparkContext sc;
	
	public static void main(String[] args) {
		
		// SETUP
		// Simon
		//String basepath = "/home/bar/uni/master/s2/Big_Data_Praktikum/";
		// Matze
		String basepath = "/Users/mat/Workspaces/Eclipse/MovRecTwo/WebContent/html/";
			
	    SparkConf conf = new SparkConf().setAppName("Movie Recommendation");
	    conf.setMaster("local[2]");
	    sc = new JavaSparkContext(conf);

	    // Load and parse the data
	    movies = parseCSVFile(basepath + "ml-latest-small/movies1.csv");
	    links = parseCSVFile(basepath + "ml-latest-small/links1.csv");
	    ratings = parseRatings(basepath+"ml-latest-small/ratings1.csv", sc);
	    
	    System.out.println("Movies:"+movies.size()+" Links:"+links.size()+" Ratings:"+ratings.count());
	   
/*	    Map<Integer,Double> userR = new HashMap<Integer,Double>();
	    userR.put(1, 4.0);
	    userR.put(2, 3.5);
	    Map<Integer,List<String>> recMov = new HashMap<Integer,List<String>>();
	    recMov = addUserRatings(userR);*/
	    
	    
	    // Choose n random movies to rate.
	    //HashMap<Integer,List<String>> test = getRatingMovies(10);
	    
	    //Print the random movies.
/*	    Set<Integer> keySet = test.keySet();
	    Iterator<Integer> it = keySet.iterator();
	    while(it.hasNext()){
	    	int key = it.next();
	    	System.out.println(key);
	    	System.out.println(test.get(key));
	    }*/
	    	     
	    System.out.println("Done");
	    sc.close();
	  }
	
	public Rec() {
		// SETUP
		// Simon
		//String basepath = "/home/bar/uni/master/s2/Big_Data_Praktikum/";
		// Matze
		//String basepath = "/Users/mat/Workspaces/Eclipse/MovRecTwo/WebContent/html/";
			
	    SparkConf conf = new SparkConf().setAppName("Movie Recommendation");
	    conf.setMaster("local[2]");
	    sc = new JavaSparkContext(conf);

	    // Load and parse the data
	    movies = parseCSVFile(getClass().getClassLoader().getResource("ml-latest-small/movies.csv").getFile());
	    links = parseCSVFile(getClass().getClassLoader().getResource("ml-latest-small/links.csv").getFile());
	    ratings = parseRatings(getClass().getClassLoader().getResource("ml-latest-small/ratings.csv").getFile(), sc);
	    
	    System.out.println("Movies:"+movies.size()+" Links:"+links.size()+" Ratings:"+ratings.count());
	    System.out.println("Done");
	    sc.close();
	}
	  
		// Parse the given CSV file and return a JavaRDD containing the ratings.
		public static JavaRDD<Rating> parseRatings(String dataPath, JavaSparkContext sc) {
			JavaRDD<String> input = sc.textFile(dataPath);
			JavaRDD<Rating> rating = input.map(
					new Function<String, Rating>() {
						public Rating call(String s) {
							String[] sarray = s.split(",");
							return new Rating(Integer.parseInt(sarray[0]), Integer.parseInt(sarray[1]), 
									Double.parseDouble(sarray[2]));
						}
					}
					);
			return rating;
		}
		
		// Parse the given CSV file and returns a hashmap of the content.
		public static HashMap<Integer,List<String>> parseCSVFile(String pathToCSV) {
			HashMap<Integer,List<String>> movieInformation = new HashMap<Integer,List<String>>();
			
			BufferedReader br = null;
			String line = "";
			
			try {
				br = new BufferedReader(new FileReader(pathToCSV));
				while ((line = br.readLine()) != null) {
					String[] movie = line.split(",");
					int movieId = Integer.parseInt(movie[0]);
					
					List<String> temp = Arrays.asList(movie);
					List<String> information = new ArrayList(temp);
					information.remove(0);
					movieInformation.put(movieId, information);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return movieInformation;
		}

		// Returns n random movies + movie information.
		public HashMap<Integer,List<String>> getRatingMovies(int n) {
			HashMap<Integer,List<String>> ratingMovies = new HashMap<Integer,List<String>>();
			Random generator = new Random();
			Set<Integer> movIds = movies.keySet();
			Iterator<Integer> it = movIds.iterator();
			    for (int idx = 1; idx <= n; ++idx){
			    	int key = it.next();
			    	List<String> randMovie = (List<String>) movies.get(key);
			    	List<String> imdId = links.get(key);
			    	randMovie.add(imdId.get(0));
			    	ratingMovies.put(key, randMovie);
			    }
			return ratingMovies;
		}

		// Add User ratings to the data set. key:movieId, value:rating
		// Returns recommended movies. key:movieId, value:movietitle,genre,rating,imdId
		public Map<Integer,List<String>> addUserRatings(Map<Integer,Double> userRatings){
			Iterator<Map.Entry<Integer,Double>> it = userRatings.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer,Double> pair = (Map.Entry<Integer,Double>)it.next();
				Rating rating = new Rating(0,(Integer)pair.getKey(),(Double)pair.getValue());
				List<Rating> ratingList = Arrays.asList(rating);
				JavaRDD<Rating> userRating = sc.parallelize(ratingList);
				ratings = ratings.union(userRating);
				
/*				Iterator<Rating> itR = ratings.toLocalIterator();
				while (itR.hasNext()) {
					System.out.println(itR.next());
				}*/
				
			}
			Rating[] recommendedMovies = recommendMovies(2);
			Map<Integer,List<String>> products = new HashMap<Integer, List<String>>();
			
			for(int i=0; i<recommendedMovies.length; ++i) {
				String ranking = recommendedMovies[i].toString();
				String delims = "[,]";
				String[] tokens = ranking.split(delims);
				int key = Integer.parseInt(tokens[1]);
				List<String> movinf = movies.get(key);
				movinf.add(tokens[2]);
				List<String> imdId = links.get(key);
				movinf.add(imdId.get(0));
				products.put(key, movinf);
			}
			return products;
		}

		// Recommend n movies.
		public Rating[] recommendMovies(int n) {
			
			// Build the recommendation model using ALS
		    int rank = 10;
		    int numIterations = 10;
		    MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(ratings), rank, numIterations, 0.01);
		    
		    
			// Recommend n products to user 0
		    Rating[] userProducts = model.recommendProducts(0,n);
		    return userProducts;
		}
}