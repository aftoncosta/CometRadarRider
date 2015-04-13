package com.afton.cometradar;

// The thread that updates locations every 5 seconds
public class UpdateLocationsThread extends Thread{

    MapsActivity ma;

    public UpdateLocationsThread(MapsActivity _ma){
        super();
        ma = _ma;
    }
    public void run() {
        /* Uncomment this section when ready to test cart tracking
            DO NOT uncomment until then. I accidentally left the emulator open with this running
            and quickly used up my Google Directions API daily quota (2,500 calls)... so yeah.
*/

        /*while(true){
            new GetETA(ma).execute();
            try {
                this.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
}
