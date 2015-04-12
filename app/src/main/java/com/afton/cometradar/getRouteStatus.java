package com.afton.cometradar;


import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class getRouteStatus extends AsyncTask<Void, Void, String> {

    String route;
    String shuttle;
    int capacity;
    boolean duty;

    protected String doInBackground(Void... arg0) {

        String url = "http://10.0.2.2:3000/route-data";

        try {
            System.out.println("Get rouets 1");
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
            System.out.println("RESPONSE FROM SERVER GETROUTES: " + answer);

            parseData(answer);

        }catch(Exception e){
            e.printStackTrace();
        }

        return "";
    }

    public void parseData(String answer) throws Exception{
        JSONArray array = new JSONArray(answer);

        String routeName;
        String shuttleNo;
        int currentCapacity;
        int onDuty;

        List<String> routeName_json = new ArrayList<String>();
        List<String> shuttleNo_json = new ArrayList<String>();
        List<String> cc_json = new ArrayList<String>();
        List<String> onDuty_json = new ArrayList<String>();

        for(int i = 0 ; i < array.length() ; i++) {
            routeName_json.add(array.getJSONObject(i).getString("route_name"));
            shuttleNo_json.add(array.getJSONObject(i).getString("shuttle"));
            cc_json.add(array.getJSONObject(i).getString("students_on_shuttle"));
            onDuty_json.add(array.getJSONObject(i).getString("onduty"));

        }

        routeName = routeName_json.get(0);
        shuttleNo = shuttleNo_json.get(0);
        currentCapacity = Integer.parseInt(cc_json.get(0));
        onDuty = Integer.parseInt(onDuty_json.get(0));

        System.out.println("Printing biatches");
        System.out.println(routeName +"   " + shuttleNo +"   " +  currentCapacity +"   " +  onDuty);

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

    public void setRoute(int onDuty) {
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
}
