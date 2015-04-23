package com.afton.cometradar;

// The thread that updates locations every 5 seconds
public class UpdateLocationsThread extends Thread{

    MapsActivity ma;

    public UpdateLocationsThread(MapsActivity _ma){
        super();
        ma = _ma;
    }
    public void run() {
        while (true) {
            if ( ma.routeName != null) {
                new GetETA(ma).execute();
                try {
                    this.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
