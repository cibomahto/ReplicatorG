/**
 * 
 */
package replicatorg.drivers;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.w3c.dom.Node;

import replicatorg.app.Base;
import replicatorg.app.tools.XML;
import replicatorg.app.util.serial.SerialFifoEventListener;

/**
 * @author Matt Mets
 *
 */
public class SocketDriver extends DriverBaseImplementation implements UsesSocket {

	protected Socket socket;
	
	private String hostName;
	private int portNumber;

    private boolean explicit = false;
	
    /** Lock for multi-threaded access to this driver's serial port. */
	private final ReentrantReadWriteLock serialLock = new ReentrantReadWriteLock();
	/** Locks the serial object as in use so that it cannot be disposed until it is 
	 * unlocked. Multiple threads can hold this lock concurrently. */
	public final ReadLock serialInUse = serialLock.readLock();
	
    protected SocketDriver() {
    	hostName = Base.preferences.get("socket.hostname", null);
    	portNumber = Base.preferences.getInt("socket.portnumber",2000);
    }
    
	public void loadXML(Node xml) {
		super.loadXML(xml);
        // load from our XML config, if we have it.
        if (XML.hasChildNode(xml, "hostname")) {
            hostName = XML.getChildNodeValue(xml, "hostname");
            explicit = true;
        }
        if (XML.hasChildNode(xml, "portnumber"))
            portNumber = Integer.parseInt(XML.getChildNodeValue(xml, "portnumber"));
	}

// diplo1d: see this fix by kintel that conflicted when merging. You also fixed it but differently:
// https://github.com/makerbot/ReplicatorG/commit/899c7c8e059d986ac0744ca4ab1f2f44efaae4b5
// your fix:
//	public void setSerial(Serial serial) {
//		serialLock.writeLock().lock();
// kintel had just changed it to "synchronized".
	public synchronized void setSocket(Socket socket) {
		serialLock.writeLock().lock();
		if (this.socket == socket)
		{
			serialLock.writeLock().unlock();
			return;
		}
		if (this.socket != null) {
			synchronized(this.socket) {
				try {
					this.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.socket = null;
			}
		}
		setInitialized(false);
		this.socket = socket;

		// asynch option: the serial port forwards all received data in FIFO format via 
		// serialByteReceivedEvent if the driver implements SerialFifoEventListener.
//		if (this instanceof SerialFifoEventListener && socket != null)
//			serial.listener.set( (SerialFifoEventListener) this );
		// TODO: Implement this.

		serialLock.writeLock().unlock();
	}
	

	public Socket getSocket() { return socket; }
	
	public String getHostName() {
		return hostName;
	}
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public void dispose() {
		serialLock.writeLock().lock();
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket = null;
		
		serialLock.writeLock().unlock();
	}

	@Override
	public boolean isExplicit() { return explicit; }
}
