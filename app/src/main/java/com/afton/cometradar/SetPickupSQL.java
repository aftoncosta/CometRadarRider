package com.afton.cometradar;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.net.*;
import java.sql.*;
import de.roderick.weberknecht.*;

public class SetPickupSQL extends AsyncTask<Void, Void, String>  {
        String url = "jdbc:mysql://69.195.124.139:3306/";
        String dbName = "bsxpccom_cometradar";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "bsxpccom_teamX";
        String password = "C$1RFKqdCr&w";

        double lat;
        double lon;

    public SetPickupSQL(double a, double b) {
        lat = a;
        lon = b;
    }


    /*
        Background task that runs to connect to database and get route data
     */
    protected String doInBackground(Void... arg0) {
       /* try {

            System.out.println("CONNECTING TO DATABASE TO GET ROUTE DATA");
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(url+dbName,userName,password);

            PreparedStatement statement = (PreparedStatement) conn.prepareStatement("INSERT INTO " +
                    "pickup_request(`route_name`,`lat`,`long`) " +
                    "VALUES (\"" + MapsActivity.routeName + "\"," +
                    "\""+lat+"\",\""+lon+"\")");

            System.out.println(statement);
            statement.execute();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        */




        /*try {
            URI url = new URI("ws://10.0.2.2:3001");
            WebSocket websocket = new WebSocket(url);

            String temp = "INSERT INTO " +
                    "pickup_request(`route_name`,`lat`,`long`) " +
                    "VALUES (\"" + MapsActivity.routeName + "\"," +
                    "\""+lat+"\",\""+lon+"\")";
            // Register Event Handlers
            websocket.setEventHandler(new WebSocketEventHandler() {
                public void onOpen()
                {
                    System.out.println("--open");
                }

                public void onMessage(WebSocketMessage message)
                {
                    System.out.println("--received message: " + message.getText());
                }

                public void onClose()
                {
                    System.out.println("--close");
                }

                public void onPing() {}
                public void onPong() {}
            });

            // Establish WebSocket Connections
            websocket.connect();

            // Send UTF-8 Text
            websocket.send(temp);

            // Close WebSocket Connection
            websocket.close();
        }
        catch (WebSocketException wse) {
            wse.printStackTrace();
        }
        catch (URISyntaxException use) {
            use.printStackTrace();
        }*/

        String temp = "INSERT INTO " +
                "pickup_request('route_name','lat','long') " +
                "VALUES (\"" + MapsActivity.routeName + "\"," +
                "\""+lat+"\",\""+lon+"\")";

        String url = "http://10.0.2.2:3001/sendPickup?string=" + temp;
        try {
            URL obj = new URL(url);
            System.out.println("url swag: " + obj.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            //con.setRequestMethod("GET");

            //int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            //System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());

        }catch(Exception e){
            System.out.println( "error: " );
            e.printStackTrace();
        }
        return "";
    }
}


