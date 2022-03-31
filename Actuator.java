// Author : Merlin Prasad :)
//Student number : 19333557
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import tcdIO.*;

/**
 * Actuator class
 * An instance accepts user input
 */
public class Actuator extends Node {
	static final int DEFAULT_SRC_PORT = 50004;
	static final int DEFAULT_DST_PORT = 50001;
	static final String DEFAULT_DST_NODE = "broker";
	InetSocketAddress dstAddress;
	Terminal terminal;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for
	 * the destinations
	 */
	Actuator(Terminal terminal, String dstHost, int dstPort, int srcPort) {
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
	 * Assume that incoming packets contain a String and print the string.
	 * The incoming packet is either ACK from Broker or instrcutions from the dashboard
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		PacketContent content = PacketContent.fromDatagramPacket(packet);

		terminal.println("Actuator recieved the following packet :" + content.toString());

		if(content.getDeviceInfo().equalsIgnoreCase("dashboard"))
		{
			terminal.println(content.getTopicInfo() + " actuator recieved the following instruction: " + content.getPacketInfo());
		}
	}

	/**
	 * Sender Method
	 * Actuator will send Subscription requests to the Broker with information on that specific actuator like its current room,value,topic and ID. 
	 */
	public synchronized void start() throws Exception {

		terminal.println("Actuator is started .");

		String userInput;
		userInput = terminal.readString("Which rooms actuators would you like to subscribe? (or quit to exit)");

		DatagramPacket packet;

		boolean finished = false;

		if(userInput.equalsIgnoreCase("bedroom")|| userInput.equalsIgnoreCase("bed")){
			//assume there are two actuators in each room one for controlling light and the other temperature
			packet = new SendPacketContent("Sub","act","bed","10","temp","07").toDatagramPacket();
			packet.setSocketAddress(dstAddress);
			socket.send(packet);

			packet = new SendPacketContent("Sub","act","bed","25","light","08").toDatagramPacket();
			packet.setSocketAddress(dstAddress);
			socket.send(packet);
		}
		else if (userInput.equalsIgnoreCase("kitchen") || userInput.equalsIgnoreCase("kit")){

			packet = new SendPacketContent("Sub","act","bed","90","temp","09").toDatagramPacket();
			packet.setSocketAddress(dstAddress);
			socket.send(packet);

			packet = new SendPacketContent("Sub","act","kit","50","light","10").toDatagramPacket();
			packet.setSocketAddress(dstAddress);
			socket.send(packet);
		}
		else if(userInput.equalsIgnoreCase("quit")){ 
			finished = true;
		}
		else {
			terminal.println("Error .Please select a room in the house i.e kitchen or bedroom");
			userInput = terminal.readString("Which rooms actuators would you like to subscribe? (or quit to exit)");
		}

		terminal.println("Actuator subscribed to Broker" );
		this.wait();
	}

	/**
	 * Test method
	 *
	 * Recieve a packet from a given address
	 */
	public static void main(String[] args) {
		try {
			Terminal terminal = new Terminal("Actuator");
			(new Actuator(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			System.out.println("Program completed");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}

