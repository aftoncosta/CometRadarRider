package com.afton.cometradar;

import android.os.Build;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class getShuttleMax {

    String shuttle;
    int shuttleMax;
    String ip = "104.197.3.201";

    public getShuttleMax(String a){
        shuttle = a;
    }

    public void getShuttle() {

        String temp = "SELECT max FROM shuttle WHERE shuttle=" + shuttle;
        String query = temp.replace(" ", "%20");

        String url = "http://" + ip + ":3000/doQuery?string=" + query ;

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
            //System.out.println("RESPONSE FROM SERVER MAXSHUTTLE: " + answer);

            parse(answer);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void parse(String answer) throws Exception {
        JSONArray array = new JSONArray(answer);
        int size;
        List<String> size_json = new ArrayList<String>();
        size_json.add(array.getJSONObject(0).getString("max"));
        size = Integer.parseInt(size_json.get(0));

        //System.out.println("Size is " + size);
        setShuttleSize(size);
    }

    public void setShuttleSize(int size){
        shuttleMax = size;
    }

    public int getShuttleMax(){
        return shuttleMax;
    }
}
