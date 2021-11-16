// Author : Merlin Prasad
//Student number : 19333557

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import tcdIO.*;

/**
 * Endpoint class
 * An instance accepts user input
 */
public class Endpoint extends Node {
	static final int DEFAULT_SRC_PORT = 53521; //come back to this 
	static final int DEFAULT_DST_PORT = 51510;
	static final String DEFAULT_DST_NODE = "router";
	InetSocketAddress dstAddress;
	Terminal terminal;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for
	 * the destinations
	 */
	Endpoint(Terminal terminal, String dstHost, int dstPort, int srcPort) {
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

		terminal.println("Endpoint received the following packet:");
		terminal.println(content.toString());

		terminal.println(content.getPacketInfo());

	}

	/**
	 * Dashboard will either send Pub or Sub packets to the Broker on startup
	 * It accepts user input which it encrpts into a packet to send the the Broker
	 */
	public synchronized void start() throws Exception {

		terminal.println("Endpoint is ready. ");
		DatagramPacket packet;

		String userInput;
		userInput = terminal.readString("Type recieve or send or quit to exit: ");

		boolean finished = false;
		while (!finished) {

			if(userInput.equalsIgnoreCase("quit")){ 
				finished = true ;
			}
			else if (userInput.equalsIgnoreCase("recieve")){
				String topicName;

				boolean recieve = true ;
				while (recieve = true){
					terminal.println("Endpoint is waiting to recieve packets from host :");
					this.wait();
			}
		}
			else if (userInput.equalsIgnoreCase("send")){
				boolean send = true ;
				while (send = true){
				
					//types
					//1 - network ID 
					//3 - container names
					//4 - combination
																		//info        topic
					//	packet = new SendPacketContent("send","ep","pc","endpoint","endpointB","89").toDatagramPacket();
					//decode 4-combin 2-length 3-container 9- length V-value

					packet = new TLVPacketContent("423","9","endpointB","17trinity").toDatagramPacket(); 
						
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
					terminal.println("Host has published to endpointB:");
					this.wait();
				}
			  }
			else {
				terminal.println("Error . Not a valid input.");
				userInput = terminal.readString("Type send, recieve or quit to exit: ");
			}
		}
	}

	/**
	 * Recieve a packet from a given address
	 */
	public static void main(String[] args) {
		try {
			Terminal terminal = new Terminal("Endnode");
			(new Endpoint(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}

