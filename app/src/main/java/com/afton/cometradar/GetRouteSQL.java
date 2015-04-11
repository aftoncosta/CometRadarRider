package com.afton.cometradar;

import android.os.Build;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.sql.*;

import org.json.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.lang.String;
import java.util.*;


public class GetRouteSQL {
    String url = "jdbc:mysql://69.195.124.139:3306/";
    String dbName = "bsxpccom_cometradar";
    String driver = "com.mysql.jdbc.Driver";
    String userName = "bsxpccom_user2";
    String password = "5Q$gP7jfxeO4";

    int dataSize = 4;
   // int dataSize = getWaypointSize();
    Integer[] order =  new Integer[dataSize];
    String originLat;
    String originLong;
    String destLat;
    String destLong;
    String[] wpLat = new String[dataSize];
    String[] wpLong = new String[dataSize];

    public void getRouteData() {

System.out.println("EAR: " + MapsActivity.routeName);

        String temp = "SELECT  W.order,R.originLat, R.originLong, R.destLat, " +
                        "R.destLong, W.wp_lat, W.wp_long FROM routes AS R JOIN " +
                        "route_waypoints AS W WHERE R.route_name = \"" +MapsActivity.routeName+"\" " +
                        "AND R.route_name=W.route_name ORDER BY W.order";

        String query = temp.replace(" ", "%20");

        String url = "http://10.0.2.2:3001/sendPickup?string=" + query ;

        try {
            URL obj = new URL(url);
            System.out.println("url swag: " + obj.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                System.out.println("CLOSE");
                con.setRequestProperty("Connection", "close");
            }
            con.setRequestMethod("GET");

            System.out.println("\nSending 'GET' request to URL : " + url);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            //print result
            String answer = response.toString();
            System.out.println("RESPONSE FROM SERVER: " + answer);
            System.out.println("RUNNNNNNNNNNNNNNNING-1");

            parseData(answer);

            System.out.println("RUNNNNNNNNNNNNNNNING-2");

        }catch(Exception e){
            e.printStackTrace();
        }

        /*try {

            System.out.println("CONNECTING TO DATABASE TO GET ROUTE DATA");
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(url+dbName,userName,password);

            //String query = "SELECT SUM(paypal_fee) FROM shopifyorders JOIN paypalfees ON paypal_id=payment_id WHERE sale_date BETWEEN \"" +date1+"\" AND \""+date2+"\"";
            String query = "SELECT  W.order,R.originLat, R.originLong, R.destLat, R.destLong, W.wp_lat, W.wp_long FROM routes AS R JOIN route_waypoints AS W WHERE R.route_name = \"" +MapsActivity.routeName+"\" AND R.route_name=W.route_name ORDER BY W.order";

            System.out.println("QUERY= " + query);
            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            int i = 0;
            // iterate through the java resultset
            while (rs.next())
            {
                order[i] = rs.getInt("order");
                originLat = rs.getString("originLat");
                originLong = rs.getString("originLong");
                destLat = rs.getString("destLat");
                destLong = rs.getString("destLong");
                wpLat[i] = rs.getString("wp_lat");
                wpLong[i] = rs.getString("wp_long");

                i++;

            }

            System.out.println("Closing Connection for route data");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    public void parseData(String data)throws Exception{
//        JSONArray parse =  new JSONArray(data);
//        System.out.println("parse = " + parse.get(0));
//
//System.out.println("size is = "+ parse.length());

        JSONObject obj = new JSONObject(data);

        List<String> list = new ArrayList<String>();
        JSONArray array = obj.getJSONArray("order");
        for(int i = 0 ; i < array.length() ; i++){
            list.add(array.getJSONObject(i).getString("order"));
            System.out.println("Size = " + array.length());
        }

    }
    public int getWaypointSize() {

        int size = 0;
        try {

            System.out.println("CONNECTING TO DATABASE TO GET SIZE");
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(url+dbName,userName,password);

            String query = "SELECT  W.order,R.originLat, R.originLong, R.destLat, R.destLong, W.wp_lat, W.wp_long FROM routes AS R JOIN route_waypoints AS W WHERE R.route_name = \"" +MapsActivity.routeName+"\" AND R.route_name=W.route_name ORDER BY W.order";

            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // iterate through the java resultset
            while (rs.next())
            {
                size++;
            }

            System.out.println("Closing Connection for size");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return size;
    }

	/*
	 * 	Integer[] order =  new Integer[dataSize];
	String originLat;
	String originLong;
	String destLat;
	String destLong;
	String[] wpLat = new String[dataSize];
	String[] wpLong = new String[dataSize];
	 *
	 */

    public Integer[] getOrder(){
        return order;
    }

    public String getOriginLat(){
        return originLat;
    }

    public String getOriginLong(){
        return originLong;
    }

    public String getDestLat(){
        return destLat;
    }

    public String getDestLong(){
        return destLong;
    }

    public String[] getWpLat(){
        return wpLat;
    }

    public String[] getWpLong(){
        return wpLong;
    }

    public int getSize(){
        return dataSize;
    }
}
