// Author : Merlin Prasad
//Student number : 19333557
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import tcdIO.Terminal;
import java.util.*;

// import javax.net.ssl.HostnameVerifier;



//Router class handles the management of sending packets to other routers
public class Router extends Node {
	static final int DEFAULT_PORT = 51510;

	static final int ENDPOINTB = 53521 ;

	Terminal terminal;
	InetSocketAddress dstAddress;

	String routerName ;

	
	HashMap<String, String> forwardingTable = new HashMap<String, String>();

	Router(Terminal terminal,int port) {
		try {
			this.terminal= terminal;
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	public synchronized void start() throws Exception {
	boolean finished = false;
		while(!finished){
			routerName = terminal.readString("Type in a router name to launch or quit to exit ");
				
				if(routerName.equalsIgnoreCase("A") || routerName.equalsIgnoreCase("B") || routerName.equalsIgnoreCase("c") ||routerName.equalsIgnoreCase("d")){
					terminal.println("Router: " + routerName + " is ready and waiting for contact");
					this.wait();
				}
				else if(routerName.equalsIgnoreCase("quit"))
				{
					finished = true ;
				}
				terminal.println("Error" + routerName + "not a valid router name");
    	 }
	}

	/**
	 * The Router will be handling multiple incoming packets
	 * Send Ackknowledgements to tell the device it was sucessfull 
	 * Forward on other Routers or Endpoints p.
	 */

	public void onReceipt(DatagramPacket packet) {
		try {
			PacketContent content= PacketContent.fromDatagramPacket(packet);

			

			//Router recieved following packet
			terminal.println(content.toString());

			if(routerName.equalsIgnoreCase("A")) //A is the gateway router
			{
				//send an ACK
					DatagramPacket response;
					response= new AckPacketContent("Router ACK").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
					terminal.println("Router send acknowledgment ");

					forwardingTable.put("endpoint", "endpoint");
					forwardingTable.put("endpointB", "routerD");
					forwardingTable.put("router", "-");
					forwardingTable.put("routerB", "routerD");
					forwardingTable.put("routerC", "routerD");
					forwardingTable.put("routerD", "routerD");
			}
			else if(routerName.equalsIgnoreCase("B"))
			{
					

					forwardingTable.put("endpoint", "routerD");
					forwardingTable.put("endpointB", "endpointB");
					forwardingTable.put("router", "routerD");
					forwardingTable.put("routerB", "-");
					forwardingTable.put("routerC", "routerC");
					forwardingTable.put("routerD", "routerD");
			}
			else if(routerName.equalsIgnoreCase("C"))
			{
			
					forwardingTable.put("endpoint", "routerD");
					forwardingTable.put("endpointB", "routerB");
					forwardingTable.put("router", "routerD");
					forwardingTable.put("routerB", "routerB");
					forwardingTable.put("routerC", "-");
					forwardingTable.put("routerD", "routerD");
			}
			else if(routerName.equalsIgnoreCase("D"))
			{
				forwardingTable.put("endpoint", "router");
				forwardingTable.put("endpointB", "routerB");
				forwardingTable.put("router", "router");
				forwardingTable.put("routerB", "routerB");
				forwardingTable.put("routerC", "routerC");
				forwardingTable.put("routerD", "-");
				
			}
			
					String nextDest = content.getV() ;
					String nextContainer = forwardingTable.get(nextDest);
				
					terminal.println("Quickest route to endpointB is through: "+ nextContainer); 
					
					int destPort = DEFAULT_PORT ;

					if(nextContainer.equalsIgnoreCase("endpointB"))
					{
						destPort = ENDPOINTB ;
					}
				    	dstAddress= new InetSocketAddress(nextContainer, destPort);
						packet.setSocketAddress(dstAddress);
						socket.send(packet);
						terminal.println("Packet sent by router");
						
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public static void main(String[] args) {
		try {
			Terminal terminal= new Terminal("Router");
			(new Router(terminal,DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
