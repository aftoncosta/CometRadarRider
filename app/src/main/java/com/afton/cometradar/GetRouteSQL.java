package com.afton.cometradar;

import java.sql.*;


public class GetRouteSQL {
    String url = "jdbc:mysql://69.195.124.139:3306/";
    String dbName = "bsxpccom_cometradar";
    String driver = "com.mysql.jdbc.Driver";
    String userName = "bsxpccom_user2";
    String password = "5Q$gP7jfxeO4";

    int dataSize = getWaypointSize();
    Integer[] order =  new Integer[dataSize];
    String originLat;
    String originLong;
    String destLat;
    String destLong;
    String[] wpLat = new String[dataSize];
    String[] wpLong = new String[dataSize];

    public void getRouteData() {

System.out.println("EAR: " + MapsActivity.routeName);
        try {

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
