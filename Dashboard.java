// Author : Merlin Prasad
//Student number : 19333557
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import tcdIO.*;

/**
 * Dashboard class
 * An instance accepts user input
 */
public class Dashboard extends Node {
	static final int DEFAULT_SRC_PORT = 50002;
	static final int DEFAULT_DST_PORT = 51510;
	static final String DEFAULT_DST_NODE = "broker";
	static final String DASHBOARD_ID = "02";
	InetSocketAddress dstAddress;
	Terminal terminal;

	static final int ROOM_TEMP_MAX = 22 ;
	static final int ROOM_TEMP_MIN = 18 ;

	static final int ROOM_LUX_MAX = 100 ;
	static final int ROOM_LUX_MIN = 500 ;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for
	 * the destinations
	 */
	Dashboard(Terminal terminal, String dstHost, int dstPort, int srcPort) {
		try {
			this.terminal = terminal;
			dstAddress = new InetSocketAddress(dstHost, dstPort);
			socket = new DatagramSocket(srcPort);
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dashboard will recieve both Subscriber (Sub) packets and ACKs.
	 * If Dashboard is publishing or subscribing it recieves an ACK from Broker to show this was succefull
	 * If Dashboard is subscribed to Sensors it will get packets from the relevant sensors.
	 * Information on whether these readings are suitable is also supplied to terminal
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		PacketContent content = PacketContent.fromDatagramPacket(packet);

		terminal.println("Dashboard recieved the following packet:");
		terminal.println(content.toString());

		if(content.getDeviceInfo().equalsIgnoreCase("sens"))
		{
			if(content.getTopicInfo().equalsIgnoreCase("temp"))
			{
				String temp = content.getPacketInfo() ;
				int val = Integer.parseInt(temp);

				if(val > ROOM_TEMP_MAX )
				{
					terminal.println("Sensor " + content.getIDInfo() + " value reading of " + val + "C is hot");
				}
				else if (val < ROOM_TEMP_MIN)
				{
					terminal.println("Sensor " + content.getIDInfo() + " value reading of " + val + "C is cold");
				}
				else{
					terminal.println("Sensor " + content.getIDInfo() + " value reading of " + val + "C is optimum room temp");
				}
			}
			else if(content.getTopicInfo().equalsIgnoreCase("light"))
			{
				String lux = content.getPacketInfo() ;
				int val = Integer.parseInt(lux);

				if(val > ROOM_LUX_MAX )
				{
					terminal.println("Sensor " + content.getIDInfo() + " value reading of " + val + "lux is intensly bright");
				}
				else if (val < ROOM_LUX_MIN)
				{
					terminal.println("Sensor " + content.getIDInfo() + " value reading of " + val + "lux is dark");
				}
				else{
					terminal.println("Sensor " + content.getIDInfo() + " value reading of " + val + "lux is good lighting");
				}
			}
		}
	}

	/**
	 * Dashboard will either send Pub or Sub packets to the Broker on startup
	 * It accepts user input which it encrpts into a packet to send the the Broker
	 */
	public synchronized void start() throws Exception {

		terminal.println("Dashboard is ready. ");
		DatagramPacket packet;

		String userInput;
		userInput = terminal.readString("Type sub, pub or quit to exit: ");

		boolean finished = false;
		while (!finished) {

			if(userInput.equalsIgnoreCase("quit")){ 
				finished = true ;
			}
			else if (userInput.equalsIgnoreCase("sub")){
				String topicName;

				boolean sub = true ;
				while (sub = true){

				topicName = terminal.readString("Select topic to subscribe to: ");

				if(topicName.length() < 6)
				{
					packet = new SendPacketContent("Sub","dash","pc","",topicName,DASHBOARD_ID ).toDatagramPacket();
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					terminal.println("Dashboard subscribed to :" + topicName);
					this.wait();
				}
				else{
					terminal.println("Error .Topic is too long. Please select a valid topic such as temp or subtopic like room or ID");
				}
			}
		}
			else if (userInput.equalsIgnoreCase("pub")){
				String topicName;
				String instruction ;

				boolean pub = true ;
				while (pub = true){

				topicName = terminal.readString("Select topic to publish to: ");	
				instruction = terminal.readString("Type in instruction to send to subscribers: ");

				if(instruction.length() < 6 && topicName.length() < 6)
				{
					packet = new SendPacketContent("Pub","dash","pc",instruction,topicName,DASHBOARD_ID ).toDatagramPacket();
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					terminal.println("Dashboard has published to:" + topicName);
					this.wait();
				}
				else{
					terminal.println("Error . Instruction or Topic inputs are too long ");
				}
			 }
			}
			else {
				terminal.println("Error . Not a valid input.");
				userInput = terminal.readString("Type sub, pub or quit to exit: ");
			}
		}
	}

	/**
	 * Recieve a packet from a given address
	 */
	public static void main(String[] args) {
		try {
			Terminal terminal = new Terminal("Dashboard");
			(new Dashboard(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}
