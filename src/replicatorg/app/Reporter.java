package replicatorg.app;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import replicatorg.drivers.Driver;

public class Reporter implements Runnable {

	long intervalS = 10;
	
	public void sendStatus(Driver driver) {
		String postURL = "http://10.0.0.83:8888/upload";
		Base.logger.info("Sending status");
		
		String message = 
	    "machinename=" + Base.getMachineLoader().getMachine().getMachineName()
	    + "&jobname=" + Base.getEditor().build.getName()
	    + "&status=" + Base.getMachineLoader().getMachine().getMachineState().getState().toString()
    	+ "&extrudertemp=" + Double.toString(driver.getTemperature())
    	+ "&platformtemp=" + Double.toString(driver.getPlatformTemperature());
		
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
					sendStatus(Base.getMachineLoader().getDriver());
				}
				Thread.sleep(intervalS * 1000);
			}
		} catch (InterruptedException e) {
		}
	}
}