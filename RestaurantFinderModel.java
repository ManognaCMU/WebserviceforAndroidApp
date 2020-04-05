/**
 * Name: Sai Manogna Pentyala
 * Andrew: spentyal
 * Task: Project 4 Task 2
 *
 * thanks to  http://mongodb.github.io/mongo-java-driver/3.11/driver/getting-started/quick-start/
 * for mongo db connection and retrieval of data
 *  This class calls the zomato
 *  api and fetches the response as
 *  requested by the user.
 *  The response is sent in a JSON format
 *  It sends the list of restaurants
 *  and a list of images of a restaurant.
 *  It also connects to the mongo db
 *  and stores data in the database.
 *  Also retrives data from the mongo db
 *  and sends it to controller.
 */


package com.andrew.spentyal;

// imports to fetch restaurants, images of restaurants, store and retrieve data from mongo db

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * business class for the application
 */
public class RestaurantFinderModel {

    /**
     * method to retrieve city id based on city name and state name
     */
    public String getCityId(String cityName, String stateName, String originalCityName) {

        // connection object
        HttpURLConnection conn;
        // status of the connection request
        int status = 0;
        // captures the response
        String response = "";

        try {

            // call to the zomato API
            URL url = new URL("https://developers.zomato.com/api/v2.1/cities?q=" + cityName);
            // opens the connection
            conn = (HttpURLConnection) url.openConnection();
            // set request method to GET
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "application/json");
            // user key to retrieve data from Zomato api
            conn.setRequestProperty("user-key", "8870cc75aba8e95c55197f3f7a473814");

            // wait for response
            status = conn.getResponseCode();

            // If things went poorly
            if (status != 200) {
                // captures the error response
                String msg = conn.getResponseMessage();
            }

            // captures the response from api
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            // as long as there is data to read
            while ((output = br.readLine()) != null) {
                // captures the response
                response += output;
            }

            //captures the response in the JSON format
            JSONObject jo = new JSONObject(response);
            // retrieves the location suggestions
            JSONArray restaurantArray = jo.getJSONArray("location_suggestions");

            // for each restaurant
            for (int i = 0; i < restaurantArray.length(); i++) {
                JSONObject restaurantObj = restaurantArray.getJSONObject(i);
                // capture the city name
                String cityNameInput = restaurantObj.getString("name");
                // capture the state name
                String stateNameInput = restaurantObj.getString("state_name");

                // if city name and the state name mentioned by the user are same as that in the json response from api
                if (originalCityName != null && !originalCityName.isEmpty() && stateName != null && !stateName.isEmpty() &&
                cityNameInput != null && !cityNameInput.isEmpty() && cityNameInput.toLowerCase().contains(originalCityName.toLowerCase()) && stateNameInput != null
                        && !stateNameInput.isEmpty() && stateNameInput.toLowerCase().contains(stateName.toLowerCase())) {
                    // capture the city id
                    String cityId = String.valueOf(restaurantObj.get("id"));
                    // return the city id
                    return cityId;
                }
            }

            // close the connection
            conn.disconnect();

            // handles IO Exceptions
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
            // handles JSON Exceptions
        } catch (JSONException e) {
            e.printStackTrace();
            // handles Exceptions
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * retrieves the restaurant list from api
     */
    public List<String> getRestaurantList(String cityId) {

        // captures the list of restaurants
        List<String> restaurantList = new ArrayList<String>();
        // connection object
        HttpURLConnection conn;
        // status of the connection request
        int status = 0;
        // captures the response
        String response = "";

        try {

            // call to the zomato api
            URL url = new URL("https://developers.zomato.com/api/v2.1/search?entity_id=" + cityId + "&entity_type=city&count=5&sort=rating&order=desc");
            // opens the connection
            conn = (HttpURLConnection) url.openConnection();
            // set request method to GET
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "application/json");
            // sets the user key
            conn.setRequestProperty("user-key", "8870cc75aba8e95c55197f3f7a473814");

            // wait for response
            status = conn.getResponseCode();

            // If things went poorly
            if (status != 200) {
                // captures the error response
                String msg = conn.getResponseMessage();
            }

            // captures the output
            String outputNew = "";
            // things went well so let's read the response
            BufferedReader br1 = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            // as long as there is data to read
            while ((outputNew = br1.readLine()) != null) {
                // captures the response from api
                response += outputNew;
            }

            // creates the JSON object for the response
            JSONObject joNew = new JSONObject(response);
            // retrieves the restaurant array
            JSONArray restaurantArrayNew = joNew.getJSONArray("restaurants");

            // for each restaurant
            for (int j = 0; j < restaurantArrayNew.length(); j++) {
                JSONObject restaurantNewObj = restaurantArrayNew.getJSONObject(j);
                // capture the restaurant details as a json object
                restaurantNewObj = restaurantNewObj.getJSONObject("restaurant");
                // captures the restaurant name
                String restaurantName = restaurantNewObj.getString("name");
                // adds the name to the list
                restaurantList.add(restaurantName);
            }

            // returns the list of restaurants
            return restaurantList;

            // handles IO Exceptions
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
            // handles JSON Exceptions
        } catch (JSONException e) {
            e.printStackTrace();
            // handles exceptions
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // retrieves images of restaurants
    public List<String> getRestaurantPicList(String cityId, String originalRestaurantName) {

        // captures the list of images in a restaurant
        List<String> restaurantPicList = new ArrayList<String>();
        // connection object
        HttpURLConnection conn;
        // status of the connection request
        int status = 0;
        // captures the response
        String response = "";

        try {

            // hits the zomato api
            URL url = new URL("https://developers.zomato.com/api/v2.1/search?entity_id=" + cityId + "&entity_type=city&count=5&sort=rating&order=desc");
            // open the connection
            conn = (HttpURLConnection) url.openConnection();
            // set request method to GET
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "application/json");
            // sets the user key
            conn.setRequestProperty("user-key", "8870cc75aba8e95c55197f3f7a473814");

            // wait for response
            status = conn.getResponseCode();

            // If things went poorly
            if (status != 200) {
                // capture the error message
                String msg = conn.getResponseMessage();
            }

            // captures the response from api
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            // as long as there is data to read from json
            while ((output = br.readLine()) != null) {
                // captures the response
                response += output;
            }

            // creates the json object of the response
            JSONObject jo = new JSONObject(response);
            // retrieves the restaurant array
            JSONArray restaurantArray = jo.getJSONArray("restaurants");
            // for each restaurant
            for (int i = 0; i < restaurantArray.length(); i++) {
                JSONObject restaurantObj = restaurantArray.getJSONObject(i);
                // capture the restaurant details as a json object
                restaurantObj = restaurantObj.getJSONObject("restaurant");
                // captures the restaurant name
                String restaurantName = restaurantObj.getString("name");

                // if the restaurant name chosen by the user is same as the restaurant name in the json response
                if (restaurantName.equals(originalRestaurantName)) {

                    // retrieves the photos of the restaurant
                    JSONArray picArray = restaurantObj.getJSONArray("photos");
                    // capture upto 4 photos
                    for (int j = 0; j < 4; j++) {
                        JSONObject restPicObj = picArray.getJSONObject(j);
                        // capture the photo url as a json object
                        restPicObj = restPicObj.getJSONObject("photo");
                        // capture the photo url
                        String pictureURL = restPicObj.getString("url");
                        // adds the url to the list
                        restaurantPicList.add(pictureURL);
                    }
                    // returns the restaurant images list
                    return restaurantPicList;
                }
            }

            // handles IO Exceptions
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
            // handles JSON Exceptions
        } catch (JSONException e) {
            e.printStackTrace();
            // handles exceptions
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * stores input to the mongo db
     */
    public void saveInputToMongoRestaurantList(String cityName, String stateName, long requestTime, long responseTime, long latency, String userAgent, String requestToApi, String resListResponse) {

        try {

            // connect to the mongo db
            MongoClientURI uri = new MongoClientURI("mongodb+srv://manogna:manogna1234@cluster0-oqkjs.mongodb.net/test?retryWrites=true&w=majority");
            // connect to the mongo db client
            MongoClient mongoClient = new MongoClient(uri);
            // connect to the database
            MongoDatabase database = mongoClient.getDatabase("Project4Task2DB");
            // connect to the collection
            MongoCollection<Document> collection = database.getCollection("UserInputColl");

            // create a new document
            Document doc = new Document();
            // store the city name in document
            doc.put("cityName", cityName);
            // store the state name in document
            doc.put("stateName", stateName);
            // capture the request timestamp
            Timestamp requestTimestamp = new Timestamp(requestTime);
            // store the request timestamp in document
            doc.put("requestTime", requestTimestamp);
            // capture the response timestamp
            Timestamp responseTimestamp = new Timestamp(responseTime);
            // store the response timestamp in document
            doc.put("responseTime", responseTimestamp);
            // store the latency in document
            doc.put("latency", String.valueOf(latency));
            // store the user agent in document
            doc.put("userAgent", userAgent);
            // store the request to api in document
            doc.put("requestToApi", requestToApi);
            // store the response to android app
            doc.put("resListResponse", resListResponse);
            // insert the document in the collection
            collection.insertOne(doc);

            // handles exceptions
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * stores input in mongo db
     */
    public void saveInputToMongoRestaurantPhotoList(String cityName, String stateName, String restaurantName, long requestTime, long responseTime, long latency, String userAgent, String requestToApi, String resListResponse) {

        try {

            // connect to the mongo db
            MongoClientURI uri = new MongoClientURI("mongodb+srv://manogna:manogna1234@cluster0-oqkjs.mongodb.net/test?retryWrites=true&w=majority");
            // connect to the mongo db client
            MongoClient mongoClient = new MongoClient(uri);
            // connect to the database
            MongoDatabase database = mongoClient.getDatabase("Project4Task2DB");
            // connect to the collection
            MongoCollection<Document> collection = database.getCollection("UserInputColl");


            // create a new document
            Document doc = new Document();
            // store the city name in document
            doc.put("cityName", cityName);
            // store the state name in document
            doc.put("stateName", stateName);
            // store the restaurant name in document
            doc.put("restaurantName", restaurantName);
            // capture the request timestamp
            Timestamp requestTimestamp = new Timestamp(requestTime);
            // store the request timestamp in document
            doc.put("requestTime", requestTimestamp);
            // capture the response timestamp
            Timestamp responseTimestamp = new Timestamp(responseTime);
            // store the response timestamp in document
            doc.put("responseTime", responseTimestamp);
            // store the latency in document
            doc.put("latency", String.valueOf(latency));
            // store the user agent in document
            doc.put("userAgent", userAgent);
            // store the request to api in document
            doc.put("requestToApi", requestToApi);
            // store the response to android app
            doc.put("resListResponse", resListResponse);
            // insert the document in the collection
            collection.insertOne(doc);

            // handles exceptions
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * retrieve data from mongo db for restaurant list
     */
    public List<String> getDataFromMongoDbForRestaurantList() {

        // capture the data from mongo db in a list
        List<String> inputList = new ArrayList<>();

        // connect to mongo db
        MongoClientURI uri = new MongoClientURI("mongodb+srv://manogna:manogna1234@cluster0-oqkjs.mongodb.net/test?retryWrites=true&w=majority");
        // connect to mongo db client
        MongoClient mongoClient = new MongoClient(uri);
        // connect to the database
        MongoDatabase database = mongoClient.getDatabase("Project4Task2DB");
        // connect to the collection
        MongoCollection<Document> collection = database.getCollection("UserInputColl");

        // iterate through the documents present in the collection
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            // while there is a document to read next
            while (cursor.hasNext()) {
                // the next available document
                Document doc = cursor.next();
                // capture the restaurant name
                String restaurantName = (String) doc.get("restaurantName");
                // if restaurant name is not available
                if (restaurantName == null || restaurantName.isEmpty()) {
                    // capture the city name
                    String cityName = (String) doc.get("cityName");
                    // capture the state name
                    String stateName = (String) doc.get("stateName");
                    // capture the request time
                    Date time = (Date) doc.get("requestTime");
                    // specific date format
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    // capture the request time
                    String reqtimestamp = dateFormat.format(time);
                    // capture the response time
                    time = (Date) doc.get("responseTime");
                    // capture the response time
                    String restimestamp = dateFormat.format(time);
                    // captures latency
                    String latency = (String) doc.get("latency");
                    // captures user agent
                    String userAgent = (String) doc.get("userAgent");
                    // captures request to api
                    String requestToApi = (String) doc.get("requestToApi");
                    // captures the response
                    String resListResponse = (String) doc.get("resListResponse");
                    // input string
                    String input = cityName + "$" + stateName + "$" + reqtimestamp + "$" + restimestamp + "$ " + latency + "$" + userAgent + "$" + requestToApi + "$" + resListResponse;
                    // store the input strings in a list
                    inputList.add(input);
                }
            }
            // captures the exceptions
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close the cursor
            cursor.close();
        }
        // returns the list of input strings
        return inputList;
    }


    /**
     * retrieve data from mongo db for restaurant images list
     */
    public List<String> getDataFromMongoDbForRestaurantPhotoList() {

        // capture the data from mongo db in a list
        List<String> inputList = new ArrayList<>();

        // connect to mongo db
        MongoClientURI uri = new MongoClientURI("mongodb+srv://manogna:manogna1234@cluster0-oqkjs.mongodb.net/test?retryWrites=true&w=majority");
        // connect to mongo db client
        MongoClient mongoClient = new MongoClient(uri);
        // connect to the database
        MongoDatabase database = mongoClient.getDatabase("Project4Task2DB");
        // connect to the collection
        MongoCollection<Document> collection = database.getCollection("UserInputColl");

        // iterate through the documents present in the collection
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            // while there is a document to read next
            while (cursor.hasNext()) {
                // the next available document
                Document doc = cursor.next();
                // captures the restaurant name
                String restaurantName = (String) doc.get("restaurantName");
                // if the restaurant name is available
                if (restaurantName != null && !restaurantName.isEmpty()) {
                    // capture the city name
                    String cityName = (String) doc.get("cityName");
                    // capture the state name
                    String stateName = (String) doc.get("stateName");
                    // capture the request time
                    Date time = (Date) doc.get("requestTime");
                    // specific date format
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    // capture the request time
                    String reqtimestamp = dateFormat.format(time);
                    // capture the response time
                    time = (Date) doc.get("responseTime");
                    // capture the response time
                    String restimestamp = dateFormat.format(time);
                    // captures latency
                    String latency = (String) doc.get("latency");
                    // captures user agent
                    String userAgent = (String) doc.get("userAgent");
                    // captures request to api
                    String requestToApi = (String) doc.get("requestToApi");
                    // captures the response
                    String resListResponse = (String) doc.get("resListResponse");
                    // input string
                    String input = cityName + "$" + stateName + "$" + restaurantName + "$" + reqtimestamp + "$" + restimestamp + "$" + latency + "$" + userAgent + "$" + requestToApi + "$" + resListResponse;
                    // store the input strings in a list
                    inputList.add(input);
                }
            }
            // captures the exceptions
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close the cursor
            cursor.close();
        }
        // returns the list of input strings
        return inputList;
    }

    /**
     * retrieve data from mongo db to show top 5 user searches
     */
    public Map<String, Integer> getDataFromMongoDbForOAPart1() {

        // captures the input searches and their count
        Map<String, Integer> inputMap = new TreeMap<String, Integer>();

        // connect to the mongo db
        MongoClientURI uri = new MongoClientURI("mongodb+srv://manogna:manogna1234@cluster0-oqkjs.mongodb.net/test?retryWrites=true&w=majority");
        // connect to the mongo db client
        MongoClient mongoClient = new MongoClient(uri);
        // connect to the database
        MongoDatabase database = mongoClient.getDatabase("Project4Task2DB");
        // connect to the collection
        MongoCollection<Document> collection = database.getCollection("UserInputColl");

        // iterate through the documents present in the collection
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            // while there is a document to read next
            while (cursor.hasNext()) {
                // the next available document
                Document doc = cursor.next();
                // captures the restaurant name
                String restaurantName = (String) doc.get("restaurantName");
                // captures city name
                String cityName = (String) doc.get("cityName");
                // captures state name
                String stateName = (String) doc.get("stateName");
                // if the restaurant name is available
                if ((restaurantName == null || restaurantName.isEmpty()) && cityName != null && !cityName.isEmpty() && stateName != null
                    && !stateName.isEmpty()) {
                    // captures count of each user search
                    int count = 1;
                    cityName = cityName.toLowerCase();
                    stateName = stateName.toLowerCase();
                    // concatenation of cityname and statename
                    String input = cityName + "$" + stateName;
                    // if input map does not contain the input
                    if (inputMap.get(input) != null) {
                        // retrieve the count
                        count = inputMap.get(input);
                        // increment the count
                        inputMap.put(input, count + 1);
                    } else {
                        // captures the count as 1 for the first instance of search word
                        inputMap.put(input, 1);
                    }
                }
            }

            // captures the map in a list
            List<Map.Entry<String, Integer>> inputList = new LinkedList<Map.Entry<String, Integer>>(inputMap.entrySet());

            // sort the list in descending order
            Collections.sort(inputList, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    // returns the list in descending order of counts
                    return (o2.getValue() - o1.getValue());
                }
            });

            // captures the map in the descending order
            HashMap<String, Integer> actualInputMap = new LinkedHashMap<String, Integer>();
            // iterate through the map
            for (Map.Entry<String, Integer> hmap : inputList) {
                // insert data into the map
                actualInputMap.put(hmap.getKey(), hmap.getValue());
            }
            // return the map having the user searches and their counts
            return actualInputMap;

            // handles exceptions
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close the cursor
            cursor.close();
        }
        // return the map having the user searches and their counts
        return inputMap;
    }


    /**
     * retrieves data from mongo db to get the unique user agents
     */
    public Set<String> getDataFromMongoDbForOAPart3() {

        // captures the unique set of user agents
        Set<String> inputSet = new LinkedHashSet<String>();

        // connect to the mongo db
        MongoClientURI uri = new MongoClientURI("mongodb+srv://manogna:manogna1234@cluster0-oqkjs.mongodb.net/test?retryWrites=true&w=majority");
        // connect to the mongo db client
        MongoClient mongoClient = new MongoClient(uri);
        // connect to the database
        MongoDatabase database = mongoClient.getDatabase("Project4Task2DB");
        // connect to the collection
        MongoCollection<Document> collection = database.getCollection("UserInputColl");

        // iterate through the documents present in the collection
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            // while there is a document to read next
            while (cursor.hasNext()) {
                // the next available document
                Document doc = cursor.next();
                // captures the user agents
                String userAgent = (String) doc.get("userAgent");
                // add the user agents into a set
                inputSet.add(userAgent);
            }

            // captures exceptions
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close the cursor
            cursor.close();
        }

        // return the set of unique user agents
        return inputSet;
    }
}
