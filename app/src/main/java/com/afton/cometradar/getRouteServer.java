package com.afton.cometradar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.iharder.Base64.InputStream;

public class getRouteServer {

    String route;
    String shuttle;
    int capacity;
    boolean duty;
    String driverName;
    Drawable driverPic;
    public static String driverURL;
    String ip = "104.197.3.201";


    public void getRouteData(){

        String url = "http://" + ip + ":3000/route-data";

        try {
            URL obj = new URL(url);
            //System.out.println("url swag: " + obj.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                //System.out.println("CLOSE");
                con.setRequestProperty("Connection", "close");
            }
            con.setRequestMethod("GET");

            //System.out.println("\nSending 'GET' request to URL : " + url);

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
            //System.out.println("RESPONSE FROM SERVER GETROUTES: " + answer);

            parseData(answer);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void parseData(String answer) throws Exception{
        JSONArray array = new JSONArray(answer);

        String routeName = "";
        String shuttleNo = "";
        int currentCapacity = 0;
        int onDuty = 0;
        String driverFName = "";
        String driverLName = "";

        List<String> routeName_json = new ArrayList<String>();
        List<String> shuttleNo_json = new ArrayList<String>();
        List<String> cc_json = new ArrayList<String>();
        List<String> onDuty_json = new ArrayList<String>();
        List<String> driverFName_json = new ArrayList<String>();
        List<String> driverLName_json = new ArrayList<String>();
        List<String> driverURL_json = new ArrayList<String>();

        for(int i = 0 ; i < array.length() ; i++) {
            routeName_json.add(array.getJSONObject(i).getString("route_name"));
            shuttleNo_json.add(array.getJSONObject(i).getString("shuttle"));
            cc_json.add(array.getJSONObject(i).getString("students_on_shuttle"));
            onDuty_json.add(array.getJSONObject(i).getString("onduty"));
            driverFName_json.add(array.getJSONObject(i).getString("fname"));
            driverLName_json.add(array.getJSONObject(i).getString("lname"));
            driverURL_json.add(array.getJSONObject(i).getString("picture"));

            //If the route selected is in database, save the info cause we sending it
            if(routeName_json.get(i).equals(MapsActivity.routeName)){
                routeName = routeName_json.get(i);
                shuttleNo = shuttleNo_json.get(i);
                currentCapacity = Integer.parseInt(cc_json.get(i));
                onDuty = Integer.parseInt(onDuty_json.get(i));
                driverFName = driverFName_json.get(i);
                driverLName = driverLName_json.get(i);
                driverURL = "http://" + ip + ":3000/uploads/" + driverURL_json.get(i);
                //System.out.println("URL: " + driverURL);
                MapsActivity.driverURL = driverURL;
            }
        }

       // System.out.println("Printing biatches");
        //System.out.println(routeName +"   " + shuttleNo +"   " +  currentCapacity +"   " +  onDuty);

        setRoute(routeName);
        setShuttle(shuttleNo);
        setCap(currentCapacity);
        setDuty(onDuty);
        setDriverName(driverFName + " " + driverLName);
        setDriverPic(driverURL);
    }

    //SETTERS
    public void setRoute(String routeName){
        route = routeName;
    }

    public void setShuttle(String shuttleCap){
        shuttle = shuttleCap;
    }

    public void setCap(int cap){
        capacity = cap;
    }


    public void setDriverName(String name){
        driverName = name;
    }

    public void setDriverPic(String pic){
        try {
            Bitmap x;

            System.out.println("DRIVER URL: " + driverURL);
            HttpURLConnection connection = (HttpURLConnection) new URL(driverURL).openConnection();
            connection.connect();
            java.io.InputStream input = connection.getInputStream();

            x = BitmapFactory.decodeStream(input);
            MapsActivity.driverPic =  new BitmapDrawable(x);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR");
            return;
        }
    }

    public void setDuty(int onDuty) {
        //System.out.println("Duty is " + onDuty);
        if (onDuty == 0) //0 = false
            duty = false;
        else
            duty = true;
    }


    //GETTERS
    public String getRoute(){
        return route;
    }

    public String getShuttle(){
        return shuttle;
    }

    public int getCap(){
        return capacity;
    }

    public boolean getDuty(){
        return duty;
    }

    public String getDriverName(){
        return driverName;
    }

    public Drawable getDriverPic(){
        return driverPic;
    }
}
