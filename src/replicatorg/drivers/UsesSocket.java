package replicatorg.drivers;

import java.net.Socket;

public interface UsesSocket {
	
	public void setSocket(Socket socket);
	
	public Socket getSocket();
	
	public String getHostName();
	
	public int getPortNumber();
	
	// Indicates that the serial port is explicitly specified in machines.xml
	public boolean isExplicit();
}
