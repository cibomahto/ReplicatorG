package replicatorg.app.thingspeak;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import replicatorg.app.Base;

public class Thingspeak implements Runnable {

	long intervalS = 15;
	String thingSpeakKey = "XRHPO1AK44GLLXEZ";
	
	public void sendTemperatureData(double toolTemp, double bedTemp) {
		String postURL = "http://api.thingspeak.com/update";
		Base.logger.info("Sending temperature data!");
		
		String message = "key=" + thingSpeakKey
    	+ "&field1=" + Double.toString(toolTemp)
    	+ "&field2=" + Double.toString(bedTemp);
		
        try {
            URL url = new URL(postURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(message);
            writer.close();
    
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
            } else {
                // Server returned HTTP error code.
            }
        } catch (MalformedURLException e) {
            // ...
        } catch (IOException e) {
            // ...
        }
	}
	
	public void run() {
		// we'll break on interrupts
		try {
			while (true) {
				Base.logger.info("awake!");
				if (Base.getMachineLoader().isLoaded()) {
					double toolTemp = Base.getMachineLoader().getDriver().getTemperature();
					double bedTemp = Base.getMachineLoader().getDriver().getPlatformTemperature();
					sendTemperatureData(toolTemp, bedTemp);
				}
				Thread.sleep(intervalS * 1000);
			}
		} catch (InterruptedException e) {
		}
	}
}
