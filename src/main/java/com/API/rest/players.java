package com.API.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.opencsv.CSVReader;

@Path("/")
public class players {
	
	//CSV file url
	String url = "https://s3.eu-central-1.amazonaws.com/playerscsv/PBS_players.csv";
	
	//+++++++++++++++++++++Returns data from the CSV file.+++++++++++++++++++++++++++
	public CSVReader getCSV(String url) throws IOException {
		CSVReader reader = null;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		try {
			int responseCode = con.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			reader = new CSVReader(in);
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
		return reader;
		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	
	
	//+++++++++++++++++++++Creates the endpoint api/players.+++++++++++++++++++++++++++
	@Path("/players")
	public String getPlayers() throws JSONException, IOException {
		JSONArray jArray = new JSONArray();
		JSONArray jArrayProperties = new JSONArray();
		int numberOfPlayers = 0;
		String result = null;
		CSVReader csvReader = getCSV(url);
		
			try {
				// Reading Records One by One in a String array
		        String[] nextRecord;
		        nextRecord = csvReader.readNext();
		        String[] properties = {"ranking","tournamentRegionCode","regionCode","tournamentName","teamName","rating","goal","yellowCard","redCard","shotsPerGame","name"};
		        int[] dataPositions = {0,5,6,7,15,24,25,27,28,29,32};
		        while ((nextRecord = csvReader.readNext()) != null) {
		        	numberOfPlayers++;
		        	JSONObject jsonArray = new JSONObject();
		        	
		        	for (int i = 0; i < properties.length; i++) {
						jsonArray.put(properties[i], nextRecord[dataPositions[i]]);
		             	
					}
		            jArray.put(jsonArray);
		        }
		        
		        JSONObject jsonArrayProperties = new JSONObject();
		        for (int i = 0; i < properties.length; i++) {
					jsonArrayProperties.put("p"+i, properties[i]);
		         	
				}
		        jArrayProperties.put(jsonArrayProperties);
					
					JSONObject json = new JSONObject();
					 json.put("numberOfPlayers", numberOfPlayers);
					 json.put("numberOfProperties", properties.length);
					 json.put("results", jArray);
					 json.put("properties", jArrayProperties);
					 
					 Gson gson = new Gson();
					 result = gson.toJson(json);
				      System.out.println(result);
				      csvReader.close();
			} catch (Exception e) {
				System.out.println(e);
				result = "Error reading the CSV file";
			}
			
		return result;
	}
	
	//+++++++++++++++++++++Creates the endpoint api/players/{id}.+++++++++++++++++++++++++++
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/players/{id}")
	public String getPlayerDetail(@PathParam("id")int id) throws JSONException, IOException {
		JSONArray jArray = new JSONArray();
		JSONArray jArrayProperties = new JSONArray();
		int numberOfProperties = 0;
		
		String result = null;
		CSVReader csvReader = getCSV(url);
		
		try {
			String[] nextRecord;
	        nextRecord = csvReader.readNext();
	        String[] properties = new String [nextRecord.length];
	        for (int i = 0; i < nextRecord.length; i++) {
	        	properties[i] = nextRecord[i];
	        	numberOfProperties++;
	        }
	        while ((nextRecord = csvReader.readNext()) != null ) {
	        	
	        	int rankingCSV = Integer.parseInt(nextRecord[0]);
	        	if(rankingCSV == id) {
	        		JSONObject jsonArray = new JSONObject();
	        		JSONObject jsonArrayProperties = new JSONObject();
	        		
	                for (int i = 0; i < properties.length; i++) {
	                 	jsonArray.put(properties[i], nextRecord[i]);
	                 	jsonArrayProperties.put("p"+i, properties[i]);
	                 	
					}

	                jArray.put(jsonArray);
	                jArrayProperties.put(jsonArrayProperties);
	        	}
	        	
	        }
			    
			 JSONObject json = new JSONObject();
			 json.put("properties", jArrayProperties);
			 json.put("numberOfProperties", numberOfProperties);
			 json.put("results", jArray);
			 		
			 Gson gson = new Gson();
			 result = gson.toJson(json);
		      System.out.println(result);
		      csvReader.close();
		} catch (Exception e) {
			System.out.println(e);
			result = "Error reading the CSV file";
		}
	      
		return result;
	}
	
	
	
}

