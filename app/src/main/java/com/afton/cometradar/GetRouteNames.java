package com.afton.cometradar;

import android.os.AsyncTask;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Afton on 4/13/15.
 */
public class GetRouteNames extends AsyncTask<Void, Void, Void> {
    MapsActivity ma;
    JSONObject jsonO;
    JSONArray jsonA;
    String ip = "104.197.3.201";

    public GetRouteNames(MapsActivity mAct) {
        super();
        ma = mAct;
    }

    @Override
    protected Void doInBackground(Void... params) {


        String url = "http://"+ ip + ":3000/route-names";

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
            System.out.println("ROUTE NAMES: " + response.toString());

            String answer = response.toString();
            jsonA = new JSONArray(answer);
            System.out.println(jsonA.toString());
            jsonO = jsonA.getJSONObject(0);
            System.out.println(jsonO.toString());

            System.out.println("eta RESPONSE FROM SERVER: " + jsonO.getString("route_name"));

            //ArrayAdapter adapter = new ArrayAdapter<String>(this, ma.spinner, array);

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused){
        List<String> routeNames = new ArrayList();
        try {
            for (int i = 0; i < jsonA.length(); i++){
                System.out.println(jsonA.length() + " JSONA: " + jsonA.getJSONObject(i).getString("route_name"));
                routeNames.add(jsonA.getJSONObject(i).getString("route_name"));
               // ma.adapter.add(routeNames[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //ma.spinner.setAdapter(ma.adapter);
        //ma.spinner.setSelection(0);
        //ma.routeNames = routeNames;
        //ma.routeName = routeNames.get(0);
    }
}
