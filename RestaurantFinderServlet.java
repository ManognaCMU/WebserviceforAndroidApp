/**
 * Name: Sai Manogna Pentyala
 * Andrew: spentyal
 * Task: Project 4 Task 2
 *  Last Modified: April 4, 2020
 *
 *  This class calls the model
 *  class by sending the request
 *  given by the user, which in turn
 *  calls the api to send the response
 *  requested by the user.
 *  The response is sent in a JSON format.
 *  Also, it calls the model class
 *  to store data in the mongo db, which
 *  includes several inputs related to
 *  the request and response sent to zomato
 *  api. This logged data is again used to
 *  show as a operational analytics
 *  and logging dashboard
 */

package com.andrew.spentyal;

// imports to fetch restaurants, images of restaurants and a dashboard

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "RestaurantFinderServlet", urlPatterns = {"/getRestaurantList", "/getRestaurantPhotos", "/getDashboard"})
/** calls Zomato API to fetch restaurants, images of restaurants and dashboard*/
public class RestaurantFinderServlet extends javax.servlet.http.HttpServlet {

    // model to call Zomato API
    RestaurantFinderModel rfm = null;
    // captures the request time
    long startTime;

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        // captures the request time
        startTime = System.currentTimeMillis();
        // captures the business model of the app
        rfm = new RestaurantFinderModel();
    }

    /**
     * to fetch restaurants, images of restaurants, and the dashboard details
     */
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        // to fetch restaurant list
        if (request.getServletPath().equalsIgnoreCase("/getRestaurantList")) {

            try {

                // captures the mobile phone model
                String userAgent = request.getHeader("User-Agent");
                //captures the city Name
                String cityName = request.getParameter("cityName");
                // captures the state Name
                String stateName = request.getParameter("stateName");

                String originalCityName = cityName;

                // to replace the special character so that it can be parsed correctly in the url
                cityName = cityName.replaceAll("&", "%26");

                // to replace the special character so that it can be parsed correctly in the url
                cityName = cityName.replaceAll("\'", "%27");

                // to replace the special character so that it can be parsed correctly in the url
                cityName = cityName.replaceAll(" ", "%20");

                // to replace the special character so that it can be parsed correctly in the url
                cityName = cityName.replaceAll(",", "%2C");


                // fetches the cityID by calling the zomato API
                String cityId = rfm.getCityId(cityName, stateName, originalCityName);

                // create a Print writer object
                PrintWriter out = response.getWriter();
                // send the response in the form of json
                response.setContentType("application/json");
                // JSON object is created
                JSONObject obj = new JSONObject();
                // captures the restaurant list
                List<String> restaurantResultList = new ArrayList<String>();

                if(cityId != null) {
                    // fetches the list of restaurants based on city Id
                    restaurantResultList = rfm.getRestaurantList(cityId);
                    // restaurant List is sent as a json response
                    obj.put("restaurant_list", restaurantResultList);
                    // response is sent to Android app
                    out.write(String.valueOf(obj));
                } else {
                    // JSON object is created
                    obj = new JSONObject();
                    // empty list is sent as a json response
                    obj.put("restaurant_list", restaurantResultList);
                    // response is sent to Android app
                    out.write(String.valueOf(obj));
                }

                //captures the response time
                long endTime = System.currentTimeMillis();

                // api url to store in the mongo db
                String requestToApi_1 = "https://developers.zomato.com/api/v2.1/cities?q=" + cityName;
                // api url to store in the mongo db
                String requestToApi_2 = "https://developers.zomato.com/api/v2.1/search?entity_id=" + cityId + "&entity_type=city&count=5&sort=rating&order=desc";
                // saves several inputs and details as a document in mongo db
                rfm.saveInputToMongoRestaurantList(originalCityName, stateName, startTime, endTime, (endTime - startTime), userAgent, requestToApi_1 + ", " + requestToApi_2, String.valueOf(obj));

                // handles JSON Exception
            } catch (JSONException e) {
                e.printStackTrace();
                // handles Exception
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /** to fetch images of restaurants */
        if (request.getServletPath().equalsIgnoreCase("/getRestaurantPhotos")) {

            try {
                // captures the mobile phone model
                String userAgent = request.getHeader("User-Agent");
                // captures the city name
                String cityName = request.getParameter("cityName");
                // captures the state name
                String stateName = request.getParameter("stateName");
                // captures the restaurant name
                String restaurantName = request.getParameter("restaurantName");

                String originalCityName = cityName;

                // to replace the special character so that it can be parsed correctly in the url
                cityName = cityName.replaceAll("&", "%26");

                // to replace the special character so that it can be parsed correctly in the url
                cityName = cityName.replaceAll("\'", "%27");

                // to replace the special character so that it can be parsed correctly in the url
                cityName = cityName.replaceAll(" ", "%20");

                // to replace the special character so that it can be parsed correctly in the url
                cityName = cityName.replaceAll(",", "%2C");

                // fetches the cityID by calling the zomato API
                String cityId = rfm.getCityId(cityName, stateName, originalCityName);

                // create a Print writer object
                PrintWriter out = response.getWriter();
                // send the response in the form of json
                response.setContentType("application/json");
                // JSON object is created
                JSONObject obj = new JSONObject();
                //captures the images of restaurants
                List<String> restaurantPicList = new ArrayList<String>();

                if(cityId != null) {
                    // fetches the list of restaurants images based on city Id and restaurant name
                    restaurantPicList = rfm.getRestaurantPicList(cityId, restaurantName);
                    // restaurant pic list is sent as a json response
                    obj.put("restaurant_pic_list", restaurantPicList);
                    // response is sent to Android app
                    out.write(String.valueOf(obj));
                } else {
                    // JSON object is created
                    obj = new JSONObject();
                    // empty list is sent as a json response
                    obj.put("restaurant_pic_list", restaurantPicList);
                    // response is sent to Android app
                    out.write(String.valueOf(obj));
                }
                // captures the response time
                long endTime = System.currentTimeMillis();

                // api url to store in the mongo db
                String requestToApi_1 = "https://developers.zomato.com/api/v2.1/cities?q=" + cityName;
                // api url to store in the mongo db
                String requestToApi_2 = "https://developers.zomato.com/api/v2.1/search?entity_id=" + cityId + "&entity_type=city&count=5&sort=rating&order=desc";
                // stores several inputs in the mongo db as a document
                rfm.saveInputToMongoRestaurantPhotoList(originalCityName, stateName, restaurantName, startTime, endTime, (endTime - startTime), userAgent, requestToApi_1 + ", " + requestToApi_2, String.valueOf(obj));

                // handles exceptions
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // to design the dashboard of the application
        if (request.getServletPath().equalsIgnoreCase("/getDashboard")) {

            // retrieves data from mongo db for restaurant list
            List<String> actualInputList_Part1 = rfm.getDataFromMongoDbForRestaurantList();
            // retrieves data from mongo db for restaurant photo list
            List<String> actualInputList_Part2 = rfm.getDataFromMongoDbForRestaurantPhotoList();
            // retrieves data from mongo db for showing top 5 searches
            Map<String, Integer> operationalAnalytics_Part1 = rfm.getDataFromMongoDbForOAPart1();
            // captures the total number of searches
            int totalCountOfWords = 0;
            for (int count : operationalAnalytics_Part1.values()) {
                // captures the total number of searches
                totalCountOfWords = totalCountOfWords + count;
            }
            // captures the average latency of part 1 - retrieving restaurant list
            int averageLatency_Part1 = 0;
            // captures the total latency
            int sumLatency = 0;
            for (String inputStr : actualInputList_Part1) {
                // captures the latency for each request
                String latencyStr = inputStr.split("\\$")[4];
                // trim the latency
                latencyStr = latencyStr.trim();
                // convert string to integer
                int latency = Integer.valueOf(latencyStr);
                // computes the total latency
                sumLatency = sumLatency + latency;
            }
            // determines the average latency period
            averageLatency_Part1 = sumLatency / actualInputList_Part1.size();

            // captures the average latency of part 2 - retrieving restaurant images list
            int averageLatency_Part2 = 0;
            // captures the total latency
            sumLatency = 0;
            for (String inputStr : actualInputList_Part2) {
                // captures the latency for each request
                String latencyStr = inputStr.split("\\$")[5];
                // trim the latency
                latencyStr = latencyStr.trim();
                // convert string to integer
                int latency = Integer.valueOf(latencyStr);
                // computes the total latency
                sumLatency = sumLatency + latency;
            }
            // determines the average latency period
            averageLatency_Part2 = sumLatency / actualInputList_Part2.size();

            // retrieves data from Mongo db to populate the unique user agents
            Set<String> actualInputList_Part3 = rfm.getDataFromMongoDbForOAPart3();

            // sets the restaurant list
            request.setAttribute("actualInputList_Part1", actualInputList_Part1);
            // sets the restaurant images list
            request.setAttribute("actualInputList_Part2", actualInputList_Part2);
            // sets the list of top 5 user searches
            request.setAttribute("operationalAnalytics_Part1", operationalAnalytics_Part1);
            // sets the total number of searches
            request.setAttribute("userSearchCount", totalCountOfWords);
            // sets the average latency period while retrieving restaurant list
            request.setAttribute("averageLatency_Part1", averageLatency_Part1);
            // sets the average latency period while retrieving restaurant images list
            request.setAttribute("averageLatency_Part2", averageLatency_Part2);
            // sets the unique user agents list
            request.setAttribute("actualInputList_Part3", actualInputList_Part3);
            // forwards the request to homepage.jsp
            RequestDispatcher dispatcher = request.getRequestDispatcher("homepage.jsp");
            // forwards the request to homepage.jsp
            dispatcher.forward(request, response);

        }
    }
}
