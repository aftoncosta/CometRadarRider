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

    int dataSize = 0;
    Integer[] order;
    String originLat = "";
    String originLong = "";
    String destLat = "";
    String destLong = "";
    String[] wpLat;
    String[] wpLong;
    String ip = "104.197.3.201";

    public void getRouteData() {

//System.out.println("EAR: " + MapsActivity.routeName);

        String temp = "SELECT  W.order,R.originLat, R.originLong, R.destLat, " +
                        "R.destLong, W.wp_lat, W.wp_long FROM routes AS R JOIN " +
                        "route_waypoints AS W WHERE R.route_name = \"" +MapsActivity.routeName+"\" " +
                        "AND R.route_name=W.route_name ORDER BY W.order";

        String query = temp.replace(" ", "%20");

        String url = "http://" + ip + ":3000/doQuery?string=" + query ;

        try {
            URL obj = new URL(url);
           // System.out.println("url swag: " + obj.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
             //   System.out.println("CLOSE");
                con.setRequestProperty("Connection", "close");
            }
            con.setRequestMethod("GET");

           // System.out.println("\nSending 'GET' request to URL : " + url);

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
           // System.out.println("RESPONSE FROM SERVER: " + answer);
            //System.out.println("RESPONSE FROM SERVER: " + answer);
            parseData(answer);

        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void parseData(String data)throws Exception{

        JSONArray array = new JSONArray(data);

        int dataSize = array.length();
        Integer[] order =  new Integer[dataSize];
        String originLat;
        String originLong;
        String destLat;
        String destLong;
        String[] wpLat = new String[dataSize];
        String[] wpLong = new String[dataSize];

        List<String> order_json = new ArrayList<String>();
        List<String> originLat_json = new ArrayList<String>();
        List<String> originLong_json = new ArrayList<String>();
        List<String> destLat_json = new ArrayList<String>();
        List<String> destLong_json = new ArrayList<String>();
        List<String> wpLat_json = new ArrayList<String>();
        List<String> wpLong_json = new ArrayList<String>();



        for(int i = 0 ; i < array.length() ; i++){
            order_json.add(array.getJSONObject(i).getString("order"));
            originLat_json.add(array.getJSONObject(i).getString("originLat"));
            originLong_json.add(array.getJSONObject(i).getString("originLong"));
            destLat_json.add(array.getJSONObject(i).getString("destLat"));
            destLong_json.add(array.getJSONObject(i).getString("destLong"));
            wpLat_json.add(array.getJSONObject(i).getString("wp_lat"));
            wpLong_json.add(array.getJSONObject(i).getString("wp_long"));

            //order
            order[i] = Integer.parseInt(order_json.get(i));

            //wp_lat
            wpLat[i] = wpLat_json.get(i);

            //wp_long
            wpLong[i] = wpLong_json.get(i);
        }

        //originLat
        originLat = originLat_json.get(0);

        //originLong
        originLong = originLong_json.get(0);

        //destLat
        destLat = destLat_json.get(0);

        //destLong
        destLong = destLong_json.get(0);

        setOriginLat(originLat);
        setOriginLong(originLong);
        setDestLat(destLat);
        setDestLong(destLong);
        setWpLat(wpLat);
        setWpLong(wpLong);
        setSize(dataSize);

    }


    public void setOriginLat(String a){
        originLat = a;
    }

    public void setOriginLong(String a){
        originLong = a;
    }

    public void setDestLat(String a){
        destLat = a;
    }

    public void setDestLong(String a){
        destLong = a;
    }

    public void setWpLat(String[] a){
         wpLat = new String[a.length];
         for(int i = 0 ; i < a.length ; i ++) {
             wpLat[i] = a[i];
         }
    }

    public void setWpLong(String[] a){
        wpLong = new String[a.length];
        for(int i = 0 ; i < a.length ; i ++)
            wpLong[i] = a[i];
    }

    public void setSize(int a){
        dataSize = a;
    }


    //GET

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
